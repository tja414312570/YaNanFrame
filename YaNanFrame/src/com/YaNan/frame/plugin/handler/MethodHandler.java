package com.YaNan.frame.plugin.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.handler.PlugsHandler.ProxyType;

import net.sf.cglib.proxy.MethodProxy;

public class MethodHandler {
	private PlugsHandler plugsProxy; 
	private Method method;
	private MethodProxy methodProxy;
	private Object[] parameters;
	private boolean chain=false;
	private Object originResult;
	private Object headerResult;
	private Object footResult;
	private Map<String,Object> attribute = new HashMap<String,Object>();

	public MethodHandler(PlugsHandler plugsProxy, Method method, Object[] args) {
		this.plugsProxy = plugsProxy;
		this.method = method;
		this.parameters = args;
	}
	public MethodHandler(PlugsHandler plugsProxy, MethodProxy methodProxy, Object[] args) {
		this.plugsProxy = plugsProxy;
		this.methodProxy = methodProxy;
		this.parameters = args;
	}
	public PlugsHandler getPlugsProxy() {
		return plugsProxy;
	}

	public void setPlugsProxy(PlugsHandler plugsProxy) {
		this.plugsProxy = plugsProxy;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object getResult() {
		return null;
	}

	public boolean chain() {
		boolean tmp = chain;
		chain=!chain;
		return tmp;
	}

	public void setOriginResult(Object result) {
		this.originResult = result;
		
	}
	public Object getOriginResult() {
		return this.originResult;
	}

	public Object getHeaderResult() {
		return headerResult;
	}

	public void setHeaderResult(Object headerResult) {
		if(this.headerResult==null)
			this.headerResult = headerResult;
	}

	public Object getFootResult() {
		return footResult;
	}

	public void setFootResult(Object footResult) {
		this.footResult = footResult;
	}
	/**
	 * 重新执行目标方法，可以传入之前的参数或新的参数
	 * @param parmeters
	 * @throws Throwable 
	 */
	public void chain(Object... parmeters) throws Throwable {
		this.headerResult = this.plugsProxy.getProxyType().equals(ProxyType.JDK)
				?this.method.invoke(this.plugsProxy.getProxyObject(), parmeters)
						:methodProxy.invokeSuper(this.plugsProxy.getProxyObject(), parameters);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attribute.get(key);
	}

	public void addAttribute(String key,Object value) {
		this.attribute.put(key, value);
	}
}
