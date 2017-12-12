package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class ServiceClosed extends RPCException {
	private String serviceName;
	private String SUID;
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
	public ServiceClosed(String serviceName,String SUID,String RUID) {
		super("RPC service is closed,service name:"+serviceName+",service node id:"+SUID,RPCExceptionType.SERVICE_CLOSED,RUID);
		this.serviceName = serviceName;
		this.SUID = SUID;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String toString() {
		return "RPC service is closed,service name:"+serviceName+",service node id:"+SUID;
	}

}
