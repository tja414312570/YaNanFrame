package com.YaNan.frame.servlets.session.servletSupport;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.servletSupport.MultiFormServlet;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.servlets.session.annotation.TokenObject;
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
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, TokenServlet.class);
	protected transient Token TokenContext;
	public Token getTokenContext() {
		return TokenContext;
	}
	public void setTokenContext(Token tokenContext) {
		TokenContext = tokenContext;
	}
	public void setServletContext(HttpServletRequest request,
			HttpServletResponse response) {
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
						log.error(e.getMessage(),e);
					}
				}
			}
	}
}
