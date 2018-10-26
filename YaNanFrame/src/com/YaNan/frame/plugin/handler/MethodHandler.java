package com.YaNan.frame.plugin.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.handler.PlugsHandler.ProxyType;

import net.sf.cglib.proxy.MethodProxy;

/**
 * 方法处理器
 * v1.0 支持方法信息的传递
 * v1.1 20180910 新增每个MethodHandler进入InvokeHandler处理时、
 * 		对应的InvokeHandler的获取
 * @author yanan
 *
 */
public class MethodHandler {
	private PlugsHandler plugsProxy; 
	private Method method;
	private InvokeHandlerSet invokeHandlerSet;
	private MethodProxy methodProxy;
	private Object[] parameters;
	private boolean chain=true;
	private Object originResult;
	private Object headerResult;
	private Object footResult;
	private Map<String,Object> attribute = new HashMap<String,Object>();
	private Object interruptResult;

	public boolean isChain() {
		return chain;
	}
	public void setChain(boolean chain) {
		this.chain = chain;
	}
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

	public void interrupt(Object result){
		this.chain = false;
		this.setInterruptResult(result) ;
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

	void setHeaderResult(Object headerResult) {
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
	public InvokeHandlerSet getInvokeHandlerSet() {
		return invokeHandlerSet;
	}
	public void setInvokeHandlerSet(InvokeHandlerSet invokeHandlerSet) {
		this.invokeHandlerSet = invokeHandlerSet;
	}
	public Object getInterruptResult() {
		return interruptResult;
	}
	public void setInterruptResult(Object interruptResult) {
		this.interruptResult = interruptResult;
	}
}
