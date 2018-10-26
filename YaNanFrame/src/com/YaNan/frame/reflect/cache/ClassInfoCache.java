package com.YaNan.frame.reflect.cache;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 类信息缓存类，提供ClassHelper的缓存。
 * 支持ClassLoader的部分方法缓存
 * @author yanan
 *
 */
public class ClassInfoCache {
	private static Map<Class<?>,ClassHelper> classCache = new LinkedHashMap<Class<?>,ClassHelper>();
	private static Map<String,String> fieldAddMethodCache = new LinkedHashMap<String,String>();
	private static Map<String,String> fieldSetMethodCache = new LinkedHashMap<String,String>();
	private static Map<String,String> fieldGetMethodCache = new LinkedHashMap<String,String>();
	private static Map<String,Class<?>> classMapCache = new LinkedHashMap<String,Class<?>>();
//	public static ClassHelpers getClassHelpers(Class<?> clzz){
//		return classCache.get(clzz);
//	}
	/**
	 * 获取一个类的ClassHelper
	 * @param clzz
	 * @return
	 */
	public static ClassHelper getClassHelper(Class<?> clzz){
		if(classCache.get(clzz)!=null)
			return classCache.get(clzz);
		ClassHelper cache = new ClassHelper(clzz);
		classCache.put(clzz, cache);
		return cache;
	}
	public static String getFieldAddMethod(String name) {
		
		String str = fieldAddMethodCache.get(name);
		if(str==null){
			str=new StringBuilder("add").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).toString();
			fieldAddMethodCache.put(name, str);
		}
		return str;
	}
	public static String getFieldSetMethod(String name) {
		String str = fieldSetMethodCache.get(name);
		if(str==null){
			str=new StringBuilder("set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).toString();
			fieldSetMethodCache.put(name, str);
		}
		return str;
	}
	public static String getFieldGetMethod(String name) {
		String str = fieldGetMethodCache.get(name);
		if(str==null){
			str=new StringBuilder("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).toString();
			fieldGetMethodCache.put(name, str);
		}
		return str;
	}
	public static Method getMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Class<?> classForName(String className) throws ClassNotFoundException {
		Class<?> clzz = classMapCache.get(className);
		if(clzz==null){
			clzz=Class.forName(className);
			classMapCache.put(className, clzz);
		}
		return clzz;
	}
}
