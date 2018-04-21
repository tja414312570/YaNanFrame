package com.YaNan.frame.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.CacheContainer;

/**
 * 组件描述类
 * 用于创建组件时的组件信息
 * @author yanan
 *
 */
public class RegisterDescription {
	private static Map<Integer,Object> instanceContainer = new HashMap<Integer,Object>();
	/**
	 * 组件类
	 */
	private Class<?> clzz;
	private Register register;
	private Class<?>[] plugs;
	private int priority=0;
	private String attribute="*";
	private boolean signlton;
	private File file;
	private String description ="";
	/**
	 * 支持注解类型的构造器
	 * register为注解
	 * clzz为注册器的类名
	 * impls 为注册器实现的接口
	 * @param register
	 * @param clzz
	 * @param impls
	 * @throws Exception 
	 */
	public RegisterDescription(Register register, Class<?> clzz) throws Exception {
		this.clzz = register.declare().equals(Object.class)?clzz:register.declare();
		this.register = register;
		this.plugs = register.register().length==0?this.clzz.getInterfaces():register.register();
		this.priority = register.priority();
		this.signlton = register.signlTon();
		this.attribute = register.attribute();
		this.description = register.description();
		if(this.plugs==null||this.plugs.length==0)
			throw new Exception("register "+clzz.getName()+" not implements any interface");
		checkPlugs(this.plugs);
	}
	public static <T> Class<?>[] getPlugs(Class<?> clzz,String declareRegister){
		Set<Class<?>> set = new HashSet<Class<?>>();
		String[] strs = declareRegister.split(",");
		for(String str :strs){
			if(str.trim().equals("*")){
				Class<?>[] plugs = clzz.getInterfaces();
				for(Class<?> plug : plugs)
					set.add(plug);
			}else{
				try {
					Class<?> interfacer = Class.forName(str.trim());
					if(ClassLoader.implementsOf(clzz, interfacer))//判断类及父类是否实现某接口
						set.add(interfacer);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		Class<?>[] plugs = new Class<?>[set.size()];
		int i = 0;
		for(Class<?> plug : set){
			plugs[i] = plug;
			i++;
		}
		return plugs;
	}
	public static void checkPlugs(Class<?>[] plugs){
		for(Class<?> plug : plugs){
			if(PlugsFactory.getInstance().getPlug(plug)==null)
				PlugsFactory.getInstance().addPlugsByDefault(plug);
		}
	}
	public static Class<?>[] getPlugs(Class<?> clzz,Class<?>[] declareRegister){
		Class<?>[] plugs;
		if(declareRegister.length==0)
			plugs = clzz.getInterfaces();
		else{
			plugs = new Class<?>[declareRegister.length];
			int index = 0;
			Class<?>[] cls = clzz.getInterfaces();
			for(int i = 0;i<cls.length;i++){
				for(int j = 0;j<plugs.length;j++){
					if(declareRegister[j].equals(cls[i].getName())){
						plugs[index]=cls[i];
						index++;
					}
				}
			}
		}
		return plugs;
	}
	public RegisterDescription(File file) throws Exception {
			try {
				this.setFile(file);
				String fileName = file.getName();
				String clzzStr = fileName.substring(0,fileName.lastIndexOf("."));
				InputStream in = new BufferedInputStream(new FileInputStream(file));   
				Properties p = new Properties();   
				p.load(in);
				//读取属性
				this.priority = Integer.valueOf(p.getProperty("comps.priority", "0"));
				this.signlton = Boolean.valueOf(p.getProperty("comps.signlton", "true"));
				this.attribute = p.getProperty("comps.attribute", "*");
				this.description = p.getProperty("comps.description", "");
				String className = p.getProperty("comps.class", clzzStr);
				String declareRegister = p.getProperty("comps.register", "*");
				//获取实现类
				this.clzz= new ClassLoader(className,false).getLoadedClass();
				//获取实现类所在的接口
				this.plugs = getPlugs(clzz, declareRegister);
				if(this.plugs==null||this.plugs.length==0)
					throw new Exception("register "+clzz.getName()+" not implements any interface");
				checkPlugs(this.plugs);
			} catch (Exception e) {
				throw new Exception("plugin "+file.getName()+" init failed",e);
			}
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

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public boolean isSignlton() {
		return signlton;
	}

	public void setSignlton(boolean signton) {
		this.signlton = signton;
	}

	public Object getRegisterInstance(Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object object;
		if(args.length==0){
			if(this.signlton){
				object = instanceContainer.get(hash(this.clzz,args));
				if(object==null){
					object = this.clzz.newInstance();
					instanceContainer.put(hash(this.clzz,args), object);
				}
			}else{
				object = this.clzz.newInstance();
			}
		}else{
			Class<?>[] parameterTypes = com.YaNan.frame.reflect.ClassLoader.getParameterTypes(args);
			Constructor<?> constructor =CacheContainer.getClassInfoCache(this.clzz).getConstructor(parameterTypes);//this.cl.getConstructor(parameterTypes);
			object = constructor.newInstance(args);
		}
		return object;
	}
	public static int hash(Class<?> clzz,Object...objects){
		return clzz.hashCode()+hash(objects);
	}
	public static int hash(Object...objects){
		int hash = 0;
		for(int i = 0;i<objects.length;i++)
			hash+=objects[i].hashCode();
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
	public void setDescription(String description) {
		this.description = description;
	}

}
