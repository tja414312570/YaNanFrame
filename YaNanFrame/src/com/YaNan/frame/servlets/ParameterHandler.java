package com.YaNan.frame.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ParameterHandler {
	Object[] getParameters(HttpServletRequest request,HttpServletResponse response,ServletBean servletBean);
}
