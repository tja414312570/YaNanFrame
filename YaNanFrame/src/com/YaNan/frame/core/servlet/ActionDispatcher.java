package com.YaNan.frame.core.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.servlet.annotations.RESPONSE_METHOD;
import com.YaNan.frame.core.servlet.annotations.Validate;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;
import com.YaNan.frame.stringSupport.StringUtil;

/**
 * 	action组件2.0.0，新增注解配置，新增验证器，Action注解@Action,配合可重复注解@Result，验证器注解@Valiate
 * @version 2.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class ActionDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static long MaxSize=1024*1024*2;
	protected transient WebResourceRoot resources = null;
	// 日志类，用于输出日志
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, ActionDispatcher.class);
	protected boolean showServerInfo = true;
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 调用doPost方法
		doPost(request, response);
	}
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应属性
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		// 获取url相对路径
		String urlMapping = CoreDispatcher.getRelativePath(request, true);
		DefaultServletMapping servletMap = DefaultServletMapping.getInstance();
		ServletBean bean = servletMap.getServlet(urlMapping);
		// 如果ServletBean存在
		if (bean!=null) {
			//判断映射是否跨域
			if(bean.isCorssOrgin()){
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
				response.setHeader("Access-Control-Allow-Credentials","true");
			}
			try {
				ClassLoader loader = new ClassLoader(bean.getServletClass());
				try {
				//判断是否含有setServletContext方法
				if (loader.hasMethod("setServletContext",
						RequestFacade.class, ResponseFacade.class)) {
						loader.invokeMethod("setServletContext", request,
								response);
				}
				//判断是否含有doOther方法
				if (loader.hasMethod("doOther",
						ClassLoader.class)) 
					loader.invokeMethod("doOther", loader);
				// 非表单处理
				Enumeration<String> parameters = request
						.getParameterNames();
				while (parameters.hasMoreElements()) {
					String element = parameters.nextElement();
					String value = request.getParameter(element);
					if(!valuation(loader, element, value,response))return;
				}
				// 表单处理
				if(loader.hasMethod("MultiFormSupport",ClassLoader.class,ServletBean.class)){
					loader.invokeMethod("MultiFormSupport",loader,
							bean);
				}
				//字段验证
				if(bean.getArgs().length!=0){
					for(String fieldstr : bean.getArgs()){
						int splInx = fieldstr.indexOf(":");
						String filedStr = splInx>0?fieldstr.substring(0, splInx):fieldstr;
						Field field=getField(loader, filedStr);
						if(field==null){
							log.error(new ServletException("Action [ "+urlMapping+" ] error,The parameter [ "+filedStr+" ] that needs to be validated does not exist! at class : "+bean.getServletClass().getName()));
							throw new ServletException("Action [ "+urlMapping+" ] error,The parameter [ "+filedStr+" ] that needs to be validated does not exist! at class : "+bean.getServletClass().getName());
						}
						log.debug(splInx+"");
						if(splInx>0){
							String defaultValue = fieldstr.substring(splInx);
							setField(field,loader,defaultValue,response);
							continue;
						}
						Validate validate = field.getAnnotation(Validate.class);
						if(validate == null){
							log.error(new ServletException("Action [ "+urlMapping+" ] error,The parameter [ "+filedStr+"] that needs to be validated but the validate annotations does not exist! at class : "+bean.getServletClass().getName()));
							//response.sendError(sc, msg);
							
							throw new ServletException("Action [ "+urlMapping+" ] error,The parameter [ "+filedStr+"] that needs to be validated but the validate annotations does not exist! at class : "+bean.getServletClass().getName());
						}
						Object value = loader.getFieldValue(field);
						if(value==null){
							if(validate.isNull().equals(""))
								valuationFailed(validate, "Action [ "+urlMapping+" ] error,The parameter [ "+filedStr+" ] that needs to be validated but the parameter is null! at class : "+bean.getServletClass().getName(), response);
							else
								valuationFailed(validate,validate.isNull(), response);
							return;
						}else {
							if(!field.getType().equals(String.class)&&!valuationValue(validate, value, response))return;
						}
					}
				}
				
				this.invoke(bean, loader, session,request, response);
				} catch (Exception e) {
					log.error("Action error at \"" + urlMapping+"\"",e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Action error at \"" + urlMapping+"\" , Cause By :"+e.getCause());
					e.printStackTrace();
					return ;
				}
			} catch (Exception e) {
				log.error("Action error at \"" + urlMapping+"\"",e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Action error at \"" + urlMapping+"\" , Cause By :"+e.getCause());
				e.printStackTrace();
				return ;
			}
		} else {
			Exception exception = new ServletException("Could not find action \"" + urlMapping+"\"");
			log.error(exception);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Could not find action \"" + urlMapping+"\"");
			exception.printStackTrace();
			return ;
		}

	}
	
	/**
	 * 执行需要返回值的javaBean方法
	 * 
	 * @param bean
	 * @param loader
	 * @param session
	 * @param response
	 * @param request 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void invoke(ServletBean bean, ClassLoader loader,
			HttpSession session,HttpServletRequest request , HttpServletResponse response) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ServletException {
		String callResult = null;
		if (bean.hasOutputStream()) {
			loader.invokeMethod(bean.getMethod().getName());
		} else {
			Object callBack = loader.invokeMethod(bean.getMethod().getName());
			callResult = callBack == null ? "null" : callBack
					.toString();
			this.response(bean, callResult,request, response,loader);
		}
	}

	/**
	 * 用于给变量赋值。
	 * 
	 * @param loader
	 * @param field
	 * @param value
	 * @throws IOException 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static boolean valuation(ClassLoader loader, String field, Object value,HttpServletResponse response) throws IOException {
			try {
				field = field.trim();
				if (loader.hasDeclaredField(field)) {
					Field f = loader.getDeclaredField(field);
					return setField(f,loader,value,response);
				}else{
					ClassLoader cLoader = new ClassLoader(loader.getLoadedClass().getSuperclass(),false);
					if(cLoader.hasDeclaredField(field)){
						Field f = cLoader.getDeclaredField(field);
						return setField(f,loader,value,response);
					}
				}
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			return true;
	}
	public static Field getField(ClassLoader loader, String field) {
		try {
			if (loader.hasDeclaredField(field)) {
				Field f = loader.getDeclaredField(field);
				return f;
			}else{
				ClassLoader cLoader = new ClassLoader(loader.getLoadedClass().getSuperclass(),false);
				if(cLoader.hasDeclaredField(field)){
					Field f = cLoader.getDeclaredField(field);
					return f;
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void valuationFailed(Validate validate,String message,HttpServletResponse response) throws IOException{
		switch(validate.method()){
			case RESPONSE_METHOD.OUTPUT:
				response.getWriter().write(message);
				break;
			case RESPONSE_METHOD.REDIRCET:
				response.sendRedirect(message);
				break;
			default :
				response.getWriter().write(message);
				break;
			}
	}
	public static boolean valuationValue(Validate validate,Object value,HttpServletResponse response) throws IllegalArgumentException, IllegalAccessException, IOException{
		if(!value.toString().matches(validate.RegExpression())){
			valuationFailed(validate, validate.Failed().replace("${value}",value.toString()), response);
			return false;
			}
		return true;
	}
	public static boolean setField(Field field,ClassLoader loader,Object value,HttpServletResponse response) throws IOException{
			try {
				Validate validate = field.getAnnotation(Validate.class);
				if(validate!=null&&!valuationValue(validate, value, response))
					return false;
				if (loader.hasMethod(ClassLoader.createFieldSetMethod(field),
						field.getType()))
					loader.set(field.getName(), field.getType(), ClassLoader.castType(value, field.getType()));
				else{
					field.setAccessible(true);
					field.set(loader.getLoadedObject(),ClassLoader.castType(value, field.getType()));
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		return true;
	}

	public static String getExceptionTrace(Exception e){
		StringBuilder sb = new StringBuilder("Exception Trace:");
		StackTraceElement[]	 sts = e.getStackTrace();
		for(int i = 0;i<sts.length;i++)
			sb.append("<li>"+sts[i]+"</li>");
		return sb.toString();
	}

	/**
	 * 判断是否用户需要跳转,或自己获取输出流，不需要则直接将回调传入前台
	 * if(bean.hasResult()&&bean.hasResultName(callResult)){
	 * response.sendRedirect(bean.getResult(callResult)); return; }else{
	 * out.write(callResult); return; }
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void response(ServletBean bean, String callResult,HttpServletRequest request,
			HttpServletResponse response,ClassLoader loader) throws IOException, ServletException {
			if (!bean.hasOutputStream())
				if (bean.hasResult() && bean.hasResultName(callResult)) {
					ServletResult result= bean.getResult(callResult);
					switch(result.getMethod()){
						case RESPONSE_METHOD.OUTPUT:
							response.getWriter().write(bean.decode()?StringUtil.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						case RESPONSE_METHOD.REDIRCET:
							response.sendRedirect(bean.decode()?StringUtil.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						case RESPONSE_METHOD.FORWARD:
							request.getRequestDispatcher(bean.decode()?StringUtil.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue()).forward(request, response);
							break;
						default :
							response.getWriter().write(bean.decode()?StringUtil.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						}
				} else {
					switch(bean.getType()){
					case RESPONSE_METHOD.OUTPUT:
						response.getWriter().write(bean.decode()?StringUtil.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					case RESPONSE_METHOD.REDIRCET:
						response.sendRedirect(bean.decode()?StringUtil.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					case RESPONSE_METHOD.FORWARD:
						request.getRequestDispatcher(bean.decode()?StringUtil.decodeVar(callResult,loader.getLoadedObject()):callResult).forward(request, response);
						break;
					default :
						response.getWriter().write(bean.decode()?StringUtil.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					}
				}
			bean = null;
			loader = null;
	}
}
