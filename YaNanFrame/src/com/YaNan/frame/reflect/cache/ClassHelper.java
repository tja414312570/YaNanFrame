package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为程序在框架中方便高效调用反射提供缓存支持</br>
 * 作为{@link com.YaNan.frame.reflect.ClassLoader}的底层,为其提供访问Class文件缓存</br>
 * 支持获取Field,Method,Annotation等常用方法，和系统反射方法名相同</br>
 * 我们建议使用helper来获取Class文件的Field、Method等等信息。</br>
 * @author yanan
 * 20181011 提供获取类及其父类中所有的Field，方法getAllFields();
 * 20181221 新增getClassHelper方法
 */
public class ClassHelper {
	private Class<?> cacheClass;
	private Map<Integer,Method> methodMap=new HashMap<Integer,Method>();//方法缓存
	private Map<Integer,Method> declaredMethodMap=new HashMap<Integer,Method>();//方法缓存
	private Map<Method,MethodHelper> methodHelpers = new HashMap<Method,MethodHelper>();
	private Map<String,Field> fieldMap=new HashMap<String,Field>();//域缓存
	private Map<String,Field> declaredFieldMap=new HashMap<String,Field>();//域缓存
	private Map<String,Field> allFieldMap;//所有的域缓存
	private Map<Field,FieldHelper> fieldHelpers = new HashMap<Field,FieldHelper>();
	private Map<Integer,Constructor<?>> declaredConstructorMap = new HashMap<Integer,Constructor<?>>();
	private Map<Integer,Constructor<?>> constructorMap = new HashMap<Integer,Constructor<?>>();
	private Map<Constructor<?>,ConstructorHelper> constructorHelpers = new HashMap<Constructor<?>,ConstructorHelper>();
	
	private Map<Class<?>,Annotation> declaredAnnotationMap = new HashMap<Class<?>,Annotation>();
	private Map<Class<?>,Annotation> annotationMap = new HashMap<Class<?>,Annotation>();
	
	private Method[] methods;
	private Method[] declaredMethods;
	private Field[] fields;//域缓存
	private Field[] declaredFields;//域缓存
	private Field[] allFields;//所有Field缓存
	private Constructor<?>[] declaredConstructors;
	private Constructor<?>[] constructors ;
	private Annotation[] declaredAnnotations;
	private Annotation[] annotations;
	
