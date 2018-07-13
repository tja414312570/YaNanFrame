package com.YaNan.frame.servlets.session.parameter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ParameterHandlerCache;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.parameter.ParameterHandler;
import com.YaNan.frame.servlets.session.Token;

/**
 * 默认的参数调配器
 * @author yanan
 *
 */
@Register(attribute={"com.YaNan.frame.servlets.session.parameter.TokenAttribute","com.YaNan.frame.servlets.session.Token"},signlTon=false,priority=-20180519)
public class TokenParameterHandler implements ParameterHandler{
	//获取session attribute 名称迭代器
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	private ServletBean servletBean;
	private Token token;
	
	
	@Override
	public void initHandler(HttpServletRequest request, HttpServletResponse response, ServletBean servletBean,ParameterHandlerCache parameterHandlerCache) {
		this.servletBean = servletBean;
		this.servletRequest = request;
		this.servletResponse = response;
		this.token = Token.getToken(request);
		if(token==null)
			token = Token.addToken(request, response);
	}

	@Override
	public Object getParameter(Parameter parameter, Annotation parameterAnnotation) {
		if(token!=null){
			TokenAttribute tokenAttributes = (TokenAttribute) parameterAnnotation;
			if(tokenAttributes.value().trim().equals("")){
				Object value = token.get(parameter.getType());
				if(value==null)
					value = token.get(parameter.getType().getName());
				return value;
			}
			return token.get(tokenAttributes.value());
		}
		return null;
	}

	@Override
	public Object getParameter(Parameter parameter) throws IOException {
		return token;
	}


	@Override
	public Object getParameter(Field field, Annotation parameterAnnotation) {
		if(token!=null){
			TokenAttribute tokenAttributes = (TokenAttribute) parameterAnnotation;
			if(tokenAttributes.value().trim().equals("")){
				Object value = token.get(field.getType());
				if(value==null)
					value = token.get(field.getType().getName());
				return value;
			}
			return token.get(tokenAttributes.value());
		}
		return null;
	}


	@Override
	public Object getParameter(Field field) throws IOException {
		return token;
	}


	@Override
	public ServletBean getServletBean() {
		return this.servletBean;
	}


	@Override
	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}


	@Override
	public HttpServletResponse getServletResponse() {
		return this.servletResponse;
	}

}
