package com.YaNan.frame.reflect;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class UrlClassLoader {
	private Object loadObject;
	private Class<?> loadClass;
	private URL[] url = null;

	/*
	 * static method
	 */
	public static String createFieldGetMethod(String name) {
		String nameMethod = ("get" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
	}

	public static String createFieldSetMethod(String name) {
		String nameMethod = ("set" + name.substring(0, 1).toUpperCase() + name
				.substring(1, name.length()));
		return nameMethod;
	}

	public void stop() {

	}

	/*
	 * constructs start
	 */
	// public UrlClassLoader(String path){
	// this.loadObject=object;
	// this.loadClass=this.loadObject.getClass();
	// }
	@SuppressWarnings("resource")
	public UrlClassLoader(String path, String fullClassName)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, MalformedURLException {

		this.url = new URL[1];
		url[0] = new URL("file:"
				+ new File(path).getAbsolutePath().toString().replace(".", "")
				+ "/");

		URLClassLoader classLoader = new URLClassLoader(url);
		this.loadObject = classLoader.loadClass(fullClassName).newInstance();
		this.loadClass = classLoader.loadClass(fullClassName);

	}

	@SuppressWarnings("resource")
	public UrlClassLoader(String path, String packageName, String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, MalformedURLException {
		this.url = new URL[1];
		url[0] = new URL("file:"
				+ new File(path).getAbsolutePath().toString().replace(".", "")
				+ "/");
		URLClassLoader classLoader = new URLClassLoader(url);
		this.loadObject = classLoader.loadClass(packageName + "." + className)
				.newInstance();
		this.loadClass = classLoader.loadClass(packageName + "." + className);

	}

	public UrlClassLoader(String... path) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			MalformedURLException {

		this.url = new URL[path.length];
		for (int i = 0; i < path.length; i++) {
			url[i] = new URL("file:"
					+ new File(path[i]).getAbsolutePath().toString()
							.replace(".", "") + "/");
		}

	}

	@SuppressWarnings("resource")
	public boolean loadClass(String className) {
		URLClassLoader classLoader = new URLClassLoader(url);
		try {
			this.loadObject = classLoader.loadClass(className).newInstance();
			this.loadClass = classLoader.loadClass(className);
			return true;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("resource")
	public boolean loadClass(String packageName, String className) {
		URLClassLoader classLoader = new URLClassLoader(url);
		try {
			this.loadObject = classLoader.loadClass(
					packageName + "." + className).newInstance();
			this.loadClass = classLoader.loadClass(packageName + "."
					+ className);
			return true;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * get loaded object or class
	 */
	public Class<?> getLoadedClass() {
		return this.loadClass;
	}

	public Object getLoadedObject() {
		return this.loadObject;
	}

	/*
	 * Field
	 */
	public Field[] getFields() {
		return this.loadClass.getFields();
	}

	public Field[] getDeclaredFields() {
		return this.loadClass.getDeclaredFields();
	}

	public boolean hasField(String field) {
		try {
			this.loadClass.getField(field);
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}

	public Object getFieldValue(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getField(field);
		f.setAccessible(true);
		Object result = f.get(this.loadObject);
		return result;
	}

	public void setFieldValue(String field, Object value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field f = this.loadClass.getField(field);
		f.setAccessible(true);
		f.set(this.loadObject, value);
	}

	/*
	 * field set or get,only used by servletDispatcher
	 */
	public void set(String method, String arg) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		invokeMethod(createFieldSetMethod(method), arg);
	}

	public Object get(String method) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return invokeMethod(createFieldGetMethod(method));
	}

	/*
	 * invoke method
	 */
	public Object invokeMethod(String methodName, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Class<?>[] argsClass = new Class[args.length];
		Object result = null;
		Method method;
		if (args.length == 0) {
			method = this.loadClass.getMethod(methodName);
		} else {
			for (int i = 0; i < args.length; i++) {
				argsClass[i] = args[i].getClass();
			}
			method = this.loadClass.getMethod(methodName, argsClass);
		}
		result = method.invoke(this.loadObject, args);
		return result;
	}

	public Object invokeMain() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = this.loadClass.getMethod("main", String[].class);
		Object result = method.invoke(this.loadObject, (Object) null);
		return result;
	}

	/*
	 * Method
	 */
	// get method
	public Method getDeclaredMethod(String method, Class<?>... parameterType)
			throws NoSuchMethodException, SecurityException {
		return this.loadClass.getDeclaredMethod(method, parameterType);
	}

	public Method getMethod(String method, Class<?>... parameterType)
			throws NoSuchMethodException, SecurityException {
		return this.loadClass.getMethod(method, parameterType);
	}

	public Method[] getMethods() throws NoSuchMethodException,
			SecurityException {
		return this.loadClass.getMethods();
	}

	public Method[] getDeclaredMethods() throws NoSuchMethodException,
			SecurityException {
		return this.loadClass.getDeclaredMethods();
	}

	// judge method
	public boolean hasDeclaredMethod(String method, Class<?>... parameterType) {
		try {
			this.loadClass.getDeclaredMethod(method, parameterType);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean hasMethod(String method, Class<?>... parameterType) {
		try {
			this.loadClass.getMethod(method, parameterType);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

}