	public static ClassHelper getClassHelper(Class<?> clzz){
		return ClassInfoCache.getClassHelper(clzz);
	}
	public ClassHelper(Class<?> cls){
		this.setCacheClass(cls);
		//DeclaredMethod
		declaredMethods = cls.getDeclaredMethods();
		for(Method method : declaredMethods){
			this.declaredMethodMap.put(hash(method.getName(),method.getParameterTypes()), method);
			this.methodHelpers.put(method, new MethodHelper(method));
		}
		//Method
		methods = cls.getMethods();
		for(Method method : methods){
			this.methodMap.put(hash(method.getName(),method.getParameterTypes()), method);
			this.methodHelpers.put(method, new MethodHelper(method));
		}
		//DeclaredField
		declaredFields = cls.getDeclaredFields();
		for(Field field : declaredFields){
			this.declaredFieldMap.put(field.getName(), field);
			fieldHelpers.put(field,new FieldHelper(field));
		}
		//fieldMap
		fields = cls.getFields();
		for(Field field : fields){
			fieldMap.put(field.getName(), field);
			fieldHelpers.put(field,new FieldHelper(field));
		}
		//declaredConstructor
		declaredConstructors = cls.getDeclaredConstructors();
		for(Constructor<?> constructor : declaredConstructors)
			this.declaredConstructorMap.put(hash(constructor.getParameterTypes()), constructor);
		//constructor
		constructors = cls.getConstructors();
		for(Constructor<?> constructor : constructors){
			this.constructorMap.put(hash(constructor.getParameterTypes()), constructor);
			this.constructorHelpers.put(constructor, new ConstructorHelper(constructor));
		}
		//declaredAnnotationMap
		declaredAnnotations= cls.getDeclaredAnnotations();
		for(Annotation annotation : declaredAnnotations)
			this.declaredAnnotationMap.put(annotation.annotationType(), annotation);
		//annotationMap
		annotations=cls.getAnnotations();
		for(Annotation annotation : annotations)
			this.annotationMap.put(annotation.annotationType(), annotation);
	}
	/**
	 * 获取所有的Field，包括该类所有Field及其父类所有的Field
	 * 包括protected，private，public，default修饰的Field
	 * @param endClass
	 * @return
	 */
	public Field[] getAllFields(Class<?> endClass){
		if(endClass==null)
			endClass = Object.class;
		Class<?> tempClass = this.cacheClass;
		List<Field> fieldList = new ArrayList<Field>();
		boolean find=tempClass.equals(endClass)?true:false;
		while(!find){
			Field[] fields = ClassInfoCache.getClassHelper(tempClass).getDeclaredFields();
			for(Field field : fields)
				fieldList.add(field);
			if((tempClass = tempClass.getSuperclass()).equals(endClass))
					find = true;
		}
		return fieldList.toArray(new Field[]{});
	}
	/**
	 * 获取所有的Field，包括该类所有Field及其父类所有的Field
	 * 包括protected，private，public，default修饰的Field
	 * @return
	 */
	public Field[] getAllFields(){
		if(this.allFields==null)
			allFields = this.buildAllFields();
		return this.allFields;
	}
	/**
	 * 获取所有的Field，包括该类所有Field及其父类所有的Field
	 * 包括protected，private，public，default修饰的Field
	 * @param target
	 * @param endClass
	 * @return
	 */
	private Field[] buildAllFields(){
		this.allFieldMap = new HashMap<String,Field>();
		Class<?> endClass = Object.class;
		Class<?> tempClass = this.cacheClass;
		List<Field> fieldList = new ArrayList<Field>();
		boolean find=tempClass.equals(endClass)?true:false;
		while(!find){
			Field[] fields = ClassInfoCache.getClassHelper(tempClass).getDeclaredFields();
			for(Field field : fields){
				fieldList.add(field);
				fieldHelpers.put(field,new FieldHelper(field));
				allFieldMap.put(field.getName(), field);
			}
			if((tempClass = tempClass.getSuperclass()).equals(endClass))
					find = true;
		}
		return fieldList.toArray(new Field[]{});
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> cls){
		return (T) this.annotationMap.get(cls);
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> cls){
		return (T) this.declaredAnnotationMap.get(cls);
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
	/**
	 * 获取一个Field，包括其父类包含的所有的Field
	 * @param fieldName
	 * @return
	 */
	public Field getAnyField(String fieldName){
		if(this.allFieldMap==null)
			this.buildAllFields();
		return this.allFieldMap.get(fieldName);
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
	/**
	 * 此部分用于获取Field method constructor的helper
	 */
	public Map<Field, FieldHelper> getFieldHelperMap(){
		return this.fieldHelpers;
	}
	public Map<Method,MethodHelper> getMethodHelperMap(){
		return this.methodHelpers;
	}
	public Map<Constructor<?>,ConstructorHelper> getConstructorHelperMap(){
		return this.constructorHelpers;
	}
	public FieldHelper getFieldHelper(Field field){
		return this.fieldHelpers==null?null:this.fieldHelpers.get(field);
	}
	public MethodHelper getMethodHelper(Method method){
		return this.methodHelpers==null?null:this.methodHelpers.get(method);
	}
	public ConstructorHelper getConstructorHelper(Constructor<?> constructor){
		return this.constructorHelpers==null?null:this.constructorHelpers.get(constructor);
	}
	public static int hash(Class<?>...clzz){
		int hash = 0;
		for(Class<?> clz:clzz)
			hash+=clz==null?1:clz.getName().hashCode();
		return hash;
	}
	public static int hash(Parameter parameter,Annotation annotation){
		return parameter.hashCode()+annotation.hashCode();
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
