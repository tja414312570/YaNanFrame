package com.YaNan.frame.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.CacheContainer;

/**
 * 组件描述类 用于创建组件时的组件信息
 * 
 * @author yanan
 *
 */
public class RegisterDescription {
	private Map<Integer, Object> proxyContainer = new HashMap<Integer, Object>();
	/**
	 * 组件类
	 */
	private Class<?> clzz;
	private Register register;
	private Class<?>[] plugs;
	private int priority = 0;
	private String[] attribute = { "*" };
	private boolean signlton;
	private File file;
	private String description = "";
	private Map<Field,Method> services = null;

	public Map<Field, Method> getServices() {
		return services;
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
		this.clzz = register.declare().equals(Object.class) ? clzz : register.declare();
		this.register = register;
		this.plugs = register.register().length == 0 ? this.clzz.getInterfaces() : register.register();
		this.priority = register.priority();
		this.signlton = register.signlTon();
		this.attribute = register.attribute();
		this.description = register.description();
		checkPlugs(this.plugs);
		this.findServiceField();
	}

	public RegisterDescription(Class<?> clzz) {
		// 读取属性
		this.priority = Integer.MAX_VALUE;
		this.signlton = true;
		this.description = "default register description :" + clzz.getName();
		// 获取实现类
		this.clzz = new ClassLoader(clzz, false).getLoadedClass();
		// 获取实现类所在的接口
		this.plugs = clzz.getInterfaces();
		this.findServiceField();
	}

	public RegisterDescription(File file) throws Exception {
		try {
			this.setFile(file);
			String fileName = file.getName();
			String clzzStr = fileName.substring(0, fileName.lastIndexOf("."));
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			Properties p = new Properties();
			p.load(in);
			// 读取属性
			this.priority = Integer.valueOf(p.getProperty("comps.priority", "0"));
			this.signlton = Boolean.valueOf(p.getProperty("comps.signlton", "true"));
			this.attribute = p.getProperty("comps.attribute", "*").split(",");
			this.description = p.getProperty("comps.description", "");
			String className = p.getProperty("comps.class", clzzStr);
			String declareRegister = p.getProperty("comps.register", "*");
			// 获取实现类
			this.clzz = new ClassLoader(className, false).getLoadedClass();
			// 获取实现类所在的接口
			this.plugs = getPlugs(clzz, declareRegister);
			if (this.plugs == null || this.plugs.length == 0)
				throw new Exception("register " + clzz.getName() + " not implements any interface");
			checkPlugs(this.plugs);
			this.findServiceField();
		} catch (Exception e) {
			throw new Exception("plugin " + file.getName() + " init failed", e);
		}
	}

	public void findServiceField() {
		for (Field field : this.clzz.getDeclaredFields()) {
			Service service = field.getAnnotation(Service.class);
			if (service != null) {
				if (services == null)
					services = new LinkedHashMap<Field, Method>();
				Method method =null;
				try {
					method = ClassLoader.getMethod(this.clzz, ClassLoader.createFieldSetMethod(field), field.getType());
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				services.put(field, method);
			}
		}
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
		for (Class<?> plug : set) {
			plugs[i] = plug;
			i++;
		}
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
						plugs[index] = cls[i];
						index++;
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
					proxy = proxyContainer.get(hash(plug, args));
					if (proxy == null) {
						Object obj = this.clzz.newInstance();
						proxy = PlugsHandler.newMapperProxy(plug, obj);
						proxyContainer.put(hash(plug, args), proxy);
					}
				} else {
					Object obj = this.clzz.newInstance();
					proxy = PlugsHandler.newMapperProxy(plug, obj);
				}
			} else {
				Class<?>[] parameterTypes = com.YaNan.frame.reflect.ClassLoader.getParameterTypes(args);
				Constructor<?> constructor = CacheContainer.getClassInfoCache(this.clzz).getConstructor(parameterTypes);// this.cl.getConstructor(parameterTypes);
				if (constructor == null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < parameterTypes.length; i++) {
						sb.append(parameterTypes[i].getName()).append(i < parameterTypes.length - 1 ? "," : "");
					}
					throw new Exception("constructor " + this.clzz.getSimpleName() + "(" + sb.toString()
							+ ") is not exist at " + this.clzz.getName());
				}

				Object obj = constructor.newInstance(args);
				proxy = PlugsHandler.newMapperProxy(plug, obj);
			}
		} else {
			if (this.signlton && args.length == 0) {
				proxy = proxyContainer.get(hash(plug, args));
				if (proxy == null) {
					proxy = PlugsHandler.newCglibProxy(this.getRegisterClass());
					proxyContainer.put(hash(plug, args), proxy);
				}
			} else
				proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), args);
		}
		this.initPlugInstance(proxy);
		return (T) proxy;
	}

	public void initPlugInstance(Object proxy) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(this.services!=null){
			Iterator<Entry<Field, Method>> iterator = this.services.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Field, Method> entry = iterator.next();
				Object object = PlugsFactory.getPlugsInstance(entry.getKey().getType());
				if(entry.getValue()!=null){
					entry.getValue().invoke(proxy, object);
				}else{
					entry.getKey().setAccessible(true);
					entry.getKey().set(proxy, object);
					entry.getKey().setAccessible(false);
				}
			}
		}
			
	}

	@SuppressWarnings("unchecked")
	public <T> T getRegisterNewInstance(Class<T> plug, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object proxy = null;
		if (plug.isInterface()) {
			Object obj = PlugsHandler.newCglibProxy(this.getRegisterClass(), args);
			proxy = PlugsHandler.newMapperProxy(plug, obj);
			proxyContainer.put(hash(plug, args), proxy);

		} else
			proxy = PlugsHandler.newCglibProxy(this.getRegisterClass(), args);
		this.initPlugInstance(proxy);
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
}
