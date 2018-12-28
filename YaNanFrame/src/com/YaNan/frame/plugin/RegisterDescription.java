package com.YaNan.frame.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.beans.BeanContext;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.InvokeHandlerSet;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.ClassHelper;
import com.YaNan.frame.reflect.cache.ClassInfoCache;
import com.typesafe.config.Config;

/**
 * 组件描述类 用于创建组件时的组件信息 v1.0 支持通过Class的Builder方式 v1.1 支持通过Comp文件的Builder方式 v1.2
 * 支持创建默认的Builder方式 v1.3 支持描述器的属性 v1.4 将InvokeHandler的创建迁移到组件初始化时，大幅度提高代理执行效率
 * v1.5 20180910 重新构建InvokeHandler的逻辑，提高aop的效率 v1.6 20180921
 * 添加FieldHandler和ConstructorHandler 实现方法拦截与构造器拦截
 * 
 * 
 * @author yanan
 *
 */
public class RegisterDescription {
	private Map<Integer, Object> proxyContainer;
	/**
	 * 组件类
	 */
	private Class<?> clzz;
	/**
	 * 类加载器
	 */
	private ClassLoader loader;
	/**
	 * 注册注解 通过注解注册时有效
	 */
	private Register register;
	/**
	 * 组件接口类，普通类（为实现服务接口的类）无效
	 */
	private Class<?>[] plugs;
	/**
	 * 优先级，值越大，优先级越低
	 */
	private int priority = 0;
	/**
	 * 属性，此属性为匹配属性
	 */
	private String[] attribute = { "*" };
	/**
	 * 是否为单例模式
	 */
	private boolean signlton;
	/**
	 * 组件文件，当组件用文件注册时有效
	 */
	private File file;
	/**
	 * 属性值，当组件用文件注册时有效
	 */
	private Properties properties;
	/**
	 * 描述，用于组件说明等
	 */
	private String description = "";
	/**
	 * 代理方式
	 */
	private ProxyModel proxyModel = ProxyModel.DEFAULT;
	/**
	 * 方法拦截器 映射
	 */
	private Map<Method, InvokeHandlerSet> handlerMapping;
	/**
	 * Config
	 */
	private Config config;

	public ClassLoader getLoader() {
		return loader;
	}

	public Properties getProperties() {
		return properties;
	}

