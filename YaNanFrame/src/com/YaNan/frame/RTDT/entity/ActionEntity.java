
package com.YaNan.frame.RTDT.entity;

import java.lang.reflect.Method;

public class ActionEntity {
	private String name;
	private Method method;
	private Class<?> CLASS;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Class<?> getCLASS() {
		return CLASS;
	}
	public void setCLASS(Class<?> cLASS) {
		CLASS = cLASS;
	}
}
