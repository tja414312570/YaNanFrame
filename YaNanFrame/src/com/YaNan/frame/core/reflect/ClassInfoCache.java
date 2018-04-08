package com.YaNan.frame.core.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassInfoCache {
	private Class<?> cacheClass;
	private static Map<Class<?>,ClassInfoCache> classInfoCaches = new HashMap<Class<?>,ClassInfoCache>();
	private Map<Integer,Method> methodCaches=new HashMap<Integer,Method>();
	private ClassInfoCache(Class<?> cls){
		this.cacheClass = cls;
	}
	public static ClassInfoCache getClassInfoCache(Class<?> cls){
		ClassInfoCache classInfoCache = classInfoCaches.get(cls);
		if(classInfoCache!=null)
			return classInfoCache;
		classInfoCache = new ClassInfoCache(cls);
		classInfoCaches.put(cls, classInfoCache);
		return classInfoCache;
	}
	public Method getMethod(String methodName,Class<?>[] parameterTypes) throws NoSuchMethodException, SecurityException{
		int methodHash =methodName.hashCode()+parameterTypes.hashCode();
		Method method = this.methodCaches.get(methodHash);
		if(method!=null)
			return method;
		method = this.cacheClass.getMethod(methodName, parameterTypes);
		this.methodCaches.put(methodHash, method);
		return method;
	}
}
