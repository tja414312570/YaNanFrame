package com.YaNan.frame.plugin.handler;

import java.lang.reflect.Constructor;

import com.YaNan.frame.plugin.RegisterDescription;

public interface InstanceHandler {
	/**
	 * 将代理对象实例化之前
	 * @param registerDescription
	 * @param plugClass
	 * @param args
	 */
	void before(RegisterDescription registerDescription, Class<?> plugClass,Constructor<?> constructor, Object... args);
	/**
	 * 将代理对象实例化之后
	 * @param registerDescription
	 * @param plugClass
	 * @param proxy
	 * @param args
	 */
	void after(RegisterDescription registerDescription, Class<?> plugClass,Constructor<?> constructor, Object proxyObject,Object... args);
	/**
	 * 对象实例化时异常
	 * @param registerDescription
	 * @param plug
	 * @param proxy
	 * @param t
	 * @param args
	 */
	void exception(RegisterDescription registerDescription, Class<?> plug,Constructor<?> constructor, Object proxyObject, Throwable throwable, Object... args);

}
