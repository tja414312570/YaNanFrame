package com.YaNan.frame.servlets.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;

@Register
public class ExceptionHandler implements ServletExceptionHandler{

	@Override
	public void exception(Throwable e, HttpServletRequest request, HttpServletResponse response) {
		
	}
	
}
