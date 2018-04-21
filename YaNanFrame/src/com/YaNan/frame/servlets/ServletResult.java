package com.YaNan.frame.servlets;

import com.YaNan.frame.servlets.annotations.RESPONSE_METHOD;

public class ServletResult {
	private String name;
	private int method=RESPONSE_METHOD.FORWARD;
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMethod() {
		return method;
	}
	public void setMethod(int method) {
		this.method = method;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
