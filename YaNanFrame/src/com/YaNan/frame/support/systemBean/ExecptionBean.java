package com.YaNan.frame.support.systemBean;

import com.YaNan.frame.core.servletSupport.DefaultServlet;

public class ExecptionBean extends DefaultServlet {
	private final int NotFoundPage = 404;
	private final int InnerException = 500;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String whenError() {
		return null;
	}
}
