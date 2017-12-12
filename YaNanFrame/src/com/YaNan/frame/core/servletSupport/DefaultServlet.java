package com.YaNan.frame.core.servletSupport;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.hibernate.json.Class2Json;
import com.YaNan.frame.security.XSSRequestWrapper;
import com.YaNan.frame.support.ignore;

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
	@ignore
	public transient static String SUCCESS = "success";
	@ignore
	public transient static String ERROR = "error";
	@ignore
	public transient static String FAILED = "failed";
	@ignore
	public transient HttpSession SessionContext=null;
	@ignore
	public transient HttpServletRequest RequestContext=null;
	@ignore
	public transient HttpServletResponse ResponseContext=null;
	@ignore
	public transient String ServicePath=null;
	@ignore
	public transient Cookie[] Cookies=null;
	public void setServletContext(RequestFacade request, ResponseFacade response) {
		RequestContext = request;
		ResponseContext = response;
		SessionContext = RequestContext.getSession();
		ServicePath = "http://" + RequestContext.getServerName() + ":"
				+ RequestContext.getServerPort()
				+ RequestContext.getContextPath();
		Cookies = RequestContext.getCookies();
	}

	public void setServletContext(XSSRequestWrapper request,
			ResponseFacade response) {
		RequestContext = (HttpServletRequest) request;
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
	public static class Response {
		public static String Error(String reason) {
			return "{status:'error',message:'" + reason + "'}";
		}

		public static String Failed(String reason) {
			return "{status:'failed',message:'" + reason + "'}";
		}

		public static String Success(String reason) {
			return "{status:'success',message:'" + reason + "'}";
		}

		public static String True(String reason) {
			return "{status:'true',message:'" + reason + "'}";
		}

		public static String False(String reason) {
			return "{status:'false',message:'" + reason + "'}";
		}

		public static String Error(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'error',message:" + json.toJson() + "}";
		}

		public static String Failed(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'failed',message:" + json.toJson() + "}";
		}

		public static String Success(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'success',message:" + json.toJson() + "}";
		}

		public static String True(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'true',message:" + json.toJson() + "}";
		}

		public static String False(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'false',message:" + json.toJson() + "}";
		}
	}
}
