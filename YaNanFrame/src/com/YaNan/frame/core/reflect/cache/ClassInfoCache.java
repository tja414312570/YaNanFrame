package com.YaNan.frame.core.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassInfoCache {
	private Class<?> cacheClass;
	private Map<Integer,Method> methods=new HashMap<Integer,Method>();//方法缓存
	private Map<Integer,Method> declaredMethods=new HashMap<Integer,Method>();//方法缓存
	private Map<String,Field> fields=new HashMap<String,Field>();//域缓存
	private Map<String,Field> declaredFields=new HashMap<String,Field>();//域缓存
	private Map<Integer,Constructor<?>> declaredConstructors = new HashMap<Integer,Constructor<?>>();
	private Map<Integer,Constructor<?>> constructors = new HashMap<Integer,Constructor<?>>();
	private Map<Class<?>,Annotation> declaredAnnotations = new HashMap<Class<?>,Annotation>();
	private Map<Class<?>,Annotation> annotations = new HashMap<Class<?>,Annotation>();
	public ClassInfoCache(Class<?> cls){
		this.setCacheClass(cls);
		//DeclaredMethod
		Method[] declaredMethods = cls.getDeclaredMethods();
		for(Method method : declaredMethods)
			this.declaredMethods.put(hash(method.getName(),method.getParameterTypes()), method);
		//Method
		Method[] methods = cls.getMethods();
		for(Method method : methods)
			this.methods.put(hash(method.getName(),method.getParameterTypes()), method);
		//DeclaredField
		Field[] declaredFields = cls.getDeclaredFields();
		for(Field field : declaredFields)
			this.declaredFields.put(field.getName(), field);
		//fields
		Field[] fields = cls.getFields();
		for(Field field : fields)
			this.fields.put(field.getName(), field);
		//declaredConstructor
		Constructor<?>[] declaredConstructors = cls.getDeclaredConstructors();
		for(Constructor<?> constructor : declaredConstructors)
			this.declaredConstructors.put(hash(constructor.getParameterTypes()), constructor);
		//constructor
		Constructor<?>[] constructors = cls.getConstructors();
		for(Constructor<?> constructor : constructors)
			this.constructors.put(hash(constructor.getParameterTypes()), constructor);
		//declaredAnnotations
		Annotation[] declaredAnnotations= cls.getDeclaredAnnotations();
		for(Annotation annotation : declaredAnnotations)
			this.declaredAnnotations.put(annotation.getClass(), annotation);
		//annotations
		Annotation[] annotations=cls.getAnnotations();
		for(Annotation annotation : annotations)
			this.annotations.put(annotation.getClass(), annotation);
	}
	public Annotation getAnnotation(Class<?> cls){
		return this.annotations.get(cls);
	}
	public Annotation getDeclaredAnnotation(Class<?> cls){
		return this.declaredAnnotations.get(cls);
	}
	public Method getMethod(String name,Class<?>...parameterTypes){
		return this.methods.get(hash(name,parameterTypes));
	}
	public Method getDeclaredMethod(String name,Class<?>...parameterTypes){
		return this.declaredMethods.get(hash(name,parameterTypes));
	}
	public Field getField(String fieldName){
		return this.fields.get(fieldName);
	}
	public Field getDeclaredField(String fieldName){
		return this.declaredFields.get(fieldName);
	}
	public Constructor<?> getConstructor(Class<?>...parameterTypes){
		return this.constructors.get(hash(parameterTypes));
	}
	public Constructor<?> getDedlaredConstructor(Class<?>...parameterTypes){
		return this.declaredConstructors.get(hash(parameterTypes));
	}
	
	public static int hash(Class<?>...clzz){
		int hash = 0;
		for(Object object:clzz)
			hash+=object.hashCode();
		return hash;
	}
	public static int hash(String name,Class<?>...parameterTypes){
		return name.hashCode()+hash(parameterTypes);
	}
	public Class<?> getCacheClass() {
		return cacheClass;
	}
	public void setCacheClass(Class<?> cacheClass) {
		this.cacheClass = cacheClass;
	}
	public Collection<Method> getMethods() {
		return this.methods.values();
	}
	public Collection<Method> getDeclaredMethods() {
		return this.declaredMethods.values();
	}
	public Collection<Field> getFields() {
		return this.fields.values();
	}
	public Collection<Field> getDeclaredFields() {
		return this.declaredFields.values();
	}
}
