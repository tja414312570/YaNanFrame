package com.YaNan.frame.RPC.utils;

import java.io.Serializable;

public class RegistResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status;
	private String SUID;
	public String getSUID() {
		return SUID;
	}
	public void setSUID(String sUID) {
		SUID = sUID;
	}
	public RegistResult(int status) {
		this.status=status;
	}
	public RegistResult(int status, String SUID) {
		this.status=status;
		this.SUID = SUID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
