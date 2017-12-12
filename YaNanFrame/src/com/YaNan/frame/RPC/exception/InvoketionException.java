package com.YaNan.frame.RPC.exception;

import com.YaNan.frame.RPC.Implements.RPCExceptionType;

public class InvoketionException extends RPCException {
	private static final long serialVersionUID = 1L;
	private String exception;
	/**
	 * Invoke Exception
	 * @param serviceName
	 * @param exception
	 * @param RUID
	 * @param SUID
	 */
	public InvoketionException(String serviceName,String exception,String RUID,String SUID) {
		super("RPC Invoketion exception at service name:"+serviceName+",exception info:"+exception,RPCExceptionType.INVOKETION_EXCEPTION,RUID);
		this.setServiceName(serviceName);
		this.setSUID(SUID);
		this.exception=exception;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	@Override
	public String toString() {
		return "RPC Invoketion exception at service name:"+getServiceName()+",exception info:"+exception;
	}
}
