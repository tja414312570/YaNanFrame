package com.YaNan.frame.servlets.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.response.annotations.ResponseJson;
import com.google.gson.Gson;

@Register(attribute="com.YaNan.frame.servlets.response.annotations.ResponseJson")
public class JsonResponseHandler implements ResponseHandler{
	private final static Gson gson = new Gson();

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		if(!response.isCommitted())
			if(handlerResult!=null){
				ResponseJson anno = (ResponseJson) annotation;
				response.getWriter().write(new StringBuffer(anno.prefix())
						.append(gson.toJson(handlerResult))
						.append(anno.suffix()).toString());
			}
			else
				response.setStatus(404);
	}

}
