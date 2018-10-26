package com.YaNan.frame.servlets.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletExceptionHandler {

	void exception(Throwable e, HttpServletRequest request, HttpServletResponse response);
	
}
