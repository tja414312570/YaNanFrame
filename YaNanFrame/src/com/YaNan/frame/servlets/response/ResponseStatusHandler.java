package com.YaNan.frame.servlets.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ServletBean;

@Register(attribute={"com.YaNan.frame.servlets.response.annotations.ResponseStatus","com.YaNan.frame.servlets.response.ResponseStatus"})
public class ResponseStatusHandler implements ResponseHandler{

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		response.setContentType(request.getContentType());
		if(annotation!=null){
			if(handlerResult==null)
				response.setStatus(200);
			else if(handlerResult.getClass().equals(ResponseStatus.class)){
				ResponseStatus res = ((ResponseStatus)handlerResult);
				response.setStatus(res.getStatus());
				if(res.getMessage()!=null)
					response.getWriter().write(res.getMessage());
			}else
				response.setStatus(Integer.parseInt(handlerResult.toString()));
		}else{
			if(handlerResult.getClass().equals(ResponseStatus.class)){
				ResponseStatus res = ((ResponseStatus)handlerResult);
				response.setStatus(res.getStatus());
				if(res.getMessage()!=null)
					response.getWriter().write(res.getMessage());
			}else
			response.setStatus(200);
		}
		
	}

}
