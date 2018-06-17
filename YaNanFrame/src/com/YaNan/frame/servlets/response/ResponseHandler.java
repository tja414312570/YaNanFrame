package com.YaNan.frame.servlets.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.servlets.ServletBean;

@Service
public interface ResponseHandler {

	void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,
			Annotation responseAnnotation, ServletBean servletBean) throws ServletException, IOException;

}
