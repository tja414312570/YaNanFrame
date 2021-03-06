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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;

import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.ParameterUtils.MethodDesc;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.beans.BeanContainer;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.InvokeHandlerSet;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.plugin.handler.ProxyHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.ClassHelper;
import com.YaNan.frame.reflect.cache.ClassInfoCache;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.SimpleConfigObject;

/**
 * 组件描述类 用于创建组件时的组件信息 v1.0 支持通过Class的Builder方式 v1.1 支持通过Comp文件的Builder方式 v1.2
 * 支持创建默认的Builder方式 v1.3 支持描述器的属性 v1.4 将InvokeHandler的创建迁移到组件初始化时，大幅度提高代理执行效率
 * v1.5 20180910 重新构建InvokeHandler的逻辑，提高aop的效率 v1.6 20180921
 * 添加FieldHandler和ConstructorHandler 实现方法拦截与构造器拦截
 * v1.6 20190319 支持构造器参数，支持初始化后调用方法参数，支持构造器和方法匹配，参数数据结构多种支持，参数类型自动匹配
 * 
 * @author yanan
 *
 */
public class RegisterDescription {
	private volatile Map<Integer, Object> proxyContainer;
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
	/**
	 * 初始化后执行的方法
	 */
	private Method[] initMethod;
	private String id;
	/**
	 * 链接对象
	 */
	private RegisterDescription linkRegister;
	/**
	 * 链接之后的原代理的代理对象
	 */
	private Object linkProxy;
	private Method method;

	public RegisterDescription getLinkRegister() {
		return linkRegister;
	}

	public Object getLinkProxy() {
		return linkProxy;
	}

	public ClassLoader getProxyClassLoader() {
		return loader;
	}

