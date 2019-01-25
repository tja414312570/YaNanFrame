package com.YaNan.frame.hibernate.database.exception;

public class HibernateInitException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1729117431961497757L;
	
	public HibernateInitException(String msg){
		super(msg);
	}
	public HibernateInitException(String msg,Throwable t){
		super(msg,t);
	}
	public HibernateInitException(Throwable t){
		super(t);
	}
}
