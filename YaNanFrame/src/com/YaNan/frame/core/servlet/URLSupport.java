package com.YaNan.frame.core.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class URLSupport {
	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/
	 * 
	 * @param request
	 * @return
	 */
	public static String getRootURL(ServletRequest request) {
		String cUrl = request.getScheme()+"://" + request.getServerName() + ":"
				+ request.getServerPort();
		int port = request.getServerPort();
		if(port==80)
			cUrl=cUrl.replace(":80", "");
		if(port==443)
			cUrl=cUrl.replace(":443", "");
		return cUrl;
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/
	 * 
	 * @param request
	 * @return
	 */
	public static String getContextURL(ServletRequest request) {
		return getRootURL(request)+ ((HttpServletRequest) request).getContextPath() + "/";
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/YaNan/r.do
	 * 
	 * @param request
	 * @return
	 */
	public static String getURL(HttpServletRequest request) {
		String cUrl = request.getRequestURL().toString();
		int port = request.getServerPort();
		if(port==80)
			cUrl=cUrl.replace(":80", "");
		if(port==443)
			cUrl=cUrl.replace(":443", "");
		return cUrl;
		
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> r
	 * 
	 * @param request
	 * @return
	 */
	public static String getServlet(HttpServletRequest request) {
		String url = getURL(request);
		return url.substring(url.lastIndexOf("/") + 1).replace(".do", "");
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> /YaNan/r.do
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request) {
		return getURL(request).replace(getContextURL(request), "/");
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> /YaNan
	 * 
	 * @param request
	 * @return
	 */
	public static String getNameSpace(HttpServletRequest request) {
		String url = getURL(request);
		String cUrl = getContextURL(request);
		if (url.equals(cUrl))
			return "/";
		if(!url.substring(cUrl.length(), url.length()).contains("/"))
			return "/";
		return url.substring(cUrl.length()-1,
				url.lastIndexOf("/")+1);
	}

	public static Object getRequestFile(ServletRequest request) {
		String url = getURL((HttpServletRequest) request);
		if (url.length() == url.lastIndexOf("/"))
			return "";
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	public static String getContextIP(ServletRequest request) {
		System.out.println("localAddr:"+request.getLocalAddr());
		System.out.println("LocalName:"+request.getLocalName());
		System.out.println("LocalPort:"+request.getLocalPort());
		System.out.println("Protocol:"+request.getProtocol());
		System.out.println("RemoteAddr:"+request.getRemoteAddr());
		System.out.println("RemoteHost:"+request.getRemoteHost());
		System.out.println("RemotePort:"+request.getRemotePort());
		System.out.println("Scheme:"+request.getScheme());
		System.out.println("ServerName:"+request.getServerName());
		System.out.println("ServerPort:"+request.getServerPort());
		
		return null;
	}
}
