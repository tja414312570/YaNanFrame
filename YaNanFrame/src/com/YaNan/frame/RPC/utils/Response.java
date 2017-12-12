package com.YaNan.frame.RPC.utils;

import java.io.Serializable;

import com.YaNan.frame.RPC.Implements.MessagePrototypeInterface;

public class Response implements Serializable,MessagePrototypeInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String serviceName;
	private Object responseResult;
	private String RUID;
	private String SUID;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Object getResponseResult() {
		return responseResult;
	}
	public void setResponseResult(Object responseResult) {
		this.responseResult = responseResult;
	}
	public String getRUID() {
		return RUID;
	}
	public void setRUID(String rUID) {
		RUID = rUID;
	}
	
	@Override
	public String toString() {
		return "Response [serviceName=" + serviceName + ", responseResult="
				+ responseResult + ", RUID=" + RUID + ", SUID=" + SUID + "]";
	}
	@Override
	public String getSUID() {
		return this.SUID;
	}
	@Override
	public void setSUID(String sUID) {
		this.SUID = sUID;
	}
}
