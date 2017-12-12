package com.YaNan.frame.RPC.exception.customer;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;
import com.YaNan.frame.RPC.exception.RPCException;

public class ServiceNotRegistException extends RPCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceNotRegistException() {
		super("RPC service not success to regist",RPCExceptionType.SERVICE_NOT_INIT,null);
	}

}
