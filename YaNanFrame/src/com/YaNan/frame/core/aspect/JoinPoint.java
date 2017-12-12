package com.YaNan.frame.core.aspect;

public class JoinPoint {
	private Object aspectObject;
	private Class<?> aspectClass;
	private String aspectMethod;
	private Object dispatcher;
	private Object aspectResult;
	public Object getDispatcher() {
		return dispatcher;
	}
	public void setDispatcher(Object dispatcher) {
		this.dispatcher = dispatcher;
	}
	public Object getAspectObject() {
		return aspectObject;
	}
	public void setAspectObject(Object aspectObject) {
		this.aspectObject = aspectObject;
	}
	
	public void setAspectClass(Class<?> loadClass) {
		this.aspectClass = loadClass;
	}
	public String getAspectMethod() {
		return aspectMethod;
	}
	public void setAspectMethod(String aspectMethod) {
		this.aspectMethod = aspectMethod;
	}
	public Class<?> getAspectClass() {
		return aspectClass;
	}
	public Object getAspectResult() {
		return aspectResult;
	}
	public void setAspectResult(Object aspectResult) {
		this.aspectResult = aspectResult;
	} 
}