	public String getBeanId() {
		return id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attribute);
		result = prime * result + ((clzz == null) ? 0 : clzz.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(initMethod);
		result = prime * result + priority;
		result = prime * result + ((proxyModel == null) ? 0 : proxyModel.hashCode());
		result = prime * result + (signlton ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisterDescription other = (RegisterDescription) obj;
		if (!Arrays.equals(attribute, other.attribute))
			return false;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (clzz == null) {
			if (other.clzz != null)
				return false;
		} else if (!clzz.equals(other.clzz))
			return false;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (!Arrays.equals(initMethod, other.initMethod))
			return false;
		if (!Arrays.equals(plugs, other.plugs))
			return false;
		if (priority != other.priority)
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (proxyModel != other.proxyModel)
			return false;
		if (register == null) {
			if (other.register != null)
				return false;
		} else if (!register.equals(other.register))
			return false;
		if (signlton != other.signlton)
			return false;
		return true;
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
		String[] methods = register.method();
		this.initMethod = new Method[methods.length];
		int i = 0;
		for(;i<methods.length;i++)
			try {
				this.initMethod[i] = this.loader.getDeclaredMethod(methods[i]);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new PluginInitException("failed to get init method \""+methods[i]+"\"",e);
			}
		checkPlugs(this.plugs);
		PlugsFactory.getInstance().addRegisterHandlerQueue(this);
	}

	public void createProxyContainer() {
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

	public RegisterDescription(File file) {
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
		} catch (Throwable e) {
			if (PluginAppincationContext.isWebContext())
				throw new PluginInitException("plugin " + file.getName() + " init failed", e);
			else
				e.printStackTrace();
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
				throw new PluginInitException("plugin exception init at \"" + config.origin().url() + "\" at line "
						+ config.origin().lineNumber(), e);
			else
				e.printStackTrace();
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

	/**
	 * 给方法添加Handler
	 * 
	 * @param method
	 * @param handler
	 */
	public synchronized void addMethodHandler(Method method, InvokeHandler handler) {
		if (handlerMapping == null) {
			handlerMapping = new HashMap<Method, InvokeHandlerSet>();
		}
		InvokeHandlerSet ihs = handlerMapping.get(method);
		if (handler != null) {
			if (ihs == null) {
				ihs = new InvokeHandlerSet(handler);
				handlerMapping.put(method, ihs);
			} else {
				InvokeHandlerSet ihn = new InvokeHandlerSet(handler);
				ihs.getLast().addInvokeHandlerSet(ihn);
			}
		}

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
			this.id = config.getString("id");
			if (id != null) {
				Object instance = null;
				try {
					// 判断是否有参数
					if (config.hasPath("args")) {
						// 若果配置中无method方法
						if (!config.hasPath("method")) {
							// 获取所有的构造器
							List<Constructor<?>> constructorList = new ArrayList<Constructor<?>>();
							Constructor<?>[] ctrs = this.clzz.getConstructors();
							for (Constructor<?> ctr : ctrs)
								constructorList.add(ctr);
							// 判断是否是列表
							if (config.isList("args")) {
								// 判断是否是conf集合
								if (config.isConfigList("args")) {
									List<? extends Config> values = config.getConfigList("args");
									Object[] parameters = new Object[values.size()];
									Class<?>[] parameterTypes = new Class<?>[values.size()];
									for (int i = 0; i < values.size(); i++) {
										Config conf = values.get(i);
										Iterator<Entry<String, Object>> iterator = conf.simpleObjectEntrySet()
												.iterator();
										if (iterator.hasNext()) {
											Entry<String, Object> entry = iterator.next();
											String key = entry.getKey();
											Object value = entry.getValue();
											parameterTypes[i] = ParameterUtils.getParameterType(key);
											if (parameterTypes[i].equals(File.class)) {// 文件类型特俗处理
												File file;
												try {
													List<File> files = ResourceManager.getResource(value.toString());
													file = files.get(0);
												} catch (Throwable t) {
													file = new File(ResourceManager.getPathExress(value.toString()));
												}
												parameters[i] = file;
											} else if (parameterTypes[i].equals(Service.class)) {
												String beanId = value.toString();
												Object bean = BeanContainer.getContext().getBean(beanId);
												parameters[i] = bean;
											} else {
												parameters[i] = ClassLoader.castType(value, parameterTypes[i]);
											}
										}
									}
									Constructor<?> constructor = this.getConstructor(parameterTypes);
									if (constructor == null)
										throw new PluginInitException("could not found any constructor for parameter "
												+ values + " at bean id " + id);
									instance = this.getNewInstance(this.clzz, constructor, parameters);
								} else {// 普通数据列表
									List<? extends Object> values = config.getSimpleObjectList("args");
									int argSize = values.size();
									this.cleanSizeNotEquals(constructorList, argSize);
									if (constructorList.size() == 0)
										throw new PluginInitException("could not found any constructor for parameter "
												+ values + " at bean id " + id);
									// 找到一个合适的构造器
									Constructor<?> constructor = ParameterUtils.getEffectiveConstructor(constructorList,
											values);
									if (constructor == null)
										throw new PluginInitException("could not found any constructor for parameter "
												+ values + " at bean id " + id);
									Object[] parameters = new Object[values.size()];
									Class<?>[] parameterTypes = constructor.getParameterTypes();
									for (int i = 0; i < values.size(); i++) {
										parameters[i] = ClassLoader.castType(values.get(i), parameterTypes[i]);
									}
									instance = this.getNewInstance(this.clzz, constructor, values.toArray(parameters));

								}
							} else {// 单参数
								ConfigValueType valueType = config.getType("args");
								// 如果是config类型
								if (valueType == ConfigValueType.OBJECT) {
									// 获取到config
									// 获取参数的数量
									Set<Entry<String, ConfigValue>> entrySet = config.getConfig("args").entrySet();
									int argSize = entrySet.size();
									// 排除数量不同的构造器
									this.cleanSizeNotEquals(constructorList, argSize);
									Iterator<Entry<String, ConfigValue>> argIterator = entrySet.iterator();
									if (constructorList.size() == 0)
										throw new PluginInitException("could not found any constructor for parameter "
												+ entrySet + " at bean id " + id);
									Class<?>[] parameterType = new Class<?>[argSize];
									Object[] parameters = new Object[argSize];
									int i = 0;
									while (argIterator.hasNext()) {
										Entry<String, ConfigValue> entry = argIterator.next();
										parameterType[i] = ParameterUtils.getParameterType(entry.getKey());
										Object value = entry.getValue().unwrapped();
										if (parameterType[i].equals(File.class)) {// 文件类型特俗处理
											File file;
											try {
												List<File> files = ResourceManager.getResource(value.toString());
												file = files.get(0);

											} catch (Throwable t) {
												file = new File(ResourceManager.getPathExress(value.toString()));
											}
											parameters[i] = file;
										} else if (parameterType[i].equals(Service.class)) {// bean类型
											String beanId = value.toString();
											parameters[i] = BeanContainer.getContext().getBean(beanId);
										} else {
											parameters[i] = ClassLoader.castType(entry.getValue().unwrapped(),
													parameterType[i]);
										}
										i++;
									}
									Constructor<?> constructor = this.getConstructor(parameterType);
									if (constructor == null)
										throw new PluginInitException("could not found any constructor for parameter "
												+ entrySet + " at bean id " + id);
									instance = this.getNewInstance(this.clzz, constructor, parameters);

								} else {
									this.cleanSizeNotEquals(constructorList, 1);
									Object value = config.getSimpleObject("args");
									Constructor<?> constructor = this.getConstructor(value);
									if (constructor == null)
										throw new PluginInitException("could not found any constructor for parameter "
												+ value + " at bean id " + id);
									instance = this.getNewInstance(this.clzz, constructor, value);
								}
							}
						} else {// 通过方法实例化
								// 获取实例化的方法名
							String methodName = config.getString("method");
							ClassHelper helper = ClassHelper.getClassHelper(this.getRegisterClass());
							// 判断是否是列表
							if (config.isList("args")) {
								// 判断是否是conf集合
								if (config.isConfigList("args")) {
									List<? extends Config> values = config.getConfigList("args");
									Object[] parameters = new Object[values.size()];
									Class<?>[] parameterTypes = new Class<?>[values.size()];
									for (int i = 0; i < values.size(); i++) {
										Config conf = values.get(i);
										Iterator<Entry<String, Object>> iterator = conf.simpleObjectEntrySet()
												.iterator();
										if (iterator.hasNext()) {
											Entry<String, Object> entry = iterator.next();
											String key = entry.getKey();
											Object value = entry.getValue();
											parameterTypes[i] = ParameterUtils.getParameterType(key);
											parameters[i] = ParameterUtils.getParameter(parameterTypes[i], value);
										}
									}
									Method method = helper.getMethod(methodName, parameterTypes);
									if (method == null)
										throw new PluginInitException("could not found method name is \""+methodName+"\" for parameter "
												+ values + " at bean id " + id);
									instance = this.instanceBeanByMethod(config, method, parameters);
								} else {// 普通数据列表
									List<? extends Object> values = config.getSimpleObjectList("args");
									Object[] parameters = new Object[values.size()];
									Class<?>[] parameterTypes = new Class<?>[values.size()];
									for (int i = 0; i < values.size(); i++) {
										parameters[i] = values.get(i);
										parameterTypes[i] = values.get(i).getClass();
									}
									Method method = helper.getMethod(methodName, parameterTypes);
									if (method == null)
										throw new PluginInitException("could not found method name is \""+methodName+"\" for parameter "
												+ values + " at bean id " + id);
									instance = this.instanceBeanByMethod(config, method, parameters);
								}
							} else {// 单参数
								ConfigValueType valueType = config.getType("args");
								// 如果是config类型
								if (valueType == ConfigValueType.OBJECT) {
									// 获取到config
									Set<Entry<String, ConfigValue>> entrySet = config.getConfig("args").entrySet();
									int argSize = entrySet.size();
									Class<?>[] parameterTypes = new Class<?>[argSize];
									Object[] parameters = new Object[argSize];
									int i = 0;
									Iterator<Entry<String, ConfigValue>> argIterator = entrySet.iterator();
									while (argIterator.hasNext()) {
										Entry<String, ConfigValue> entry = argIterator.next();
										parameterTypes[i] = ParameterUtils.getParameterType(entry.getKey());
										Object value = entry.getValue().unwrapped();
										parameters[i] = ParameterUtils.getParameter(parameterTypes[i], value);
										i++;
									}
									Method method = helper.getMethod(methodName, parameterTypes);
									if (method == null)
										throw new PluginInitException("could not found method name is \""+methodName+"\" for parameter " + entrySet
												+ " at bean id " + id);
									instance = this.instanceBeanByMethod(config, method, parameters);
								} else {
									Object value = config.getSimpleObject("args");
									Method method = helper.getMethod(methodName, value.getClass());
									if (method == null)
										throw new PluginInitException("could not found method name is \""+methodName+"\" for parameter "
												+ value + " at bean id " + id);
									instance = this.instanceBeanByMethod(config, method, value);
								}
							}
						}
					}
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
						// throw new Exception("register " + clzz.getName() + "
						// not
						// implements any interface");
						this.initHandlerMapping(this.clzz);
						this.initConstructorHandlerMapping(this.clzz);
						this.initFieldHandlerMapping();
						checkPlugs(this.plugs);
					}
					if (instance == null){
						instance = this.getNewBean();
						if (this.config.hasPath("init")) {
							try {
								this.initProxyMethod(instance);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								throw new PluginInitException("failed to invoke register init method", e);
							}
						}
					}
					PlugsFactory.addBeanRegister(instance, this);
				} catch (Exception e) {
					throw new PluginInitException("failed to init bean for id \""+id+"\"",e);
				}
				if (this.fieldParam != null) {
					for (FieldDesc desc : this.fieldParam.values()) {
						desc.getFieldHandler().preparedField(this, null, instance, desc, null);
					}
				}
				BeanContainer.getContext().addBean(id, instance, this);
			}
		}
	}

	/**
	 * 获取实例化的bean
	 * 
	 * @param config
	 * @param method
	 * @param parameters
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private Object instanceBeanByMethod(Config config, Method method, Object... parameters)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String refConf = config.getString("ref");
		if (refConf != null) {
			Object ref = PlugsFactory.getBean(refConf);
			return method.invoke(ref, parameters);
		} else {
			return method.invoke(null, parameters);
		}
	}

	private void cleanSizeNotEquals(List<Constructor<?>> constructorList, int argSize) {
		Iterator<Constructor<?>> iterator = constructorList.iterator();
		while (iterator.hasNext()) {
			if (argSize != iterator.next().getParameterCount())
				iterator.remove();
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
				return this.getRegisterNewInstance(this.clzz);
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
			if (cplug == null)
				return null;
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
				if (handler.getClass().equals(this.getRegisterClass()))
					return null;
				desc.setFieldHandler(handler);
				return desc;
			}
		}
		return null;
	}

	private FieldDesc getSupportField(Field field) throws Exception {
		Plug cplug = PlugsFactory.getPlug(FieldHandler.class);
		if (cplug == null)
			return null;
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
			if (handler.getClass().equals(this.getRegisterClass()))
				return null;
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
						if (handler.getClass().equals(this.getRegisterClass()))
							continue;
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
						if (register.getRegisterClass().equals(this.getRegisterClass()))
							continue;
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
								if (handler.getClass().equals(this.getRegisterClass()))
									continue;
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
//					if (ClassLoader.implementsOf(clzz, interfacer))// 判断类及父类是否实现某接口
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
	public <T> T getRegisterNewInstance(Class<T> plug, Object... args) throws Exception {
		// 获取构造器
		Constructor<?> constructor = this.getConstructor(args);
		return this.getNewInstance(plug, constructor, args);
	}

	public <T> T getRegisterNewInstanceByParamType(Class<T> plug, Class<?>[] paramTypes, Object... args)
			throws Exception {
		// 获取构造器
		Constructor<?> constructor = this.getConstructor(paramTypes);
		// 获取构造器拦截器
		return this.getNewInstance(plug, constructor, args);
	}

	@SuppressWarnings("unchecked")
	private <T> T getNewInstance(Class<T> plug, Constructor<?> constructor, Object... args) {
		if(this.linkRegister!=null){//如果有链接的注册器
			//获取链接类的相同构造器
			try {
				if(this.linkProxy==null){
					Object linkObject = this.linkRegister.getRegisterInstance(plug, args);
					this.linkProxy = ProxyHandler.newCglibProxy(this.getRegisterClass(), this,
							constructor.getParameterTypes(),linkObject, args);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (T) linkProxy;
		}
		Object proxy = null;
		Object target = null;
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
					proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				case JDK:
					target = constructor.newInstance(args);
					proxy = PlugsHandler.newMapperProxy(plug, this, target);
					break;
				case CGLIB:
					target = proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this,
							constructor.getParameterTypes(), args);
					break;
				case BOTH:
					target = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, constructor.getParameterTypes(),
							args);
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
				target = proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this,
						constructor.getParameterTypes(), args);
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
			this.initProxyMethod(proxy);
		} catch (Throwable t) {
			PluginRuntimeException exception = t.getClass().equals(PluginRuntimeException.class)
					? (PluginRuntimeException) t : new PluginRuntimeException(t);
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
	 * 代理实例化后调用方法
	 * 
	 * @param proxy
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private void initProxyMethod(Object proxy)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (this.initMethod != null) {
			for (Method method : this.initMethod) {
				method.setAccessible(true);
				method.invoke(proxy);
				method.setAccessible(false);
			}
		} else if (this.config!=null && this.config.hasPath("init")) {
			ClassHelper helper = ClassHelper.getClassHelper(proxy.getClass());
			//判断初始化后调用方法是否是一个列表
			if (this.config.isList("init")) {
				//获取列表的无包裹类型
				List<? extends Object> methodList = this.config.getValueListUnwrapper("init");
				//初始化方法列表
				Method method;
				//对列表进行遍历
				for(int i = 0;i<methodList.size();i++){
					Object methodConf = methodList.get(i);
					if(methodConf.getClass().equals(String.class)){
						method = helper.getMethod(methodConf.toString());
						method.setAccessible(true);
						method.invoke(proxy);
						method.setAccessible(false);
					}else if(methodConf.getClass().equals(SimpleConfigObject.class)){
						MethodDesc[] methodDescs = ParameterUtils.transformToMethod(((SimpleConfigObject) methodConf).toConfig(), this);
//						method[i] = methodDesc.getMethod();
//						parameters[i] = methodDesc.getParameter();
						for(MethodDesc methodDesc : methodDescs){
							methodDesc.getMethod().setAccessible(true);
							methodDesc.getMethod().invoke(proxy,methodDesc.getParameter());
							methodDesc.getMethod().setAccessible(false);
						}
					}
				}
			} else {
				if(config.getType("init") == ConfigValueType.STRING){
					Method method = helper.getMethod(config.getString("init"));
					method.setAccessible(true);
					method.invoke(proxy);
					method.setAccessible(false);
				}else if(config.getType("init") == ConfigValueType.OBJECT){
					MethodDesc[] methodDescs = ParameterUtils.transformToMethod(config.getConfig("init"), this);
					for(MethodDesc methodDesc : methodDescs){
						Method method = methodDesc.getMethod();
						method.setAccessible(true);
						method.invoke(proxy,methodDesc.getParameter());
						method.setAccessible(false);
					}
				}
			}
//			this.initProxyMethod(proxy);
		}
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
		if(config != null ){
			String ref = config.getString("ref");
			if(ref!=null){
				proxy = BeanContainer.getContext().getBean(ref);
			}else{
				proxy  = getRegisterInstance0(plug,args);
			}
		}else{
			proxy  = getRegisterInstance0(plug,args);
		}
		if(config!=null && config.hasPath("method")&&this.method==null){
			if(config.hasPath("args")){
				ParameterInfo info = ParameterUtils.getParameterInfo(config);
				this.method = ClassHelper.getClassHelper(proxy.getClass()).getDeclaredMethod(config.getString("method"), info.getParameterTypes());
			}else{
				this.method = ClassHelper.getClassHelper(proxy.getClass()).getDeclaredMethod(config.getString("method"));
			}
		}
		if(this.method!=null){
			Object[] parameter = new Object[this.method.getParameters().length];
			proxy = method.invoke(proxy,parameter);
		}
		return (T) proxy;
	}

	private Object getRegisterInstance0(Class<?> plug, Object[] args) throws Exception {
		Object proxy;
		// 判断是否单例
		if (this.signlton) {
			int hashKey = hash(plug, args);
			proxy = this.getProxyInstance(plug, hashKey, args);
			if (proxy == null)
				proxy = this.getRegisterNewInstance(plug, args);
			proxyContainer.put(hash(plug, args), proxy);
		} else {
			proxy = this.getRegisterNewInstance(plug, args);
		}
		return proxy;
	}

	@SuppressWarnings("unchecked")
	public <T> T getRegisterInstanceByParamType(Class<T> plug, Class<?>[] paramType, Object... args) throws Exception {
		Object proxy = null;
		// 判断是否单例
		if (this.signlton) {
			int hashKey = hash(plug, args);
			proxy = this.getProxyInstance(plug, hashKey, args);
			if (proxy == null)
				proxy = this.getRegisterNewInstanceByParamType(plug, paramType, args);
			this.createProxyContainer();
			proxyContainer.put(hash(plug, args), proxy);
		} else {
			proxy = this.getRegisterNewInstanceByParamType(plug, paramType, args);
		}
		return (T) proxy;
	}

	@SuppressWarnings("unchecked")
	private <T> T getProxyInstance(Class<T> plug, int hashKey, Object... args) {
		Object proxy = null;
		if(this.proxyContainer==null)
			this.createProxyContainer();
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

	public Constructor<?> getConstructor(Object... args) {
		Class<?>[] parameterTypes = ClassLoader.getParameterTypes(args);
		Constructor<?> constructor = null;
		try {
			constructor = this.getConstructor(parameterTypes);
		} catch (Throwable t) {
			Iterator<Constructor<?>> iterator = ClassInfoCache.getClassHelper(this.clzz).getConstructorHelperMap()
					.keySet().iterator();
			while (iterator.hasNext()) {
				Constructor<?> con = iterator.next();
				Class<?>[] matchType = con.getParameterTypes();
				if (matchType.length != args.length)
					continue;
				if (ClassLoader.matchType(matchType, parameterTypes)) {
					constructor = con;
					break;
				}
			}
			if (constructor == null)
				throw t;
		}
		return constructor;
	}

	public Constructor<?> getConstructor(Class<?>[] paramTypes) {
		Constructor<?> constructor = ClassInfoCache.getClassHelper(this.clzz).getConstructor(paramTypes);// this.cl.getConstructor(parameterTypes);
		if (constructor == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < paramTypes.length; i++) {
				sb.append(paramTypes[i] == null ? null : paramTypes[i].getName())
						.append(i < paramTypes.length - 1 ? "," : "");
			}
			throw new PluginRuntimeException("constructor " + this.clzz.getSimpleName() + "(" + sb.toString()
					+ ") is not exist at " + this.clzz.getName());
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

	public void updateRegister(Class<?> registerClass) {
		this.signlton = true;//启用单例
		if(this.proxyContainer != null)
			this.proxyContainer.clear();//清理代理容器
		this.linkRegister = new RegisterDescription(registerClass);//设置链接注册器
		PlugsFactory.getInstance().addPlugs(registerClass);
		PlugsFactory.getInstance().associate();
		this.linkProxy = null;//此时应将代理重置，以更新具体对象
	}
}
