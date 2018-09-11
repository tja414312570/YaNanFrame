package com.YaNan.frame.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.InvokeHandlerSet;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.ClassInfoCache;

/**
 * 组件描述类 用于创建组件时的组件信息
 * v1.0 支持通过Class的Builder方式
 * v1.1 支持通过Comp文件的Builder方式
 * v1.2 支持创建默认的Builder方式
 * v1.3 支持描述器的属性
 * v1.4 将InvokeHandler的创建迁移到组件初始化时，大幅度提高代理执行效率
 * v1.5 20180910 重新构建InvokeHandler的逻辑，提高aop的效率
 * @author yanan
 *
 */
public class RegisterDescription {
	private Map<Integer, Object> proxyContainer = new HashMap<Integer, Object>();
	/**
	 * 组件类
	 */
	private Class<?> clzz;
	private ClassLoader loader;
	private Register register;
	private Class<?>[] plugs;
	private int priority = 0;
	private String[] attribute = { "*" };
	private boolean signlton;
	private File file;
	private Properties properties;
	private String description = "";
	private Map<Method, InvokeHandlerSet> handlerMapping;// 方法拦截器
	private Map<String, Object> attributes;
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
		checkPlugs(this.plugs);
		PlugsFactory.getInstance().addRegisterHandlerQueue(this);
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
			// 获取实现类
			this.loader = new ClassLoader(className, false);
			this.clzz = loader.getLoadedClass();
			// 获取实现类所在的接口
			this.plugs = getPlugs(clzz, declareRegister);
			if (this.plugs == null || this.plugs.length == 0)
				throw new Exception("register " + clzz.getName() + " not implements any interface");
			checkPlugs(this.plugs);
			PlugsFactory.getInstance().addRegisterHandlerQueue(this);
		} catch (Exception e) {
			throw new Exception("plugin " + file.getName() + " init failed", e);
		}
	}

	public void initHandler() {
		handlerMapping = new HashMap<Method, InvokeHandlerSet>();
		for (Class<?> interfacer : plugs) {
			this.initHandlerMapping(interfacer);
		}
		this.initHandlerMapping(this.clzz);
	}

	private void initHandlerMapping(Class<?> clzz) {
		if (clzz != null) {
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
							Map<Class<?>,Object> annos = null;
							if (support == null) {
								handler = register.getRegisterInstance(InvokeHandler.class);
							} else {
								Class<? extends Annotation>[] supportClass = support.value();
								for(Class<? extends Annotation> supportClzz : supportClass){
									//依次从代理类或接口类的方法和类声明中获取支持的注解
									Annotation anno;
									//获取代理类的方法
										Method proMethod = this.loader.getMethod(method.getName(),method.getParameterTypes());
										if(proMethod!=null&&(anno = proMethod.getAnnotation(supportClzz))!=null){
											annos = this.addHanslerAnnotation(annos,anno);
											handler = register.getRegisterInstance(InvokeHandler.class);
										}else if(handler==null&&(anno = this.clzz.getAnnotation(supportClzz))!=null){
											annos = this.addHanslerAnnotation(annos,anno);
											handler = register.getRegisterInstance(InvokeHandler.class);
										}else if((anno = method.getAnnotation(supportClzz))!=null){
										annos = this.addHanslerAnnotation(annos,anno);
										handler = register.getRegisterInstance(InvokeHandler.class);
									}else if((anno = clzz.getAnnotation(supportClzz))!=null){
										annos = this.addHanslerAnnotation(annos,anno);
										handler = register.getRegisterInstance(InvokeHandler.class);
									}
								}
							}
							if(handler!=null){
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
//				List<InvokeHandler> handlers = PlugsFactory.getPlugsInstanceListByAttribute(InvokeHandler.class,
//						clzz.getName() + "." + method.getName());
//				for (int i = 0, len = handlers.size(); i < len; i++) {
//					// 获取支持的注解
//					if (ihs == null) {
//						ihs = new InvokeHandlerSet(handlers.get(i));
//						handlerMapping.put(method, ihs);
//					} else {
//						ihs.getLast().addInvokeHandlerSet(new InvokeHandlerSet(handlers.get(i)));
//					}
//				}
			}
		}
	}

	private Map<Class<?>, Object> addHanslerAnnotation(Map<Class<?>, Object> annos, Annotation anno) {
		if(annos==null)
			annos=new HashMap<Class<?>, Object>();
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

	@SuppressWarnings("unchecked")
	public <T> T getRegisterInstance(Class<T> plug, Object... args) throws Exception {
		Object proxy = null;
		if (plug.isInterface()) {
			if (args.length == 0) {
				if (this.signlton) {
					proxy = proxyContainer.get(hash(plug));
					if (proxy == null) {
						Object obj = this.clzz.newInstance();
						proxy = PlugsHandler.newMapperProxy(plug, this, obj);
						proxyContainer.put(hash(plug), proxy);
					}
				} else {
					Object obj = this.clzz.newInstance();
					proxy = PlugsHandler.newMapperProxy(plug, this, obj);
				}
			} else {
				if (this.signlton) {
					proxy = proxyContainer.get(hash(plug, args));
					if (proxy == null) {
						Constructor<?> constructor = getConstructor(args);
						proxy = getProxyObjectByContructor(plug, constructor, args);
						proxyContainer.put(hash(plug, args), proxy);
					}
				} else {
					Constructor<?> constructor = getConstructor(args);
					proxy = getProxyObjectByContructor(plug, constructor, args);
				}
			}
		} else {
			if (this.signlton) {
				proxy = proxyContainer.get(hash(plug, args));
				if (proxy == null) {
					proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this);
					proxyContainer.put(hash(plug, args), proxy);
				}
			} else {
				proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
			}
		}
		return (T) proxy;
	}

	public Object getProxyObjectByContructor(Class<?> plug, Constructor<?> constructor, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object obj = constructor.newInstance(args);
		return PlugsHandler.newMapperProxy(plug, this, obj);
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

	@SuppressWarnings("unchecked")
	public <T> T getRegisterNewInstance(Class<T> plug, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object proxy = null;
		if (plug.isInterface()) {
			Object obj = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
			proxy = PlugsHandler.newMapperProxy(plug, this, obj);
			proxyContainer.put(hash(plug, args), proxy);

		} else
			proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), this, args);
		return (T) proxy;
	}

	public static int hash(Class<?> clzz, Object... objects) {
		return clzz.hashCode() + hash(objects);
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

}
