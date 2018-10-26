package com.YaNan.frame.servlets.exception;
/**
 * Servlet运行时异常
 * @author yanan
 *
 */
public class ServletRuntimeException extends RuntimeException{
	private int status;
	private static final long serialVersionUID = -4263723785558342363L;
	public ServletRuntimeException() {
		super();
		this.status = 500;
	}
	public ServletRuntimeException(String msg){
		super(msg);
		this.status = 500;
	}
	public ServletRuntimeException(String msg,Throwable cause){
		super(msg,cause);
		if(cause.getClass().equals(ServletRuntimeException.class))
			this.status = ((ServletRuntimeException)cause).getStatus();
		else
			this.status = 500;
	}
	public ServletRuntimeException(int code,String msg){
		super(msg);
		this.status = code;
	}
	public ServletRuntimeException(int code) {
		super();
		this.status = code;
	}
	public ServletRuntimeException(int code,String msg,Throwable cause){
		super(msg,cause);
		this.status = code;
	}
	/**
	 * Servlet status code
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
