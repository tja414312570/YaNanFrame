package com.YaNan.frame.plugin;

public class PluginRuntimeException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8769553319796579864L;
	private boolean interrupt;
	public PluginRuntimeException(Throwable t) {
		super(t);
	}
	public boolean isInterrupt() {
		return interrupt;
	}
	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}
	public void interrupt() {
		this.interrupt = true;
	}
	
}
