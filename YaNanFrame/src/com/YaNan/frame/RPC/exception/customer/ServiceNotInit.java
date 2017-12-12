package com.YaNan.frame.RPC.exception.customer;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;
import com.YaNan.frame.RPC.exception.RPCException;

public class ServiceNotInit extends RPCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceNotInit() {
		super("RPC service init exception",RPCExceptionType.SERVICE_NOT_INIT,null);
	}

}
