package com.YaNan.frame.plugin.handler;

/**
 * 调用拦截器
 * @author yanan
 *
 */
public interface InvokeHandler {
	/**
	 * 在方法调用之前
	 * @param methodHandler
	 */
	void before(MethodHandler methodHandler);
	/**
	 * 在方法调用之后执行
	 * @param methodHandler
	 */
	void after(MethodHandler methodHandler);
	/**
	 * 在调用方法异常时执行
	 * @param methodHandler
	 * @param e
	 */
	void error(MethodHandler methodHandler, Throwable e);
}
