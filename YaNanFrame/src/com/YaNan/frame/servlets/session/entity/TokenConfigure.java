package com.YaNan.frame.servlets.session.entity;

public class TokenConfigure {
	private String TokenMark;
	private String RoleMark;
	private String HibernateInterface;
	private int Timeout;
	public String getTokenMark() {
		return TokenMark;
	}
	public void setTokenMark(String tokenMark) {
		TokenMark = tokenMark;
	}
	public String getRoleMark() {
		return RoleMark;
	}
	public void setRoleMark(String roleMark) {
		RoleMark = roleMark;
	}
	public String getHibernateInterface() {
		return HibernateInterface;
	}
	public void setHibernateInterface(String hibernateInterface) {
		HibernateInterface = hibernateInterface;
	}
	public int getTimeout() {
		return Timeout;
	}
	public void setTimeout(int timeout) {
		Timeout = timeout;
	}
	
	
}
