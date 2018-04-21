package com.YaNan.frame.plugin.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodHandler {
	private PlugsHandler plugsProxy; 
	private Method method;
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

	public void chain(Object... parmeters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.headerResult = this.method.invoke(this.plugsProxy.getProxyObject(), parmeters);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attribute.get(key);
	}

	public void addAttribute(String key,Object value) {
		this.attribute.put(key, value);
	}
}
