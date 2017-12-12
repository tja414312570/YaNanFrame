package com.YaNan.frame.RPC.utils;

import java.io.Serializable;

public class RPCServiceInfo implements Serializable{

	/**
	 * RPC�������Ϣ
	 */
	private static final long serialVersionUID = 1L;
	private String serviceName;
	private Object key;
	public String getServiceName() {
		return serviceName;
	}
	public RPCServiceInfo(String serviceName, Object object) {
		super();
		this.serviceName = serviceName;
		this.key = object;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
