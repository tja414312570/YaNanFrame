package com.YaNan.frame.RPC.exception.customer;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;
import com.YaNan.frame.RPC.exception.RPCException;

public class ServiceNotRunningException extends RPCException {

	private static final long serialVersionUID = 1L;

	public ServiceNotRunningException() {
		super("RPC service not running",RPCExceptionType.SERVICE_NOT_RUNTING,null);
	}

}
