package com.YaNan.frame.servlets.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.response.annotations.Redirect;

@Register(attribute="com.YaNan.frame.servlets.response.annotations.Redirect")
public class RedirectResponseHandler implements ResponseHandler{

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder(handlerResult.toString());
		if(annotation!=null)
			sb.insert(0, ((Redirect)annotation).prefix()).append(((Redirect)annotation).suffix());
		response.sendRedirect(sb.toString());
	}

}
