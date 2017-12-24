package com.YaNan.frame.core.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;

import com.YaNan.frame.core.aspect.AspectContainer;
import com.YaNan.frame.core.aspect.JoinPoint;
import com.YaNan.frame.core.aspect.interfaces.AspectInterface;


/**
 * ClassLoader for YaNan.frame 该类加载器是YaNan应用的核心处理机制之一，用于对应用内部的控制
 * 
 * @author Administrator
 * @version 1.0.1
 * @author YaNan
 *
 */
public class ClassLoader {
	private Object loadObject;
	private Class<?> loadClass;
	private List<AspectInterface> aspectInterfaces;
	private JoinPoint joinPoint;
	private boolean aspect=true;
	private final static AspectContainer aspectContainer = AspectContainer.getAspectContainer();
	static{
		aspectContainer.init();
	}
	/*
	 * static method
	 */
	/**
	 * 判断类是否存在，参数 完整类名
	 * 
	 * @param className
	 * @return
	 */
	public static boolean exists(String className) {
		try {
			Class.forName(className);
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
		Class<?> objClass;
		Field[] fields = null;
		try {
			objClass = Class.forName(className);
			fields = objClass.getFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fields;
	}

	/**
	 * 获得类下所有属性 参数 完整类名
	 * 
	 * @param className
	 * @return
	 */
	public static Field[] getAllFields(String className) {
		Class<?> objClass;
		Field[] fields = null;
		try {
			objClass = Class.forName(className);
			fields = objClass.getDeclaredFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fields;
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
	public static boolean hasMethod(String ClassName, String Method,
			Class<?>... args) {

		try {
			Class<?> cls = Class.forName(ClassName);
			cls.getMethod(Method, args);
			return true;
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			// e.printStackTrace();
			return false;
		}
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
	public static boolean hasDeclaredMethod(String ClassName, String Method,
			Class<?>... args) throws ClassNotFoundException {
		Class<?> cls = Class.forName(ClassName);
		try {
			cls.getDeclaredMethod(Method, args);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			// e.printStackTrace();
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
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argsClass[i] = args[i].getClass();
		}
		return argsClass;
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
	public static String createFieldGetMethod(String name) {
		String nameMethod = ("get" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
	}

	/**
	 * 创建属性的set方法
	 * 
	 * @param name
	 * @return
	 */
	public static String createFieldSetMethod(String name) {
		String nameMethod = ("set" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
	}

	public static String createFieldSetMethod(Field field) {
		String name = field.getName();
		String nameMethod = ("set" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
	}

	/**
	 * 创建属性的add方法
	 * 
	 * @param name
	 * @return
	 */
	public static String createFieldAddMethod(String name) {
		String nameMethod = ("add" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
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
	public static Method getMethod(Class<?> cls, String methodName,
			Class<?>... args) throws NoSuchMethodException, SecurityException {
		Method method;
		if (args.length == 0) {
			method = cls.getMethod(methodName);
		} else {
			method = cls.getMethod(methodName, args);
		}
		return method;
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
	public static Method getDeclaredMethod(Class<?> cls, String methodName,
			Class<?>... args) throws NoSuchMethodException, SecurityException {
		Method method;
		if (args.length == 0) {
			method = cls.getDeclaredMethod(methodName);
		} else {
			method = cls.getDeclaredMethod(methodName, args);
		}
		return method;
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
	public ClassLoader(Class<?> cls, boolean instance)
			{
		this.loadClass = cls;
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
	public ClassLoader(String className) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		this.loadClass = Class.forName(className);
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
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
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
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
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
	public Object Instance(String Method, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
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
	public Object Instance(Object... args) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException {
		Constructor<?> cls = this.loadClass
				.getConstructor(getParameterTypes(args));
		this.loadObject = cls.newInstance(args);
		return this.loadObject;
	}

	/*
	 * set get loaded object or class
	 */
	/**
	 * 加载类到此加载器
	 * 
	 * @param className
	 * @throws ClassNotFoundException
	 */
	public void loadClass(String className) throws ClassNotFoundException {
		this.loadClass = Class.forName(className);
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
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
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
		return this.loadClass.getFields();
	}
	

	/**
	 * 获得加载器内加载类的所有属性
	 * 
	 * @return
	 */
	public Field[] getDeclaredFields() {
		return this.loadClass.getDeclaredFields();
	}

	/**
	 * 判断加载器内加载的类中是否有某个公开的属性,传入属性名
	 * 
	 * @param field
	 * @return
	 */
	public boolean hasField(String field) {
		try {
			this.loadClass.getField(field);
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}

	/**
	 * 判断加载器内加载的类中是否有某个属性，传入属性名
	 * 
	 * @param field
	 * @return
	 */
	public boolean hasDeclaredField(String field) {
		try {
			this.loadClass.getDeclaredField(field);
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
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
	public Object getFieldValue(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getField(field);
		return getFieldValue(f);
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
	public Object getFieldValue(Field field) throws IllegalArgumentException,
			IllegalAccessException {
		field.setAccessible(true);
		Object result = field.get(this.loadObject);
		return result;
	}

	public Object getDeclaredFieldValue(String field)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getDeclaredField(field);
		return getFieldValue(f);
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
	public void setFieldValue(String field, Object value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getDeclaredField(field);
		setFieldValue(f, value);
	}

	public void setDeclaredFieldValue(String field, Object value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getDeclaredField(field);
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
	public void setFieldValue(Field field, Object value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		field.set(this.loadObject, value);
	}

	/*
	 * field set or get,only used by servletDispatcher
	 */
	/**
	 * 该方法用于直接设置某个属性的值，传入String属性，String 值
	 * 
	 * @param method
	 * @param arg
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void set(String method, Object... arg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		invokeMethod(createFieldSetMethod(method), arg);
	}

	/**
	 * 该方法用于直接设置某个属性的值，传入String属性，String 值
	 * 
	 * @param method
	 * @param arg
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void set(String method, Class<?> parameterType, Object arg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] parameter = new Class<?>[1];
		parameter[0]=parameterType;
		invokeMethod(createFieldSetMethod(method),parameter, arg);
	}

	/**
	 * 该方法用于直接获取某个属性的值，传入String属性
	 * 
	 * @param method
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Object get(String method) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return invokeMethod(createFieldGetMethod(method));
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
	public Object invokeMethod(String methodName, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMethod(methodName, getParameterTypes(args),args);
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
	public Object invokeMethod(String methodName, Class<?>[] parameterType,
			Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMethod(this.loadObject, methodName,parameterType,args);
	}
	private Object invokeMethod(final Object object,final String methodName, Class<?>[] parameterType,
			Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if(aspect)
		new Thread(new Runnable() {
			@Override
			public void run() {
				aspectInterfaces = aspectContainer.match(object.getClass().getName()+"."+methodName);
				if(aspectInterfaces!=null){
					joinPoint = new JoinPoint();
					joinPoint.setAspectClass(loadClass);
					joinPoint.setAspectObject(loadObject);
					joinPoint.setAspectMethod(methodName);
					for(AspectInterface aspectInterface : aspectInterfaces)
						aspectInterface.before(joinPoint);
				}
			}
		}).start();
		Method method = this.loadClass.getMethod(methodName, parameterType);
		final Object result = method.invoke(object, args);
		if(aspect)
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(aspectInterfaces!=null){
					joinPoint.setAspectObject(loadObject);
					joinPoint.setAspectResult(result);
					for(AspectInterface aspectInterface : aspectInterfaces)
						aspectInterface.after(joinPoint);
				}
			}
		}).start();
		return result;
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
	public Object invokeStaticMethod(String methodName, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMethod(null, methodName,getParameterTypes(args),args);
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
		return this.loadClass.getDeclaredMethod(method, parameterType);
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
	public Method getMethod(String method, Class<?>... parameterType)
			throws NoSuchMethodException, SecurityException {
		return this.loadClass.getMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的所有公开的方法
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method[] getMethods() throws NoSuchMethodException,
			SecurityException {
		return this.loadClass.getMethods();
	}

	/**
	 * 获得加载器内加载类的所有方法
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method[] getDeclaredMethods() throws NoSuchMethodException,
			SecurityException {
		return this.loadClass.getDeclaredMethods();
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
		try {
			this.loadClass.getDeclaredMethod(method, parameterType);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断加载器内加载的类是否有某个公开的方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method
	 * @param parameterType
	 * @return
	 */
	public boolean hasMethod(String method, Class<?>... parameterType) {
		try {
			this.loadClass.getMethod(method, parameterType);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			// e.printStackTrace();
			return false;
		}
	}

	public Field getDeclaredField(String field) throws NoSuchFieldException,
			SecurityException {
		return loadClass.getDeclaredField(field);
	}

	public static boolean clone(Object target, Object source) {
		if (target.getClass() == source.getClass()) {
			DisClone(target,source);
		}
		return false;
	}
	public static void DisClone(Object target, Object source){
			Field[] fields = target.getClass().getDeclaredFields();
			Class<?> sCls = source.getClass();
			try {
				for (Field f : fields) {
                    Field sField=null;
                    try {
                         sField= sCls.getDeclaredField(f.getName());
                    } catch (NoSuchFieldException e1) {
                        continue;
                    }
                    sField.setAccessible(true);
                    f.setAccessible(true);
					f.set(target, sField.get(source));
					}
			} catch (IllegalArgumentException |IllegalAccessException e) {
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
				}
			}
			return object;
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static boolean implementOf(Class<?> orginClass,Class<?> interfaceClass){
		Class<?>[] cls = orginClass.getInterfaces();
		for(Class<?> cCls : cls)
			if(cCls.equals(interfaceClass))
				return true;
		return false;
	}
	public static boolean extendsOf(Class<?> orginClass,Class<?> parentClass){
		return orginClass.getSuperclass().equals(parentClass);
	}

	public static Class<?> patchBaseType(Object patchType){
		//无类型
		if(patchType.getClass().equals(Void.class))
			return void.class;
		//整形
		if(patchType.getClass().equals(Integer.class))
			return int.class;
		if(patchType.getClass().equals(Short.class))
			return short.class;
		if(patchType.getClass().equals(Long.class))
			return long.class;
		//浮点
		if(patchType.getClass().equals(Double.class))
			return double.class;
		if(patchType.getClass().equals(Float.class))
			return float.class;
		//字节
		if(patchType.getClass().equals(Byte.class))
			return byte.class;
		if(patchType.getClass().equals(Character.class))
			return char.class;
		//布尔
		if(patchType.getClass().equals(Boolean.class))
			return boolean.class;
		return patchType.getClass();
	}

	public boolean isAspect() {
		return aspect;
	}

	public void setAspect(boolean aspect) {
		this.aspect = aspect;
	}
	public static Object castType(Object orgin, Class<?> targetType) {
		if(orgin==null)return null;
		// 整形
		if (targetType.equals(int.class))
			return Integer.parseInt((orgin.toString()).equals("")?"0":orgin.toString());
		if (targetType.equals(short.class))
			return Short.parseShort((String) orgin);
		if (targetType.equals(long.class))
			return Long.parseLong((String) orgin);
		if (targetType.equals(byte.class))
			return Byte.parseByte((String) orgin);
		// 浮点
		if (targetType.equals(float.class))
			return Float.parseFloat(orgin.toString());
		if (targetType.equals(double.class))
			return Double.parseDouble((String) orgin);
		// 日期
		if (targetType.equals(Date.class))
			return Date.valueOf((String) orgin);
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
}