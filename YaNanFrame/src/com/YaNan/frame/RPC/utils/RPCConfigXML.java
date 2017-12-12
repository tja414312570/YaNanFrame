package com.YaNan.frame.RPC.utils;

public class RPCConfigXML {
	private String name;
	private String host;
	private int port;
	private int timeout;
	private int ReconnectionTimes;
	private String Secret;
	private String Listener;
	public int getReconnectionTimes() {
		return ReconnectionTimes;
	}
	public void setReconnectionTimes(int reconnectionTimes) {
		ReconnectionTimes = reconnectionTimes;
	}
	public String getSecret() {
		return Secret;
	}
	public void setSecret(String secret) {
		Secret = secret;
	}
	public String getListener() {
		return Listener;
	}
	public void setListener(String listener) {
		Listener = listener;
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
		return "RPCConfigure [name=" + name + ", host=" + host + ", port=" + port + ", timeout=" + timeout
				+ ", ReconnectionTimes=" + ReconnectionTimes + ", Secret=" + Secret + ", Listener=" + Listener + "]";
	}
	
	
}
