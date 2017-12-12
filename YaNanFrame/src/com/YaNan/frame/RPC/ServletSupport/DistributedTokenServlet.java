package com.YaNan.frame.RPC.ServletSupport;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.hibernate.json.Class2Json;
import com.YaNan.frame.security.XSSRequestWrapper;
import com.YaNan.frame.service.Log;
import com.YaNan.frame.support.ignore;
import com.YaNan.frame.RPC.Implements.RequestType;
import com.YaNan.frame.RPC.TokenSupport.RPCToken;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.annotation.TokenObject;
/**
 * 此抽象类用于普通servlet处理，不支持文件的处理，如需要使用文件上传，请 实现接口 DefaultUpload
 * 
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public abstract class DistributedTokenServlet{
	@ignore
	public static String SUCCESS = "success";
	@ignore
	public static String ERROR = "error";
	@ignore
	public static String FAILED = "failed";
	@ignore
	public HttpSession SessionContext;
	@ignore
	public HttpServletRequest RequestContext;
	@ignore
	public HttpServletResponse ResponseContext;
	@ignore
	public String ServicePath;
	@ignore
	public Cookie[] Cookies;
	@ignore
	protected Token TokenContext;
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

	/**
	 * servlet默认执行的方法
	 * 
	 * @return
	 */
	public String execute() {
		return SUCCESS;
	}

	/**
	 * servlet执行前会执行init方法
	 */
	public void init() {

	}

	/**
	 * 当servlet执行完成以后将会执行destory方法
	 */
	public void destory() {

	}	
	public void doOther(ClassLoader loader){
		Field[] fields = loader.getDeclaredFields();
		for(Field field :fields){
			if(field.getAnnotation(TokenObject.class)!=null){
				Class<?> cls = field.getType();
				String method = ClassLoader.createFieldSetMethod(field);
					if (loader.hasMethod(method,cls))
					try {
						if(TokenContext.get(cls)!=null){
							loader.set(field.getName(),TokenContext.get(cls));
						}else{
							TokenObject tokenObj = field.getAnnotation(TokenObject.class);
							String serviceName = tokenObj.ServiceName();
							int soaType = tokenObj.Type();
							RPCToken request = RPCService.getRPCToken(TokenContext,serviceName,cls,soaType,RequestType.GET);
							Object rpcResult = request.request();
							System.out.println("rpc :"+rpcResult);
							if(rpcResult==null)
								continue;
							TokenContext.set(cls, rpcResult);
							loader.set(field.getName(),rpcResult);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						Log.getSystemLog().exception(e);
					}
				}
			}
	}
	public static class Response {
		public static String Error(String reason) {
			return "{status:'error',message:'" + reason + "'}";
		}

		public static String Failed(String reason) {
			return "{status:'failed',message:'" + reason + "'}";
		}

		public static String Success(String reason) {
			return "{status:'success',message:'" + reason + "'}";
		}

		public static String True(String reason) {
			return "{status:'true',message:'" + reason + "'}";
		}

		public static String False(String reason) {
			return "{status:'false',message:'" + reason + "'}";
		}

		public static String Error(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'error',message:" + json.toJson() + "}";
		}

		public static String Failed(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'failed',message:" + json.toJson() + "}";
		}

		public static String Success(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'success',message:" + json.toJson() + "}";
		}

		public static String True(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'true',message:" + json.toJson() + "}";
		}

		public static String False(Class2Json json)
				throws IllegalArgumentException, IllegalAccessException {
			return "{status:'false',message:" + json.toJson() + "}";
		}
	}
}
