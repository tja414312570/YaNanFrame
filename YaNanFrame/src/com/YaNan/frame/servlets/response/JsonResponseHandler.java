package com.YaNan.frame.servlets.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ServletBean;
import com.google.gson.Gson;

@Register(attribute="com.YaNan.frame.servlets.response.annotations.ResponseJson")
public class JsonResponseHandler implements ResponseHandler{

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		if(!response.isCommitted())
			if(handlerResult!=null)
				response.getWriter().write(new Gson().toJson(handlerResult));
			else
				response.setStatus(404);
	}

}
