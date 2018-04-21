package com.YaNan.frame.servlets.servletSupport;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 此抽象类用于普通servlet处理，不支持文件的处理，如需要使用文件上传，请使用MultiFormServlet
 * 
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public abstract class DefaultServlet {
	public transient static String SUCCESS = "success";
	public transient static String ERROR = "error";
	public transient static String FAILED = "failed";
	public transient HttpSession SessionContext=null;
	public transient HttpServletRequest RequestContext=null;
	public transient HttpServletResponse ResponseContext=null;
	public transient String ServicePath=null;
	public transient Cookie[] Cookies=null;
	public void setServletContext(HttpServletRequest request, HttpServletResponse response) {
		RequestContext = request;
		ResponseContext = response;
		SessionContext = RequestContext.getSession();
		ServicePath = "http://" + RequestContext.getServerName() + ":"
				+ RequestContext.getServerPort()
				+ RequestContext.getContextPath();
		Cookies = RequestContext.getCookies();
	}

	/**
	 * servlet默认执行的方法
	 * 
	 * @return
	 */
	public String execute() {
		return SUCCESS;
	}

	/**
	 * servlet执行前会执行init方法
	 */
	public void init() {

	}

	/**
	 * 当servlet执行完成以后将会执行destory方法
	 */
	public void destroy() {

	}
	/**
	 * 当异常出现时
	 * @param e
	 * @return
	 */
	public Object exception(Exception e){
		return e.getCause();
	}
}
