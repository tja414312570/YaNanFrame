package com.YaNan.frame.RTDT.entity;

import com.YaNan.frame.RTDT.WebSocketListener;
import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;
import com.YaNan.frame.servlets.session.Token;

public class Notification {
	private WebSocketListener client;
	private NotifyEntity action;
	private String name;
	private boolean bind = true;
	private String reason;
	private int mark;
	private String value;
	private RTDTNotification notifyImpl;
	private Token token;
	private RequestAction requestAction;
	private boolean isNotify;

	public Notification(WebSocketListener client, NotifyEntity action) {
		this.client = client;
		this.action = action;
		setName(action.getName());
		this.setMark(action.getMark());
		setNotifyImpl(notifyImpl);
		setToken(action.getToken());
	}

	public Notification(WebSocketListener webSocketListener, RequestAction request, NotifyEntity notifyEntity,
			RTDTNotification notifyImp) {
		this.client = webSocketListener;
		this.action = notifyEntity;
		this.notifyImpl = notifyImp;
		setName(action.getName());
		this.setMark(action.getMark());
		setNotifyImpl(notifyImpl);
		setToken(action.getToken());
		this.setRequestAction(request);
	}

	public void Notify(String message) {
		if(!this.isNotify)
			this.bindNotify();
		if (this.bind) {
			this.client.write(buildResponse(message));
		}
	}

	public ResponseAction buildResponse(String message) {
		ResponseAction response = new ResponseAction();
		response.setType(4281);
		response.setAUID(this.requestAction.getAUID());
		response.setStatus(4280);
		response.setData(message);
		return response;
	}

	public boolean isBind() {
		return this.bind;
	}
	private void bindNotify(){
		if(!isNotify){
			isNotify=true;
			ResponseAction response = new ResponseAction();
			response.setType(4281);
			response.setAUID(this.requestAction.getAUID());
			if (this.bind) {
				response.setStatus(4270);
				response.setData("Notify bind success!");
			} else {
				response.setStatus(4271);
				response.setData(this.reason);
			}
			this.client.write(response);
		}
		
	}
	public void reject(String reason) {
		this.bind = false;
		this.reason = reason;
		this.bindNotify();
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Token getToken() {
		return this.token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public RTDTNotification getNotifyImpl() {
		return this.notifyImpl;
	}

	public void setNotifyImpl(RTDTNotification notifyImpl) {
		this.notifyImpl = notifyImpl;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public void setClient(WebSocketListener client) {
		this.client = client;
	}

	public NotifyEntity getAction() {
		return action;
	}

	public void setAction(NotifyEntity action) {
		this.action = action;
	}

	public void destory() {
		this.notifyImpl.unBind(this);
	}

	public RequestAction getRequestAction() {
		return requestAction;
	}

	public void setRequestAction(RequestAction requestAction) {
		this.requestAction = requestAction;
	}
}
