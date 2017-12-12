package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class ServiceInitException extends RPCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceInitException(String exception) {
		super(exception,RPCExceptionType.SERVICE_INIT_ERROR,null);
	}

}
