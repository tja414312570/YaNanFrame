package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.MessagePrototypeInterface;

public class RPCException extends Exception implements MessagePrototypeInterface{
	private static final long serialVersionUID = 1L;
	private int code=0;
	private String SUID;
	private String RUID;
	private String serviceName;
	public int getCode() {
		return code;
	}
	public RPCException(String exception,int exceptionCode,String RUID){
		super(exception);
		this.code=exceptionCode;
		this.RUID=RUID;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getSUID() {
		return SUID;
	}
	public void setSUID(String sUID) {
		SUID = sUID;
	}
	public String getRUID() {
		return RUID;
	}
	public void setRUID(String rUID) {
		RUID = rUID;
	}
	@Override
	public String getServiceName() {
		return this.serviceName;
	}
	@Override
	public void setServiceName(String serviceName) {
		this.serviceName=serviceName;
	}
}
