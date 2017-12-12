package com.YaNan.frame.core.session.servletSupport;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.servletSupport.MultiFormServlet;
import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.annotation.TokenObject;
import com.YaNan.frame.security.XSSRequestWrapper;
import com.YaNan.frame.service.Log;
import com.YaNan.frame.support.ignore;
/**
 * 此抽象类用于普通带Token的servlet处理
 * 
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public abstract class TokenServlet extends MultiFormServlet{
	@ignore
	protected transient Token TokenContext;
	public Token getTokenContext() {
		return TokenContext;
	}
	public void setTokenContext(Token tokenContext) {
		TokenContext = tokenContext;
	}
	@Override
	public void setServletContext(RequestFacade request, ResponseFacade response) {
		RequestContext = request;
		ResponseContext = response;
		SessionContext = RequestContext.getSession();
		ServicePath = "http://" + RequestContext.getServerName() + ":"
				+ RequestContext.getServerPort()
				+ RequestContext.getContextPath();
		Cookies = RequestContext.getCookies();
		if(Token.getToken(RequestContext)==null){
			this.TokenContext = Token.addToken(RequestContext, ResponseContext);
		}else{
			TokenContext=Token.getToken(RequestContext);
		}
	}
	@Override
	public void setServletContext(XSSRequestWrapper request,
			ResponseFacade response) {
		RequestContext = request;
		ResponseContext = response;
		SessionContext = RequestContext.getSession();
		ServicePath = "http://" + RequestContext.getServerName() + ":"
				+ RequestContext.getServerPort()
				+ RequestContext.getContextPath();
		Cookies = RequestContext.getCookies();
		if(Token.getToken(RequestContext)==null){
			this.TokenContext = Token.addToken(RequestContext, ResponseContext);
		}else{
			TokenContext=Token.getToken(RequestContext);
		}
	}
	public void doOther(ClassLoader loader){
		Field[] fields = loader.getDeclaredFields();
		for(Field field :fields){
			if(field.getAnnotation(TokenObject.class)!=null){
				Class<?> cls = field.getType();
				String method = ClassLoader.createFieldSetMethod(field);
					if (loader.hasMethod(method,cls)&&TokenContext.get(cls)!=null)
					try {
						loader.set(field.getName(),TokenContext.get(cls));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						Log.getSystemLog().exception(e);
					}
				}
			}
	}
}
