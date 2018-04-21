package com.YaNan.frame.RTDT.entity;

import com.YaNan.frame.servlets.session.Token;

public class NotifyEntity {
	private String name;
	private Class<?> CLASS;
	private int mark;
	private String value;
	private Token token;
	private RequestAction requestAction;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<?> getCLASS() {
		return CLASS;
	}
	public void setCLASS(Class<?> cLASS) {
		CLASS = cLASS;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public RequestAction getRequetAction() {
		return requestAction;
	}
	public void setRequestAction(RequestAction requestAction) {
		this.requestAction = requestAction;
	}
}
