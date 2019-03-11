package com.YaNan.frame.RTDT.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.YaNan.frame.servlets.session.Token;

public class RequestAction {
	private String action;
	private int type;
	private String AUID;
	private Map<String, String> data = new HashMap<String, String>();
	private Token token;
	private ActionEntity actionEntity;
	private Notification notification;
	private String sessionId;

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAUID() {
		return this.AUID;
	}

	public void setAUID(String aUID) {
		this.AUID = aUID;
	}

	public Map<String, String> getParameterMap() {
		return data;
	}

	public void setParameterMap(Map<String, String> parameterMap) {
		data = parameterMap;
	}

	public Iterator<String> getParametersKeyIterator() {
		return data.keySet().iterator();
	}

	public Iterator<String> getParametersValueIterator() {
		return data.values().iterator();
	}

	public String getParameter(String parameterName) {
		return (String) data.get(parameterName);
	}

	public Token getToken() {
		return this.token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Notification getNotification() {
		return this.notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


	public ActionEntity getActionEntity() {
		return actionEntity;
	}

	public void setActionEntity(ActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}
}
