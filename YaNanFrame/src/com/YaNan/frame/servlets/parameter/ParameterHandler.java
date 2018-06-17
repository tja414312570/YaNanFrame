package com.YaNan.frame.servlets.parameter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.servlets.ParameterHandlerCache;
import com.YaNan.frame.servlets.ServletBean;

public interface ParameterHandler {
	void initHandler(HttpServletRequest request,HttpServletResponse response,ServletBean servletBean, ParameterHandlerCache parameterHandlerCache);
	Object getParameter(Parameter parameter, Annotation parameterAnnotation);
	Object getParameter(Parameter parameter) throws IOException;
	Object getParameter(Field field, Annotation parameterAnnotation);
	Object getParameter(Field field) throws IOException;
	ServletBean getServletBean();
	HttpServletRequest getServletRequest();
	HttpServletResponse getServletResponse();
	
	
}