	public ProxyModel getProxyModel() {
		return proxyModel;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * 构造器拦截器
	 */
	private Map<Constructor<?>, InvokeHandlerSet> constructorMapping;
	/**
	 * 一个容器，可用于记录，查询该注册器的相关数据
	 */
	private Map<String, Object> attributes;
	/**
	 * 域参数
	 */
	private Map<Field, FieldDesc> fieldParam;

	public ClassLoader getProxyClassLoader() {
		return loader;
	}

	/**
	 * 支持注解类型的构造器 register为注解 clzz为注册器的类名 impls 为注册器实现的接口
	 * 
	 * @param register
	 * @param clzz
	 * @param impls
	 * @throws Exception
	 */
	public RegisterDescription(Register register, Class<?> clzz) {
		this.loader = new ClassLoader(register.declare().equals(Object.class) ? clzz : register.declare(), false);
		this.clzz = loader.getLoadedClass();
		this.register = register;
		this.plugs = register.register().length == 0 ? this.clzz.getInterfaces() : register.register();
		this.priority = register.priority();
		this.signlton = register.signlTon();
		this.attribute = register.attribute();
		this.description = register.description();
		this.proxyModel = register.model();
		checkPlugs(this.plugs);
		PlugsFactory.getInstance().addRegisterHandlerQueue(this);
	}

	private void createProxyContainer() {
		if (this.proxyContainer == null)
			synchronized (this) {
				if (this.proxyContainer == null)
					this.proxyContainer = new HashMap<Integer, Object>();
			}
	}

	public void setAttribute(String name, Object value) {
		if (this.attributes == null)
			this.attributes = new HashMap<String, Object>();
		this.attributes.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return this.attribute == null ? null : (T) this.attributes.get(name);
	}

	public RegisterDescription(Class<?> clzz) {
		// 读取属性
		this.priority = Integer.MAX_VALUE;
		this.signlton = false;
		this.description = "default register description :" + clzz.getName();
		// 获取实现类
		this.loader = new ClassLoader(clzz, false);
		this.clzz = loader.getLoadedClass();
		// 获取实现类所在的接口
		this.plugs = clzz.getInterfaces();
		PlugsFactory.getInstance().addRegisterHandlerQueue(this);
	}

	public RegisterDescription(File file) throws Exception {
		try {
			this.setFile(file);
			String fileName = file.getName();
			String clzzStr = fileName.substring(0, fileName.lastIndexOf("."));
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			this.properties = new Properties();
			properties.load(in);
			// 读取属性
			this.priority = Integer.valueOf(properties.getProperty("comps.priority", "0"));
			this.signlton = Boolean.valueOf(properties.getProperty("comps.signlton", "true"));
			this.attribute = properties.getProperty("comps.attribute", "*").split(",");
			this.description = properties.getProperty("comps.description", "");
			String className = properties.getProperty("comps.class", clzzStr);
			String declareRegister = properties.getProperty("comps.register", "*");
			String model = properties.getProperty("comps.model", "DEFAULT");
			this.proxyModel = ProxyModel.getProxyModel(model);
			// 获取实现类
			this.loader = new ClassLoader(className, false);
			this.clzz = loader.getLoadedClass();
			String fieldTag = "comps.field.";
			// 遍历需要赋值的属性
			Iterator<java.util.Map.Entry<Object, Object>> entryIterator = properties.entrySet().iterator();
			while (entryIterator.hasNext()) {
				java.util.Map.Entry<Object, Object> entry = entryIterator.next();
				String key = (String) entry.getKey();
				int index = key.indexOf(fieldTag);
				if (index > -1) {
					String fieldName = key.substring(index + fieldTag.length());
					String type = "DEFAULT";
					int andex = fieldName.indexOf("@");
					if (andex > -1) {
						type = fieldName.substring(andex + 1);
						fieldName = fieldName.substring(0, andex);
					}
					String value = (String) entry.getValue();
					Field field = loader.getDeclaredField(fieldName);
					if (field == null)
						throw new Exception("Field \"" + fieldName + "\" is not exist at plug class" + clzz.getName());
					if (this.fieldParam == null)
						this.fieldParam = new HashMap<Field, FieldDesc>();
					this.fieldParam.put(field, new FieldDesc(type, value, field));
				}
			}
			// 获取实现类所在的接口
			this.plugs = getPlugs(clzz, declareRegister);
			if (this.plugs == null || this.plugs.length == 0)
				throw new Exception("register " + clzz.getName() + " not implements any interface");
			checkPlugs(this.plugs);
			PlugsFactory.getInstance().addRegisterHandlerQueue(this);
		} catch (Exception e) {
			if (PluginAppincationContext.isWebContext())
				throw new Exception("plugin " + file.getName() + " init failed", e);
		}
	}

	public RegisterDescription(Config config) throws Exception {
		try {
			config.allowKeyNull(true);
			this.config = config;
			// 读取属性
			String className = config.getString("class");
			String ref = config.getString("ref");
			if (className == null && ref == null)
				throw new RuntimeException("could not fond class property and no reference any at \""
						+ config.origin().url() + "\" at line : " + config.origin().lineNumber());
			if (className != null) {
				this.loader = new ClassLoader(className, false);
				this.clzz = loader.getLoadedClass();
				this.priority = config.getInt("priority", 0);
				this.signlton = config.getBoolean("signlton", true);
				this.attribute = config.getString("attribute", "*").split(",");
				this.description = config.getString("description", "");

				String model = config.getString("model", "DEFAULT");
				this.proxyModel = ProxyModel.getProxyModel(model);
				// 获取实现类
				if (config.isConfigList("field")) {
					List<? extends Config> fields = config.getConfigList("field");
					for (Config field : fields)
						this.configField(field);
				} else {
					Config field = config.getConfig("field");
					if (field != null)
						this.configField(field);
				}
				// 获取实现类所在的接口
				String declareRegister = config.getString("service", "*");
				this.plugs = getPlugs(clzz, declareRegister);
				// if (this.plugs == null || this.plugs.length == 0)
				// throw new Exception("register " + clzz.getName() + " not
				// implements any interface");
				checkPlugs(this.plugs);
			}
			PlugsFactory.getInstance().addRegisterHandlerQueue(this);
		} catch (Exception e) {
			if (PluginAppincationContext.isWebContext())
				throw new Exception("plugin exception init at \"" + config.origin().url() + "\" at line "
						+ config.origin().lineNumber(), e);
		}
	}

	/**
	 * 处理参数中Field字段
	 * 
	 * @param field
	 */
	private void configField(Config conf) {
		ParamDesc paraDesc = this.getParameterDescription(conf);
		if (paraDesc.getName() == null)
			throw new RuntimeException("plugins property field name is null at \"" + conf.origin().url() + "\" at line "
					+ conf.origin().lineNumber());
		Field field = loader.getDeclaredField(paraDesc.getName());
		if (field == null)
			throw new RuntimeException("Field \"" + paraDesc.getName() + "\" is not exist at plug class "
					+ clzz.getName() + " at \"" + conf.origin().url() + "\" at line " + conf.origin().lineNumber());
		if (this.fieldParam == null)
			this.fieldParam = new HashMap<Field, FieldDesc>();
		this.fieldParam.put(field, new FieldDesc(paraDesc.getType(), paraDesc.getValue(), field));
	}

	private ParamDesc getParameterDescription(Config conf) {
		conf.allowKeyNull(true);
		String fieldName = null;
		String value = null;
		String type = null;
		if (conf.hasPath("name")) {
			fieldName = conf.getString("name");
			type = conf.getString("type", "DEFAULT");
			value = conf.getString("value");
		} else {
			Iterator<Entry<String, Config>> iterator = conf.configEntrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Config> entry = iterator.next();
				fieldName = entry.getKey();
				Config config = entry.getValue();
				config.allowKeyNull(true);
				Iterator<Entry<String, Object>> objectIterator = config.simpleObjectEntrySet().iterator();
				while (objectIterator.hasNext()) {
					Entry<String, Object> object = objectIterator.next();
					type = object.getKey();
					value = (String) object.getValue();
					continue;
				}
			}
			Iterator<Entry<String, Object>> objectIterator = conf.simpleObjectEntrySet().iterator();
			while (objectIterator.hasNext()) {
				Entry<String, Object> object = objectIterator.next();
				fieldName = object.getKey();
				value = (String) object.getValue();
				continue;
			}
		}
		return new ParamDesc(type, value, fieldName);
	}

