package com.YaNan.frame.hibernate.database.exception;

public class DataBaseException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	public DataBaseException(int code){
		this.setCode(code);
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
