package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class ServiceIsRunning extends RPCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceIsRunning() {
		super("RPCService is running that can not to start", RPCExceptionType.SERVICE_RUNNING,null);
	}

}
