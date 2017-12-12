package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class ServiceNoFound extends RPCException {
	private static final long serialVersionUID = 1L;
	private String serviceName;
	public ServiceNoFound(String serviceName,String RUID) {
		super("RPC service is not exists,service name:"+serviceName,RPCExceptionType.SERVICE_NO_FOUND,RUID);
		this.setServiceName(serviceName);
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	@Override
	public String toString() {
		return "RPC service is not exists,service name:"+serviceName;
	}
	}
