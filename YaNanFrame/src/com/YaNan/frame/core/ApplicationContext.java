package com.YaNan.frame.core;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class ApplicationContext {
	private static ApplicationContext applicationContext;
	private String localAddr;
	private Locale local;
	private String localName;
	private String scheme;
	private int localPort;
	private String serverName;
	private int serverPort;
	private ServletContext servletContext;
	private String remoteAddr;
	private String remoteHost;
	private int remotePort;

	private ApplicationContext() {

	}

	private ApplicationContext(ServletRequest request, ServletResponse response) {
		this.localAddr = request.getLocalAddr();
		this.local = request.getLocale();
		this.localName = request.getLocalName();
		this.scheme = request.getScheme();
		this.localPort = request.getLocalPort();
		this.serverName = request.getServerName();
		this.serverPort = request.getServerPort();
		this.servletContext = request.getServletContext();
		this.remoteAddr = request.getRemoteAddr();
		this.remoteHost = request.getRemoteHost();
		this.remotePort = request.getRemotePort();
	}

	public ApplicationContext getContext() {
		return applicationContext;
	}

	public static void init(ServletRequest request, ServletResponse response) {
		if (applicationContext == null)
			applicationContext = new ApplicationContext(request, response);
	}

	@Override
	public String toString() {
		return "ApplicationContext [localAddr=" + localAddr + ", local="
				+ local + ", localName=" + localName + ", scheme=" + scheme
				+ ", localPort=" + localPort + ", serverName=" + serverName
				+ ", serverPort=" + serverPort + ", servletContext="
				+ servletContext + ", remoteAddr=" + remoteAddr
				+ ", remoteHost=" + remoteHost + ", remotePort=" + remotePort
				+ "]";
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public Locale getLocal() {
		return local;
	}

	public String getLocalName() {
		return localName;
	}

	public String getScheme() {
		return scheme;
	}

	public int getLocalPort() {
		return localPort;
	}

	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}
}