	public void initHandler() {
		handlerMapping = new HashMap<Method, InvokeHandlerSet>();
		if (plugs != null)
			for (Class<?> interfacer : plugs) {
				this.initHandlerMapping(interfacer);
			}
		if (this.clzz != null) {
			this.initHandlerMapping(this.clzz);
			this.initConstructorHandlerMapping(this.clzz);
			this.initFieldHandlerMapping();
		}
		if (this.config != null) {
			// 配置文件专用
			String id = config.getString("id");
			if (id != null) {
				Object instance = null;
				// Parmerter[] params ;
				// ParamDesc param;
				// //判断是否有参数
				// if(config.hasPath("args")){
				// //单个参数
				// if(!config.isList("args")){
				// if(config.getType("args") == ConfigValueType.OBJECT){
				// param = new ParamDesc()
				// }else{
				// param = new ParamDesc(null,config.getString("args"), null);
				// }
				// }else{
				//
				// }
				// System.out.println(config.isList("args"));
				// String sParam = config.getString("args");
				// if(sParam!=null){
				// System.out.println(sParam);
				// }else{
				// }
				// //获取参数描述
				//
				//// ParamDesc param =
				// this.getParameterDescription(config.getConfig("args"));
				// }
				Object ref = null;
				String refConf = config.getString("ref");
				if (refConf != null) {
					ref = PlugsFactory.getBean(refConf);
					this.loader = new ClassLoader(ref.getClass(), false);
					this.clzz = loader.getLoadedClass();
					try {
						RegisterDescription register = PlugsFactory.getBeanRegister(ref);
						this.priority = config.getInt("priority", register.priority);
						this.signlton = config.getBoolean("signlton", register.signlton);
						String attr = config.getString("attribute");
						if (attr == null)
							this.attribute = register.attribute;
						else
							this.attribute = attr.split(",");
						this.description = config.getString("description", register.description);
						String model = config.getString("model");

						this.proxyModel = ProxyModel.getProxyModel(model);
						// 获取实现类
						this.fieldParam = register.fieldParam;
						// 获取实现类所在的接口
						this.plugs = register.plugs;
						this.fieldParam = register.fieldParam;
					} catch (Throwable t) {
						this.priority = config.getInt("priority", 0);
						this.signlton = config.getBoolean("signlton", true);
						this.attribute = config.getString("attribute", "*").split(",");
						this.description = config.getString("description", "");

						String model = config.getString("model", "DEFAULT");
						this.proxyModel = ProxyModel.getProxyModel(model);
						// 获取实现类
						if (config.isConfigList("field")) {
							List<? extends Config> fields = config.getConfigList("field");
							for (Config field : fields)
								this.configField(field);
						} else {
							Config field = config.getConfig("field");
							if (field != null)
								this.configField(field);
						}
						// 获取实现类所在的接口
						String declareRegister = config.getString("service", "*");
						this.plugs = getPlugs(clzz, declareRegister);
					}
					// if (this.plugs == null || this.plugs.length == 0)
					// throw new Exception("register " + clzz.getName() + " not
					// implements any interface");
					this.initHandlerMapping(this.clzz);
					this.initConstructorHandlerMapping(this.clzz);
					this.initFieldHandlerMapping();
					checkPlugs(this.plugs);
				}
				try {
					instance = this.getNewBean();
					PlugsFactory.addBeanRegister(instance, this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				// if(this.plugs!=null){
				// for (Class<?> cl : this.plugs) {
				// PlugsHandler.newMapperProxy(cl, this, instance);
				// }
				// }
				if (this.fieldParam != null) {
					for (FieldDesc desc : this.fieldParam.values()) {
						desc.getFieldHandler().preparedField(this, null, instance, desc, null);
					}
				}
				if (this.config.hasPath("init")) {
					try {
						Method method;
						if (this.config.isList("init")) {
							List<String> methods = this.config.getStringList("init");
							for (String methodStr : methods) {
								method = ClassHelper.getClassHelper(clzz).getDeclaredMethod(methodStr);
								if (method == null)
									throw new RuntimeException("init invoke method \"" + methodStr
											+ "\" could not be found at class" + clzz);
								method.invoke(instance);
							}
						} else {
							String methodStr = this.config.getString("init");
							method = ClassHelper.getClassHelper(clzz).getDeclaredMethod(methodStr);
							if (method == null)
								throw new RuntimeException(
										"init invoke method \"" + methodStr + "\" could not be found at class" + clzz);
							method.invoke(instance);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				BeanContext.getContext().addBean(id, instance);
			}
		}
	}

	private Object getNewBean() throws Exception {
		String refConf = config.getString("ref");
		if (refConf != null) {
			Object ref = PlugsFactory.getBean(refConf);
			if (config.hasPath("method")) {
				String methodStr = config.getString("method");
				Method method = ClassHelper.getClassHelper(clzz).getDeclaredMethod(methodStr);
				if (method == null)
					throw new RuntimeException("method \"" + methodStr + "\" is not exists");
				return method.invoke(ref);
			} else {
				System.out.println(refConf);
				return PlugsFactory.getBeanRegister(refConf).getNewBean();
			}
		} else {
			if (config.hasPath("method")) {
				String methodStr = config.getString("method");
				Method method = ClassHelper.getClassHelper(clzz).getDeclaredMethod(methodStr);
				if (method == null)
					throw new RuntimeException("method \"" + methodStr + "\" is not exists");
				return method.invoke(null);
			} else {
				return this.getRegisterNewInstance(null);
			}
		}
	}

	private void initFieldHandlerMapping() {
		Field[] fields = this.clzz.getDeclaredFields();
		for (Field field : fields) {
			try {
				FieldDesc desc = null;
				if (this.fieldParam != null)
					desc = this.fieldParam.get(field);
				if (desc == null) {
					desc = this.getSupportField(field);
				} else {
					desc = this.getSupportField(desc);
				}
				if (desc != null) {
					if (this.fieldParam == null)
						this.fieldParam = new HashMap<Field, FieldDesc>();
					this.fieldParam.put(field, desc);
				} else {
					if (this.fieldParam != null)
						this.fieldParam.remove(field);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private FieldDesc getSupportField(FieldDesc desc) throws Exception {
		if (desc != null) {
			Plug cplug = PlugsFactory.getPlug(FieldHandler.class);
			FieldHandler handler = null;
			List<RegisterDescription> registerList = cplug
					.getRegisterDescriptionListByAttribute(clzz.getName() + "." + desc.getField().getName());
			outer: for (int i = 0; i < registerList.size(); i++) {
				RegisterDescription register = registerList.get(i);
				Class<?> registerClass = register.getRegisterClass();
				Support support = registerClass.getAnnotation(Support.class);
				if (support != null)
					for (Class<? extends Annotation> supp : support.value()) {
						if (desc.getType().equals(supp.getSimpleName())) {
							handler = registerList.get(i).getRegisterInstance(FieldHandler.class);
							break outer;
						}
					}
			}
			if (handler != null) {
				desc.setFieldHandler(handler);
				return desc;
			}
		}
		return null;
	}

	private FieldDesc getSupportField(Field field) throws Exception {
		Plug cplug = PlugsFactory.getPlug(FieldHandler.class);
		List<RegisterDescription> registerList = cplug
				.getRegisterDescriptionListByAttribute(clzz.getName() + "." + field.getName());
		Annotation[] annos = field.getAnnotations();
		Annotation annotation = null;
		FieldHandler handler = null;
		FieldDesc fieldDesc = null;
		outer: for (int i = 0; i < registerList.size(); i++) {
			RegisterDescription register = registerList.get(i);
			Class<?> registerClass = register.getRegisterClass();
			Support support = registerClass.getAnnotation(Support.class);
			if (support != null)
				for (Annotation anno : annos) {
					for (Class<? extends Annotation> supp : support.value()) {
						if (anno.annotationType().equals(supp)) {
							annotation = anno;
							handler = register.getRegisterInstance(FieldHandler.class);
							break outer;
						}
					}
				}
		}
		if (handler != null) {
			fieldDesc = new FieldDesc(field.getName(), null, field);
			fieldDesc.setFieldHandler(handler);
			fieldDesc.setAnnotation(annotation);
		}
		return fieldDesc;
	}

	private void initConstructorHandlerMapping(Class<?> clzz) {
		Constructor<?>[] constructors = clzz.getConstructors();
		// 获取所有的构造器的拦截器
		Plug cplug = PlugsFactory.getPlug(InstanceHandler.class);
		if (cplug != null) {
			// 找出满足当前类的拦截器
			List<RegisterDescription> registerList = cplug.getRegisterDescriptionListByAttribute(clzz.getName());
			for (Constructor<?> construecotr : constructors) {
				InvokeHandlerSet ihs = null;
				if (this.constructorMapping != null)
					ihs = this.constructorMapping.get(construecotr);
				Map<Class<?>, Object> annos = null;
				for (int i = 0; i < registerList.size(); i++) {
					InstanceHandler handler = null;
					RegisterDescription register = registerList.get(i);
					Class<?> registerClass = register.getRegisterClass();
					// 获取支持的注解
					Support support = registerClass.getAnnotation(Support.class);
					try {
						if (support == null) {
							handler = register.getRegisterInstance(InstanceHandler.class);
						} else {
							Class<? extends Annotation>[] supportClass = support.value();
							for (Class<? extends Annotation> supportClzz : supportClass) {
								// 对比注解
								Annotation anno;
								// 从构造器获取注解
								anno = construecotr.getAnnotation(supportClzz);
								if (anno != null) {
									annos = this.addHanslerAnnotation(annos, anno);
									handler = register.getRegisterInstance(InstanceHandler.class);
								}
								if (handler == null) {
									Parameter[] parameters = construecotr.getParameters();
									for (Parameter parameter : parameters) {
										if ((anno = parameter.getAnnotation(supportClzz)) != null) {
											annos = this.addHanslerAnnotation(annos, anno);
											handler = register.getRegisterInstance(InstanceHandler.class);
										}
									}
								}
								if (handler == null) {
									anno = clzz.getAnnotation(supportClzz);
									if (anno != null) {
										annos = this.addHanslerAnnotation(annos, anno);
										handler = register.getRegisterInstance(InstanceHandler.class);
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (handler != null) {
						if (ihs == null) {
							ihs = new InvokeHandlerSet(handler);
							ihs.setAnnotations(annos);
							if (constructorMapping == null)
								constructorMapping = new HashMap<Constructor<?>, InvokeHandlerSet>();
							constructorMapping.put(construecotr, ihs);
						} else {
							InvokeHandlerSet ihn = new InvokeHandlerSet(handler);
							ihn.setAnnotations(annos);
							ihs.getLast().addInvokeHandlerSet(ihn);
						}
					}
				}
			}
		}

	}

	private void initHandlerMapping(Class<?> clzz) {
		// 如果不是接口
		if (clzz != null) {
			// 准备方法拦截器
			Method[] methods = clzz.getMethods();
			for (Method method : methods) {
				InvokeHandlerSet ihs = handlerMapping.get(method);
				Plug plug = PlugsFactory.getPlug(InvokeHandler.class);
				if (plug != null) {
					List<RegisterDescription> registerList = plug
							.getRegisterDescriptionListByAttribute(clzz.getName() + "." + method.getName());
					for (int i = 0; i < registerList.size(); i++) {
						RegisterDescription register = registerList.get(i);
						Class<?> registerClass = register.getRegisterClass();
						Support support = registerClass.getAnnotation(Support.class);
						try {
							InvokeHandler handler = null;
							Map<Class<?>, Object> annos = null;
							if (support == null) {
								handler = register.getRegisterInstance(InvokeHandler.class);
							} else {
								Class<? extends Annotation>[] supportClass = support.value();
								for (Class<? extends Annotation> supportClzz : supportClass) {
									// 依次从代理类或接口类的方法和类声明中获取支持的注解
									Annotation anno;
									// 获取代理类的方法
									Method proMethod = this.loader.getMethod(method.getName(),
											method.getParameterTypes());
									if (proMethod != null) {
										if ((anno = proMethod.getAnnotation(supportClzz)) != null) {
											annos = this.addHanslerAnnotation(annos, anno);
											handler = register.getRegisterInstance(InvokeHandler.class);
										} else {
											Parameter[] parameters = proMethod.getParameters();
											for (Parameter parameter : parameters) {
												if ((anno = parameter.getAnnotation(supportClzz)) != null) {
													annos = this.addHanslerAnnotation(annos, anno);
													handler = register.getRegisterInstance(InvokeHandler.class);
												}
											}
										}
									}
									if (handler == null) {
										if ((anno = method.getAnnotation(supportClzz)) != null) {
											annos = this.addHanslerAnnotation(annos, anno);
											handler = register.getRegisterInstance(InvokeHandler.class);
										} else {
											Parameter[] parameters = method.getParameters();
											for (Parameter parameter : parameters) {
												if ((anno = parameter.getAnnotation(supportClzz)) != null) {
													annos = this.addHanslerAnnotation(annos, anno);
													handler = register.getRegisterInstance(InvokeHandler.class);
												}
											}
										}
									}
									if (handler == null && (anno = this.clzz.getAnnotation(supportClzz)) != null) {
										annos = this.addHanslerAnnotation(annos, anno);
										handler = register.getRegisterInstance(InvokeHandler.class);
									}
									if (handler == null && (anno = clzz.getAnnotation(supportClzz)) != null) {
										annos = this.addHanslerAnnotation(annos, anno);
										handler = register.getRegisterInstance(InvokeHandler.class);
									}
								}
							}
							if (handler != null) {
								if (ihs == null) {
									ihs = new InvokeHandlerSet(handler);
									ihs.setAnnotations(annos);
									handlerMapping.put(method, ihs);
								} else {
									InvokeHandlerSet ihn = new InvokeHandlerSet(handler);
									ihn.setAnnotations(annos);
									ihs.getLast().addInvokeHandlerSet(ihn);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				// List<InvokeHandler> handlers =
				// PlugsFactory.getPlugsInstanceListByAttribute(InvokeHandler.class,
				// clzz.getName() + "." + method.getName());
				// for (int i = 0, len = handlers.size(); i < len; i++) {
				// // 获取支持的注解
				// if (ihs == null) {
				// ihs = new InvokeHandlerSet(handlers.get(i));
				// handlerMapping.put(method, ihs);
				// } else {
				// ihs.getLast().addInvokeHandlerSet(new
				// InvokeHandlerSet(handlers.get(i)));
				// }
				// }
			}
		}
	}

	private Map<Class<?>, Object> addHanslerAnnotation(Map<Class<?>, Object> annos, Annotation anno) {
		if (annos == null)
			annos = new HashMap<Class<?>, Object>();
		annos.put(anno.annotationType(), anno);
		return annos;
	}

	public static <T> Class<?>[] getPlugs(Class<?> clzz, String declareRegister) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		String[] strs = declareRegister.split(",");
		for (String str : strs) {
			if (str.trim().equals("*")) {
				Class<?>[] plugs = clzz.getInterfaces();
				for (Class<?> plug : plugs)
					set.add(plug);
			} else {
				try {
					Class<?> interfacer = Class.forName(str.trim());
					if (ClassLoader.implementsOf(clzz, interfacer))// 判断类及父类是否实现某接口
						set.add(interfacer);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		Class<?>[] plugs = new Class<?>[set.size()];
		int i = 0;
		for (Class<?> plug : set)
			plugs[i++] = plug;
		return plugs;
	}

	public static void checkPlugs(Class<?>[] plugs) {
		for (Class<?> plug : plugs) {
			if (PlugsFactory.getPlug(plug) == null)
				PlugsFactory.getInstance().addPlugsByDefault(plug);
		}
	}

	public static Class<?>[] getPlugs(Class<?> clzz, Class<?>[] declareRegister) {
		Class<?>[] plugs;
		if (declareRegister.length == 0)
			plugs = clzz.getInterfaces();
		else {
			plugs = new Class<?>[declareRegister.length];
			int index = 0;
			Class<?>[] cls = clzz.getInterfaces();
			for (int i = 0; i < cls.length; i++) {
				for (int j = 0; j < plugs.length; j++) {
					if (declareRegister[j].equals(cls[i].getName())) {
						plugs[index++] = cls[i];
					}
				}
			}
		}
		return plugs;
	}

	public Class<?> getRegisterClass() {
		return clzz;
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	public Class<?>[] getPlugs() {
		return plugs;
	}

	public void setPlugs(Class<?>[] plugs) {
		this.plugs = plugs;
	}

	public void setClzz(Class<?> clzz) {
		this.clzz = clzz;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String[] getAttribute() {
		return attribute;
	}

	public void setAttribute(String... attribute) {
		this.attribute = attribute;
	}

	public boolean isSignlton() {
		return signlton;
	}

	public void setSignlton(boolean signton) {
		this.signlton = signton;
	}

	public Map<Constructor<?>, InvokeHandlerSet> getConstructorMapping() {
		return constructorMapping;
	}

	/**
	 * 获取一个服务的新对像
	 * 
	 * @param plug
	 * @param args
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getRegisterNewInstance(Class<T> plug, Object... args) throws Exception {
		Object proxy = null;
		Object target = null;
		// 获取构造器
		Constructor<?> constructor = this.getConstructor(args);
		// 获取构造器拦截器
		InvokeHandlerSet invokeHandlerSet = null;
		InstanceHandler handler = null;
		try {
			if (constructor != null && this.constructorMapping != null) {
				invokeHandlerSet = this.constructorMapping.get(constructor);
				if (invokeHandlerSet != null) {
					Iterator<InvokeHandlerSet> handlerIterator = invokeHandlerSet.iterator();
					while (handlerIterator.hasNext()) {
						handler = (InstanceHandler) handlerIterator.next().getInvokeHandler();
						handler.before(this, plug, constructor, args);
					}
				}
			}
			// 实例化对象
			if (plug.isInterface()) {
				switch (this.proxyModel) {
				case DEFAULT:
					target = constructor.newInstance(args);
					if (plug.equals(InvokeHandler.class) || plug.equals(InstanceHandler.class))
						proxy = target;
					else
						proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				case JDK:
					target = constructor.newInstance(args);
					if (plug.equals(InvokeHandler.class) || plug.equals(InstanceHandler.class))
						proxy = target;
					else
						proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				case CGLIB:
					target = proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
					break;
				case BOTH:
					target = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
					proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				default:
					target = constructor.newInstance(args);
					if (plug.equals(InvokeHandler.class) || plug.equals(InstanceHandler.class))
						proxy = target;
					else
						proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				}
			} else {
				target = proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
			}
			if (this.fieldParam != null) {
				for (FieldDesc desc : this.fieldParam.values()) {
					desc.getFieldHandler().preparedField(this, proxy, target, desc, args);
				}
			}
			// 调用代理完成
			if (invokeHandlerSet != null) {
				Iterator<InvokeHandlerSet> handlerIterator = invokeHandlerSet.iterator();
				while (handlerIterator.hasNext()) {
					handler = (InstanceHandler) handlerIterator.next().getInvokeHandler();
					handler.after(this, plug, constructor, target, args);
				}
			}
		} catch (Throwable t) {
			PluginRuntimeException exception = new PluginRuntimeException(t);
			if (invokeHandlerSet != null) {
				if (handler != null)
					handler.exception(this, plug, constructor, proxy, exception, args);
				if (!exception.isInterrupt()) {
					Iterator<InvokeHandlerSet> handlerIterator = invokeHandlerSet.iterator();
					InstanceHandler i;
					while (handlerIterator.hasNext() && !exception.isInterrupt()) {
						i = (InstanceHandler) handlerIterator.next().getInvokeHandler();
						if (i == handler)
							continue;
						i.exception(this, plug, constructor, proxy, exception, args);
					}
				}

			}
			if (!exception.isInterrupt())
				throw exception;
		}
		return (T) proxy;
	}

	/**
	 * 获取服务的实例
	 * 
	 * @param plug
	 * @param args
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getRegisterInstance(Class<T> plug, Object... args) throws Exception {
		Object proxy = null;
		// 判断是否单例
		if (this.signlton) {
			proxy = this.getProxyInstance(plug, args);
			if (proxy == null)
				proxy = this.getRegisterNewInstance(plug, args);
			this.createProxyContainer();
			proxyContainer.put(hash(plug, args), proxy);
		} else {
			proxy = this.getRegisterNewInstance(plug, args);
		}
		return (T) proxy;
	}

	@SuppressWarnings("unchecked")
	private <T> T getProxyInstance(Class<T> plug, Object... args) {
		Object proxy = null;
		this.createProxyContainer();
		int hashKey = hash(plug, args);
		proxy = this.proxyContainer.get(hashKey);
		return (T) proxy;
	}

	public Object getProxyObjectByContructor(Class<?> plug, Constructor<?> constructor, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object obj = constructor.newInstance(args);
		return PlugsHandler.newMapperProxy(plug, this, obj);
	}

	public Object getProxyTargetByJdk(Object... args) throws Exception {
		Object target;
		if (args.length == 0) {
			target = this.clzz.newInstance();
		} else {
			Constructor<?> constructor = getConstructor(args);
			target = constructor.newInstance(args);
		}
		return target;
	}

	public Constructor<?> getConstructor(Object... args) throws Exception {
		Class<?>[] parameterTypes = com.YaNan.frame.reflect.ClassLoader.getParameterTypes(args);
		Constructor<?> constructor = ClassInfoCache.getClassHelper(this.clzz).getConstructor(parameterTypes);// this.cl.getConstructor(parameterTypes);
		if (constructor == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < parameterTypes.length; i++) {
				sb.append(parameterTypes[i].getName()).append(i < parameterTypes.length - 1 ? "," : "");
			}
			throw new Exception("constructor " + this.clzz.getSimpleName() + "(" + sb.toString() + ") is not exist at "
					+ this.clzz.getName());
		}
		return constructor;
	}

	public static int hash(Class<?> clzz, Object... objects) {
		return clzz == null ? 0 : clzz.hashCode() + hash(objects);
	}

	public static int hash(Object... objects) {
		int hash = 0;
		for (int i = 0; i < objects.length; i++)
			hash += objects[i].hashCode();
		return hash;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getDescription() {
		return description;
	}

	public Map<Integer, Object> getProxyContainer() {
		return proxyContainer;
	}

	public void setProxyContainer(Map<Integer, Object> proxyContainer) {
		this.proxyContainer = proxyContainer;
	}

	public Map<Method, InvokeHandlerSet> getHandlerMapping() {
		return handlerMapping;
	}

	public void setHandlerMapping(Map<Method, InvokeHandlerSet> handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<Field, FieldDesc> getFieldParam() {
		return fieldParam;
	}

	public void setFieldParam(Map<Field, FieldDesc> fieldParam) {
		this.fieldParam = fieldParam;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
