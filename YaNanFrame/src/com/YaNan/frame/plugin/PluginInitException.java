package com.YaNan.frame.plugin;

public class PluginInitException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8769553319796579864L;
	private boolean interrupt;
	public PluginInitException(Throwable t) {
		super(t);
	}
	public PluginInitException(String string) {
		super(string);
	}
	public PluginInitException(String string, Throwable e) {
		super(string,e);
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
