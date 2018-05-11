package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassInfoCache {
	private Class<?> cacheClass;
	private Map<Integer,Method> methodMap=new HashMap<Integer,Method>();//方法缓存
	private Map<Integer,Method> declaredMethodMap=new HashMap<Integer,Method>();//方法缓存
	private Map<String,Field> fieldMap=new HashMap<String,Field>();//域缓存
	private Map<String,Field> declaredFieldMap=new HashMap<String,Field>();//域缓存
	private Map<Integer,Constructor<?>> declaredConstructorMap = new HashMap<Integer,Constructor<?>>();
	private Map<Integer,Constructor<?>> constructorMap = new HashMap<Integer,Constructor<?>>();
	private Map<Class<?>,Annotation> declaredAnnotationMap = new HashMap<Class<?>,Annotation>();
	private Map<Class<?>,Annotation> annotationMap = new HashMap<Class<?>,Annotation>();
	private Method[] methods;
	private Method[] declaredMethods;
	private Field[] fields;//域缓存
	private Field[] declaredFields;//域缓存
	private Constructor<?>[] declaredConstructors;
	private Constructor<?>[] constructors ;
	private Annotation[] declaredAnnotations;
	private Annotation[] annotations;
	public ClassInfoCache(Class<?> cls){
		this.setCacheClass(cls);
		//DeclaredMethod
		declaredMethods = cls.getDeclaredMethods();
		for(Method method : declaredMethods)
			this.declaredMethodMap.put(hash(method.getName(),method.getParameterTypes()), method);
		//Method
		methods = cls.getMethods();
		for(Method method : methods)
			this.methodMap.put(hash(method.getName(),method.getParameterTypes()), method);
		//DeclaredField
		declaredFields = cls.getDeclaredFields();
		for(Field field : declaredFields)
			this.declaredFieldMap.put(field.getName(), field);
		//fieldMap
		fields = cls.getFields();
		for(Field field : fields)
			this.fieldMap.put(field.getName(), field);
		//declaredConstructor
		declaredConstructors = cls.getDeclaredConstructors();
		for(Constructor<?> constructor : declaredConstructors)
			this.declaredConstructorMap.put(hash(constructor.getParameterTypes()), constructor);
		//constructor
		constructors = cls.getConstructors();
		for(Constructor<?> constructor : constructors)
			this.constructorMap.put(hash(constructor.getParameterTypes()), constructor);
		//declaredAnnotationMap
		declaredAnnotations= cls.getDeclaredAnnotations();
		for(Annotation annotation : declaredAnnotations)
			this.declaredAnnotationMap.put(annotation.getClass(), annotation);
		//annotationMap
		annotations=cls.getAnnotations();
		for(Annotation annotation : annotations)
			this.annotationMap.put(annotation.getClass(), annotation);
	}
	public Annotation getAnnotation(Class<?> cls){
		return this.annotationMap.get(cls);
	}
	public Annotation getDeclaredAnnotation(Class<?> cls){
		return this.declaredAnnotationMap.get(cls);
	}
	public Method getMethod(String name,Class<?>...parameterTypes){
		return this.methodMap.get(hash(name,parameterTypes));
	}
	public Method getDeclaredMethod(String name,Class<?>...parameterTypes){
		return this.declaredMethodMap.get(hash(name,parameterTypes));
	}
	public Field getField(String fieldName){
		return this.fieldMap.get(fieldName);
	}
	public Field getDeclaredField(String fieldName){
		return this.declaredFieldMap.get(fieldName);
	}
	public Constructor<?> getConstructor(Class<?>...parameterTypes){
		return this.constructorMap.get(hash(parameterTypes));
	}
	public Constructor<?> getDedlaredConstructor(Class<?>...parameterTypes){
		return this.declaredConstructorMap.get(hash(parameterTypes));
	}
	
	public static int hash(Class<?>...clzz){
		int hash = 0;
		for(Class<?> clz:clzz)
			hash+=clz.getName().hashCode();
		return hash;
	}
	public static int hash(String name,Class<?>...parameterTypes){
		return name.hashCode()+hash(parameterTypes);
	}
	public static String hashString(String name,Class<?>...parameterTypes){
		return  new StringBuilder(name).append(hashString(parameterTypes)).toString();
	}
	public static String hashString(Class<?>...clzz){
		StringBuilder sb = new StringBuilder();
		for(Class<?> clz:clzz)
			sb.append(clz.getName());
		return sb.toString();
	}
	public Class<?> getCacheClass() {
		return cacheClass;
	}
	public void setCacheClass(Class<?> cacheClass) {
		this.cacheClass = cacheClass;
	}
	public Method[] getMethods() {
		return this.methods;
	}
	public Method[] getDeclaredMethods() {
		return this.declaredMethods;
	}
	public Field[] getFields() {
		return this.fields;
	}
	public Field[] getDeclaredFields() {
		return this.declaredFields;
	}
}
