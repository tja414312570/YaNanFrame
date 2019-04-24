package com.YaNan.frame.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.YaNan.frame.reflect.cache.ClassHelper;
import com.YaNan.frame.reflect.cache.ClassInfoCache;

/**
 * ClassLoader for YaNan.frame 该类加载器是YaNan应用的核心处理机制之一，用于对应用内部实体、类等的控制
 * @author Administrator
 * @version 1.0.1
 * @author YaNan
 *
 */
public class ClassLoader extends java.lang.ClassLoader{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Object loadObject;
	private Class<?> loadClass;
	private ClassHelper infoCache = null;

	public ClassHelper getInfoCache() {
		return infoCache;
	}

	/*
	 * static method
	 */
	public Object getLoadObject() {
		return loadObject;
	}

	public void setLoadObject(Object loadObject) {
		this.loadObject = loadObject;
	}
	/**
	 * 判断类是否存在，参数 完整类名
	 * 
	 * @param className
	 * @return
	 */
	public static boolean exists(String className) {
		try {
			ClassInfoCache.classForName(className);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获得类下所有公开的属性 传入 完整类名
	 * 
	 * @param className
	 * @return
	 */
	public static Field[] getFields(String className) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得类下所有属性 参数 完整类名
	 * 
	 * @param className
	 * @return
	 */
	public static Field[] getAllFields(String className) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getDeclaredFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断公开的方法是否存在，参数 完整类名，方法名，参数类型（可选）
	 * 
	 * @param ClassName
	 * @param Method
	 * @param args
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean hasMethod(String className, String methodName, Class<?>... args) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getMethod(methodName,
					args) != null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断方法是否存在，参数 完整类名，方法名，参数类型（可选）
	 * 
	 * @param ClassName
	 * @param Method
	 * @param args
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean hasDeclaredMethod(String className, String methodName, Class<?>... args) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getMethod(methodName,
					args) != null;
		} catch (SecurityException | ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 获得参数的类型，至少传入一个参数
	 * 
	 * @param args
	 * @return
	 */
	public static Class<?>[] getParameterTypes(Object... args) {
		Class<?>[] parmType = new Class[args.length];
		for (int i = 0; i < args.length; i++)
			parmType[i] = args[i]==null?null:args[i].getClass();
		return parmType;
	}

	public static Class<?>[] getParameterBaseType(Object... args) {
		Class<?>[] parmType = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			parmType[i] = patchBaseType(args[i]);
		return parmType;
	}

	/**
	 * 获取方法返回值类型
	 * 
	 * @param method
	 * @return
	 */
	public static Class<?> getMethodReturnType(Method method) {
		return method.getReturnType();
	}

	/**
	 * 创建属性的get方法
	 * 
	 * @param name
	 * @return
	 */
	public static String createFieldGetMethod(String field) {
		return ClassInfoCache.getFieldGetMethod(field);
	}

	public static String createFieldGetMethod(Field field) {
		return ClassInfoCache.getFieldGetMethod(field.getName());
	}

	/**
	 * 创建属性的set方法
	 * 
	 * @param name
	 * @return
	 */
	public static String createFieldSetMethod(String field) {
		return ClassInfoCache.getFieldSetMethod(field);
	}

	public static String createFieldSetMethod(Field field) {
		return ClassInfoCache.getFieldSetMethod(field.getName());
	}

	/**
	 * 创建属性的add方法
	 * 
	 * @param name
	 * @return
	 */
	public static String createFieldAddMethod(String name) {
		return ClassInfoCache.getFieldAddMethod(name);
	}

	/**
	 * 获取类的公开方法，传入String类名，String方法名，参数类型数组（可选）
	 * 
	 * @param cls
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return ClassInfoCache.getClassHelper(cls).getMethod(methodName, parameterTypes);
	}

	/**
	 * 获取类的方法，传入String类名，String方法名，参数类型数组（可选）
	 * 
	 * @param cls
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return ClassInfoCache.getClassHelper(cls).getDeclaredMethod(methodName, parameterTypes);
	}

	/*
	 * static method end
	 */

	/*
	 * constructs start
	 */
	/**
	 * 默认构造器，不传入任何参数，要使用请先调用loadClass或loadObjcet
	 */
	public ClassLoader() {
	};

	/**
	 * 默认构造器，传入一个Object型的对象，此构造器将会加载Object和Object的Class
	 * 
	 * @param object
	 */
	public ClassLoader(Object object) {
		this.loadObject = object;
		this.loadClass = this.loadObject.getClass();
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
	}

	/**
	 * 默认构造器，传入一个Object型的对象和boolean型是否创建类的实例 ， 此构造器将会加载Class
	 * 
	 * @param cls
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ClassLoader(Class<?> cls) {
		this.loadClass = cls;
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
		try {
			this.loadObject = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 默认构造器，传入一个Object型的对象和boolean型是否创建类的实例
	 * ，如果boolean为此构造器将会加载Object和Object的Class
	 * 
	 * @param cls
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ClassLoader(Class<?> cls, boolean instance) {
		this.loadClass = cls;
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
		try {
			if (instance)
				this.loadObject = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 默认构造器，传入一个String型的完整类名，如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public ClassLoader(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.loadClass = Class.forName(className);
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
		this.loadObject = this.loadClass.newInstance();
	}

	/**
	 * 默认构造器，传入一个String型的完整类名和一个boolean型是否创建实例， 如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className
	 * @param instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public ClassLoader(String className, boolean instance)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.loadClass = Class.forName(className);
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
		if (instance)
			this.loadObject = this.loadClass.newInstance();
	}

	/**
	 * 默认构造器，传入一个String型的完整类名，如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className
	 * @param args
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public ClassLoader(String className, Object... args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
		if (args.length == 0) {
			this.loadObject = this.loadClass.newInstance();
		} else {
			this.loadObject = this.Instance(args);
		}

	}

	/**
	 * 创建一个类的实例，此方法在类被加载到该加载器后才能试用
	 * 
	 * @param Method
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object Instance(String Method, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.loadObject = this.invokeMethod(Method, args);
		return this.loadObject;
	}

	/**
	 * 创建类的实例，需要先加载类到该加载器，所需类的默认构造器的参数
	 * 
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public Object Instance(Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		Constructor<?> cls = this.infoCache.getConstructor(getParameterTypes(args));
		this.loadObject = cls.newInstance(args);
		return this.loadObject;
	}

	/**
	 * 加载对象到该加载器，传入完整String完整类名，如果该类实例化需要 参数，请依次传入参数，否则第二个参数为空
	 * 
	 * @param className
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void loadObject(String className, Object... args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
		if (args.length == 0) {
			this.loadObject = this.loadClass.newInstance();
		} else {
			this.loadObject = this.Instance(args);
		}
	}

	/**
	 * 加载对象到加载器，同时加载该对象的类，传入对象的实例
	 * 
	 * @param object
	 */
	public void loadObject(Object object) {
		this.loadObject = object;
		this.loadClass = this.loadObject.getClass();
	}

	/**
	 * 获得加载器内加载的类
	 * 
	 * @return
	 */
	public Class<?> getLoadedClass() {
		return this.loadClass;
	}

	/**
	 * 获得加载器内的加载的实例
	 * 
	 * @return
	 */
	public Object getLoadedObject() {
		return this.loadObject;
	}

	/*
	 * Field
	 */
	/**
	 * 获得加载器内加载类的所有公开的属性
	 * 
	 * @return
	 */
	public Field[] getFields() {
		return this.infoCache.getFields();
	}

	/**
	 * 获得加载器内加载类的所有属性
	 * 
	 * @return
	 */
	public Field[] getDeclaredFields() {
		return this.infoCache.getDeclaredFields();
	}

	/**
	 * 判断加载器内加载的类中是否有某个公开的属性,传入属性名
	 * 
	 * @param field
	 * @return
	 */
	public boolean hasField(String field) {
		return this.infoCache.getField(field) != null;
	}

	/**
	 * 判断加载器内加载的类中是否有某个属性，传入属性名
	 * 
	 * @param field
	 * @return
	 */
	public boolean hasDeclaredField(String field) {
		return this.infoCache.getDeclaredField(field) != null;
	}

	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object getFieldValue(String fieldName) throws IllegalAccessException {
		Field field = this.infoCache.getField(fieldName);
		return getFieldValue(field);
	}

	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object getFieldValue(Field field) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object result = field.get(this.loadObject);
		return result;
	}

	public Object getDeclaredFieldValue(String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = this.infoCache.getDeclaredField(fieldName);
		return getFieldValue(field);
	}

	/**
	 * 设置加载器内加载的对象的属性值，传入String型属性名，任意类型的值
	 * 
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void setFieldValue(String field, Object value) throws IllegalAccessException {
		Field f = this.infoCache.getDeclaredField(field);
		setFieldValue(f, value);
	}

	public void setDeclaredFieldValue(String field, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = this.infoCache.getDeclaredField(field);
		setFieldValue(f, value);
	}

	/**
	 * 设置加载器内加载的对象的属性值，传入Field型属性名，任意类型的值
	 * 
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void setFieldValue(Field field, Object value) throws IllegalAccessException {
		field.setAccessible(true);
		field.set(this.loadObject, value);
	}

	/*
	 * field set or get,only used by servletDispatcher
	 */
	/**
	 * 该方法用于直接设置某个属性的值，传入field name 和 value 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param field
	 * @param arg
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void set(String fieldName, Object arg) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Field field = this.getDeclaredField(fieldName);
		this.set(field, arg);
	}

	/**
	 * 该方法用于直接设置某个属性的值，传入Field 和 value 优先通过Set方式获取，没有则通过直接获取
	 * 
	 * @param field
	 * @param arg
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void set(Field field, Object arg) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String method = ClassLoader.createFieldSetMethod(field);
		if (this.hasMethod(method, field.getType())) {
			Class<?>[] parameter = new Class<?>[1];
			parameter[0] = field.getType();
			invokeMethod(method, parameter, castType(arg, field.getType()));
		} else {
			setFieldValue(field, castType(arg, field.getType()));
		}
	}

	/**
	 * 该方法用于直接设置某个属性的值，传入String属性，String 值
	 * 
	 * @param field
	 * @param arg
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void set(String field, Class<?> parameterType, Object arg) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] parameter = new Class<?>[1];
		parameter[0] = parameterType;
		invokeMethod(createFieldSetMethod(field), parameter, arg);
	}

	/**
	 * 该方法用于直接获取某个属性的值，传入field name 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param field
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public Object get(String field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String method = ClassLoader.createFieldGetMethod(field);
		if (this.hasMethod(method)) {
			return invokeMethod(method);
		} else {
			return this.getFieldValue(field);
		}
	}

	/**
	 * 该方法用于直接获取某个属性的值，传入Field 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param method
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public Object get(Field field) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		String method = ClassLoader.createFieldGetMethod(field);
		if (this.hasMethod(method)) {
			return invokeMethod(method);
		} else {
			return this.getFieldValue(field);
		}
	}

	/*
	 * invoke method
	 */
	/**
	 * 调用加载器内加载对象的某个方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invokeMethod(String methodName, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invokeMethod(methodName, getParameterBaseType(args), args);
	}

	/**
	 * 调用加载器内加载对象的某个方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invokeMethod(String methodName, Class<?>[] parameterType, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMethod(this.loadObject, methodName, parameterType, args);
	}

	public Object invokeMethod(Object object, String methodName, Class<?>[] parameterType, Object... args)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = this.infoCache.getMethod(methodName, parameterType);
		if(method==null)
			method = this.infoCache.getDeclaredMethod(methodName, parameterType);
		if (method == null) {
			StringBuilder ptStr = new StringBuilder(this.loadClass.getName()).append(".").append(methodName)
					.append("(");
			for (int i = 0; i < parameterType.length; i++) {
				ptStr.append(parameterType[i].getName());
				if (i < parameterType.length - 1)
					ptStr.append(".");
			}
			throw new NoSuchMethodException(ptStr.append(")").toString());
		}
		method.setAccessible(true);
		return method.invoke(object, args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invokeStaticMethod(String methodName, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invokeMethod(null, methodName, getParameterTypes(args), args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeStaticMethod(Class<?> clzz, String methodName, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = ClassInfoCache.getClassHelper(clzz).getMethod(methodName, getParameterBaseType(args));
		return method.invoke(null, args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeStaticMethod(Class<?> clzz, String methodName, Class<?>[] parameterTypes, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = ClassInfoCache.getClassHelper(clzz).getMethod(methodName, parameterTypes);
		return method.invoke(null, args);
	}

	/*
	 * Method
	 */
	// get method
	/**
	 * 获得加载器内加载类的某个方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method
	 * @param parameterType
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method getDeclaredMethod(String method, Class<?>... parameterType)
			throws NoSuchMethodException, SecurityException {
		return this.infoCache.getDeclaredMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的某个公开的方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method
	 * @param parameterType
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method getMethod(String method, Class<?>... parameterType){
		return this.infoCache.getMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的所有公开的方法
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method[] getMethods() {
		return this.infoCache.getMethods();
	}

	/**
	 * 获得加载器内加载类的所有方法
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method[] getDeclaredMethods() {
		return this.infoCache.getDeclaredMethods();
	}

	// judge method
	/**
	 * 判断加载器内加载的类是否有某个方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method
	 * @param parameterType
	 * @return
	 */
	public boolean hasDeclaredMethod(String method, Class<?>... parameterType) {
		return this.infoCache.getDeclaredMethod(method, parameterType) != null;
	}

	/**
	 * 判断加载器内加载的类是否有某个公开的方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method
	 * @param parameterType
	 * @return
	 */
	public boolean hasMethod(String method, Class<?>... parameterType) {
		return this.infoCache.getMethod(method, parameterType) != null;
	}

	public Field getDeclaredField(String field) {
		return this.infoCache.getDeclaredField(field);
	}

	public static boolean clone(Object target, Object source) {
		if (target.getClass() == source.getClass()) {
			DisClone(target, source);
		}
		return false;
	}

	public static void DisClone(Object target, Object source) {
		Field[] fields = target.getClass().getDeclaredFields();
		Class<?> sCls = source.getClass();
		try {
			for (Field f : fields) {
				Field sField = null;
				try {
					sField = sCls.getDeclaredField(f.getName());
				} catch (NoSuchFieldException e1) {
					continue;
				}
				sField.setAccessible(true);
				f.setAccessible(true);
				f.set(target, sField.get(source));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static <T> T clone(Class<T> target, Object source) {
		try {
			T object = target.newInstance();
			Field[] fields = target.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				Field fs;
				try {
					fs = source.getClass().getDeclaredField(f.getName());
					fs.setAccessible(true);
					f.set(object, fs.get(source));
				} catch (NoSuchFieldException | SecurityException e) {
					try {
						Method fgm = source.getClass().getMethod(createFieldGetMethod(f.getName()));
						if (fgm != null)
							f.set(object, fgm.invoke(source));
					} catch (NoSuchMethodException | SecurityException | InvocationTargetException e1) {
						continue;
					}
				}
			}
			return object;
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断一个类是否继承自某个接口，不支持包含父类继承的接口的继承 eg. class A implements B{}
	 * implementOf(A.class,B.class) ==>true class A implements B{},class C
	 * extends A{}; implementOf(C.class,B.class) ==>false
	 * 
	 * @param 要判断的类
	 * @param 要验证实现的接口
	 * @return
	 */
	public static boolean implementOf(Class<?> orginClass, Class<?> interfaceClass) {
		Class<?>[] cls = orginClass.getInterfaces();
		for (Class<?> cCls : cls)
			if (cCls.equals(interfaceClass))
				return true;
		return false;
	}

	/**
	 * 判断一个类是否继承自某个接口，支持包含父类继承的接口的继承 class A implements B{}
	 * implementOf(A.class,B.class) ==>true class A implements B{},class C
	 * extends A{}; implementOf(C.class,B.class) ==>true
	 * 
	 * @param 要判断的类
	 * @param 要验证实现的接口
	 * @return
	 */
	public static boolean implementsOf(Class<?> orginClass, Class<?> interfaceClass) {
		Class<?> tempClass = orginClass;
		while (tempClass != null && !tempClass.equals(Object.class)) {
			if (tempClass.equals(interfaceClass))
				return true;
			Class<?>[] interfaces = tempClass.getInterfaces();
			for (Class<?> inter : interfaces)
				if (interfaceClass.equals(inter))
					return true;
			tempClass = tempClass.getSuperclass();
		}
		return false;
	}

	public static boolean extendOf(Class<?> orginClass, Class<?> parentClass) {
		return orginClass.getSuperclass().equals(parentClass);
	}

	public static boolean extendsOf(Class<?> orginClass, Class<?> parentClass) {
		Class<?> tempClass = orginClass;
		while (!tempClass.equals(Object.class)) {
			if (tempClass.equals(parentClass))
				return true;
			tempClass = tempClass.getSuperclass();
		}
		return false;
	}

	public static Class<?> patchBaseType(Object patchType) {
		// 无类型
		if (patchType.getClass().equals(Void.class))
			return void.class;
		// 整形
		if (patchType.getClass().equals(Integer.class))
			return int.class;
		if (patchType.getClass().equals(Short.class))
			return short.class;
		if (patchType.getClass().equals(Long.class))
			return long.class;
		// 浮点
		if (patchType.getClass().equals(Double.class))
			return double.class;
		if (patchType.getClass().equals(Float.class))
			return float.class;
		// 字节
		if (patchType.getClass().equals(Byte.class))
			return byte.class;
		if (patchType.getClass().equals(Character.class))
			return char.class;
		// 布尔
		if (patchType.getClass().equals(Boolean.class))
			return boolean.class;
		return patchType.getClass();
	}

	public static Object castType(Object orgin, Class<?> targetType) {
		if (orgin == null)
			return null;
		// 整形
		if (targetType.equals(int.class))
			return (int)(Integer.parseInt((orgin.toString()).equals("") ? "0" : orgin.toString()));
		if (targetType.equals(short.class))
			return Short.parseShort((String) orgin);
		if (targetType.equals(long.class))
			return Long.parseLong(orgin.toString());
		if (targetType.equals(byte.class))
			return Byte.parseByte(orgin.toString());
		// 浮点
		if (targetType.equals(float.class))
			return Float.parseFloat(orgin.toString());
		if (targetType.equals(double.class))
			return Double.parseDouble(orgin.toString());
		// 日期
		if (targetType.equals(Date.class))
			try {
				return SimpleDateFormat.getInstance().parse((String) orgin);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		// 布尔型
		if (targetType.equals(boolean.class))
			return Boolean.parseBoolean((String) orgin);
		// char
		if (targetType.equals(char.class))
			return (char) orgin;
		if (targetType.equals(String.class))
			return orgin.toString();
		// 没有匹配到返回源数据
		return orgin;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseBaseTypeArray(Class<?> clzz, String[] arg, String format) throws ParseException {
		if (!clzz.isArray())
			return parseBaseType(clzz, arg[0], format);
		if (clzz.equals(String[].class))
			return arg;
		Object[] args = new Object[arg.length];
		for (int i = 0; i < args.length; i++)
			args[i] = parseBaseType(clzz, arg[i], format);
		return args;
	}
	/**
	 * 判断类是否为可支持的基本类型
	 * 
	 * @param clzz
	 * @return
	 */
	public static boolean isBaseType(Class<?> clzz) {
		if (clzz.equals(String.class))
			return true;
		if (clzz.equals(boolean.class))
			return true;
		if (clzz.equals(int.class))
			return true;
		if (clzz.equals(float.class))
			return true;
		if (clzz.equals(byte.class))
			return true;
		if (clzz.equals(short.class))
			return true;
		if (clzz.equals(long.class))
			return true;
		if (clzz.equals(double.class))
			return true;
		if (clzz.equals(char.class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Byte.class))
			return true;
		if (clzz.equals(Short.class))
			return true;
		if (clzz.equals(Integer.class))
			return true;
		if (clzz.equals(Long.class))
			return true;
		if (clzz.equals(Float.class))
			return true;
		if (clzz.equals(Double.class))
			return true;
		if (clzz.equals(Boolean.class))
			return true;
		if (clzz.equals(Character.class))
			return true;
		// 日期
		if (clzz.equals(Date.class))
			return true;

		// 以上所有类型的数组类型
		if (clzz.equals(String[].class))
			return true;
		if (clzz.equals(boolean[].class))
			return true;
		if (clzz.equals(int[].class))
			return true;
		if (clzz.equals(float[].class))
			return true;
		if (clzz.equals(byte[].class))
			return true;
		if (clzz.equals(short[].class))
			return true;
		if (clzz.equals(long[].class))
			return true;
		if (clzz.equals(double[].class))
			return true;
		if (clzz.equals(char[].class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Short[].class))
			return true;
		if (clzz.equals(Integer[].class))
			return true;
		if (clzz.equals(Long[].class))
			return true;
		if (clzz.equals(Float[].class))
			return true;
		if (clzz.equals(Double[].class))
			return true;
		if (clzz.equals(Boolean[].class))
			return true;
		if (clzz.equals(Character[].class))
			return true;
		// 日期
		if (clzz.equals(Date[].class))
			return true;

		return false;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseBaseType(Class<?> clzz, String arg, String format) {
		// 匹配时应该考虑优先级 比如常用的String int boolean应该放在前面 其实 包装类型应该分开
		if (clzz.equals(String.class))
			return arg;
		// 8个基本数据类型及其包装类型
		if (clzz.equals(int.class))
			return arg == null ? 0 : Integer.parseInt(arg);
		if (clzz.equals(Integer.class))
			return arg == null ? null : Integer.valueOf(arg);

		if (clzz.equals(boolean.class))
			return arg == null ? false : Boolean.parseBoolean(arg);
		if (clzz.equals(Boolean.class))
			return arg == null ? null : Boolean.valueOf(arg);

		if (clzz.equals(float.class))
			return arg == null ? 0.0f : Float.parseFloat(arg);
		if (clzz.equals(Float.class))
			return arg == null ? null : Float.valueOf(arg);

		if (clzz.equals(short.class))
			return arg == null ? 0 : Short.parseShort(arg);
		if (clzz.equals(Short.class))
			return arg == null ? null : Short.valueOf(arg);

		if (clzz.equals(long.class))
			return arg == null ? 0l : Long.parseLong(arg);
		if (clzz.equals(Long.class))
			return arg == null ? null : Long.valueOf(arg);

		if (clzz.equals(double.class))
			return arg == null ? 0.0f : Double.parseDouble(arg);
		if (clzz.equals(Double.class))
			return arg == null ? null : Double.valueOf(arg);

		if (clzz.equals(char.class))
			return arg == null ? null : arg.charAt(0);
		if (clzz.equals(Character.class))
			return arg == null ? null : Character.valueOf(arg.charAt(0));

		if (clzz.equals(char[].class))
			return arg == null ? null : arg.toCharArray();

		if (clzz.equals(byte.class) || clzz.equals(Byte.class))
			return arg == null ? null : Byte.parseByte(arg);
		// 日期
		if (clzz.equals(Date.class))
			try {
				return format == null ? DATE_FORMAT.parse(arg) : new SimpleDateFormat(format).parse(arg);
			} catch (ParseException e) {
				new RuntimeException(e);
			}
		return arg;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object proxyInstance) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object result = field.get(proxyInstance);
		field.setAccessible(false);
		return (T) result;
	}
	/**
	 * 传入参数类型和目标参数类型匹配
	 * @param matchType
	 * @param parameterTypes
	 * @return
	 */
	public static  boolean matchType(Class<?>[] matchType, Class<?>[] parameterTypes) {
		if(parameterTypes.length!=matchType.length)
			return false;
		for(int i = 0;i<parameterTypes.length;i++){
			if(parameterTypes[i]==null&&!isNotNullType(matchType[i]))
				continue;
			if(parameterTypes[i].equals(matchType[i]))
				continue;
			if(ClassLoader.extendsOf(parameterTypes[i], matchType[i]))
				continue;
			if(ClassLoader.implementsOf(parameterTypes[i], matchType[i]))
				continue;
			return false;
		}
		return true;
	}
	/**
	 * 类型是否非空类型
	 * @param type
	 * @return
	 */
	public static boolean isNotNullType(Class<?> type) {
		return type.equals(int.class)||
			   type.equals(long.class)||
			   type.equals(float.class)||
			   type.equals(double.class)||
			   type.equals(short.class)||
			   type.equals(boolean.class)?true:false;
	}

	public Class<?> loadClass(String clzzName, byte[] bytes) {
		this.loadClass = defineClass(clzzName, bytes, 0,bytes.length);
		return this.loadClass;
	}

	/**
	 * 获取field为List的泛型
	 * @param field
	 * @return
	 */
	public static Class<?> getListGenericType(Field field) {
		Type genericType = field.getGenericType(); 
		if(genericType != null && genericType instanceof ParameterizedType){   
			ParameterizedType pt = (ParameterizedType) genericType;
			//得到泛型里的class类型对象  
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
			return genericClazz;
		}   
		return null;
	}
	/**
	 * 获取Parameter为List的泛型
	 * @param field
	 * @return
	 */
	public static Class<?> getListGenericType(Parameter parm) {
		Type genericType = parm.getParameterizedType(); 
		if(genericType != null && genericType instanceof ParameterizedType){   
			ParameterizedType pt = (ParameterizedType) genericType;
			//得到泛型里的class类型对象  
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
			return genericClazz;
		}   
		return null;
	}
	/**
	 * 获取数组的类型
	 * @param arrayClass
	 * @return
	 */
	public static Class<?> getArrayType(Class<?> arrayClass){
		if(arrayClass.isArray()){
			return arrayClass.getComponentType();
		}
		return null;
	}
}