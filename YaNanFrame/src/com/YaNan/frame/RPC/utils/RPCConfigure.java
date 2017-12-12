package com.YaNan.frame.RPC.utils;

public class RPCConfigure {
	private String name;
	private String host;
	private int port;
	private int timeout;
	private Object key;
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	@Override
	public String toString() {
		return "RPCConfigure [name=" + name + ", host=" + host + ", port=" + port + ", timeout=" + timeout+ ", key=" + key  + "]";
	}
	
	
}
