package com.YaNan.frame.core;

public class SystemException extends Exception {
	private String string;

	public SystemException(String string) {
		this.string = string;
	}

	public String getReason() {
		return string;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
