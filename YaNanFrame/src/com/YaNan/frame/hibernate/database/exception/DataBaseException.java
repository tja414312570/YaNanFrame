package com.YaNan.frame.hibernate.database.exception;

public class DataBaseException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	public DataBaseException(int code){
		this.setCode(code);
	}
	public DataBaseException(int code,String message,Throwable t){
		super(message,t);
		this.code = code;
	}
	public DataBaseException(String message,Throwable t){
		super(message,t);
	}
	public DataBaseException(int code,String message){
		super(message);
		this.code = code;
	}
	public DataBaseException(String message){
		super(message);
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
