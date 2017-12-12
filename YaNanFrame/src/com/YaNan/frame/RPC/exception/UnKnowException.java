package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class UnKnowException extends RPCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceName;
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getSUID() {
		return SUID;
	}

	public void setSUID(String sUID) {
		SUID = sUID;
	}

	private String SUID;

	public UnKnowException(String serviceName,String SUID,String RUID) {
		super("Unknow exception,service name:"+serviceName+",service node id:"+SUID,RPCExceptionType.INNER_ERROR,RUID);
		this.serviceName = serviceName;
		this.SUID = SUID;
	}
}
