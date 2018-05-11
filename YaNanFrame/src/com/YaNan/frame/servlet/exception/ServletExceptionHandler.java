package com.YaNan.frame.servlet.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletExceptionHandler {

	void exception(Exception e, HttpServletRequest request, HttpServletResponse response);
	
}
