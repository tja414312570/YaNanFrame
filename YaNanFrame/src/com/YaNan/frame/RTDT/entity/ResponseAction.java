package com.YaNan.frame.RTDT.entity;

import com.YaNan.frame.RTDT.requestListener;

public class ResponseAction {
	private String AUID;
	private int status;
	private String data;
	private int type;
	private transient requestListener client;

	public String getAUID() {
		return this.AUID;
	}

	public void setAUID(String aUID) {
		this.AUID = aUID;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public requestListener getClient() {
		return client;
	}

	public void setClient(requestListener client) {
		this.client = client;
	}

	public void write() {
		this.client.write(this);
	}
	public void write(String content) {
		this.status=4280;
		this.data = content;
		this.write();
	}
}
