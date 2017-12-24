package com.YaNan.frame.core.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.servlet.annotations.RESPONSE_METHOD;
import com.YaNan.frame.core.servlet.annotations.Validate;
import com.YaNan.frame.service.Log;
import com.YaNan.frame.stringSupport.StringSupport;

/**
 * 	action组件2.0.0，新增注解配置，新增验证器，Action注解@Action,配合可重复注解@Result，验证器注解@Valiate
 * @version 2.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
@WebServlet("*.do")
public class DefaultDispatcher extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static long MaxSize=1024*1024*2;
	// 日志类，用于输出日志
	private Log log = Log.getSystemLog();

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
		String servletName = URLSupport.getServlet(request);
		String namespace = URLSupport.getNameSpace(request);
		HttpSession session = request.getSession();
		// 获取servletMap实例
		defaultServletMapping servletMap = defaultServletMapping.getInstance();
		// 如果ServletBean存在
		if (servletMap.isExist(namespace,servletName)) {
			// get ServletBean
			ServletBean bean = servletMap.getServlet(namespace,servletName);
			if(bean.isCorssOrgin()){
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
				response.setHeader("Access-Control-Allow-Credentials","true");
			}
			// get nameSpace
			String beanNamespace = bean.getNameSpace();
			String urlNamespace = URLSupport.getNameSpace(request);
			// if action,s nameSpace is not equals URL nameSpace,stop task
			if (!beanNamespace.equals("*")) {
				if (!beanNamespace.equals(urlNamespace))
					log.error("could not find action");
			}
					try {
						ClassLoader loader = new ClassLoader(bean.getClassName());
						try {
						//init setServletContext method
						if (loader.hasMethod("setServletContext",
								RequestFacade.class, ResponseFacade.class)) {
								loader.invokeMethod("setServletContext", request,
										response);
						}
						//invoke doOther method
						if (loader.hasMethod("doOther",
								ClassLoader.class)) 
							loader.invokeMethod("doOther", loader);
						//invoke init method
						if (loader.hasMethod("init")) 
							loader.invokeMethod("init");
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
								Field field = getField(loader, fieldstr);
								if(field==null){
									response.getWriter().write("Action [ "+servletName+" ] error,The parameter [ "+fieldstr+" ] that needs to be validated does not exist! at class : "+bean.getClassName().getName());
									return;
								}
								Validate validate = field.getAnnotation(Validate.class);
								if(validate == null){
									response.getWriter().write("Action [ "+servletName+" ] error,The parameter [ "+fieldstr+"] that needs to be validated but the validate annotations does not exist! at class : "+bean.getClassName().getName());
									return;
								}
								Object value = loader.getFieldValue(field);
								if(value==null){
									if(validate.isNull().equals(""))
										valuationFailed(validate, "Action [ "+servletName+" ] error,The parameter [ "+fieldstr+" ] that needs to be validated but the parameter is null! at class : "+bean.getClassName().getName(), response);
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
							e.printStackTrace();
							Object reObj = e.getCause();
//							if(loader.hasMethod("exception", Exception.class))
//								reObj = loader.invokeMethod("exception",new Class<?>[]{Exception.class},e);
							response(bean, reObj.toString(),
									request, response,loader);
						log.exception(e);
						}
					} catch (Exception e) {
						response(bean, "Action error ["+servletName+"], casuse "+e.getMessage(),
								request, response,null);
						log.exception(e);
						return;
					}
		} else {
			log.write("could not find servlet :" + servletName+" at namespace "+namespace);
			response.getWriter().write("could not find servlet :" + servletName+" at namespace "+namespace);
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
	 */
	private void invoke(ServletBean bean, ClassLoader loader,
			HttpSession session,HttpServletRequest request , HttpServletResponse response) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
				Log.getSystemLog().exception(e);
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
				else
				    field.set(loader.getLoadedObject(),ClassLoader.castType(value, field.getType()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		return true;
	}

	

	/**
	 * 判断是否用户需要跳转,或自己获取输出流，不需要则直接将回调传入前台
	 * if(bean.hasResult()&&bean.hasResultName(callResult)){
	 * response.sendRedirect(bean.getResult(callResult)); return; }else{
	 * out.write(callResult); return; }
	 */
	private void response(ServletBean bean, String callResult,HttpServletRequest request,
			HttpServletResponse response,ClassLoader loader) {
		try {	
			if (!bean.hasOutputStream())
				if (bean.hasResult() && bean.hasResultName(callResult)) {
					ServletResult result= bean.getResult(callResult);
					switch(result.getMethod()){
						case RESPONSE_METHOD.OUTPUT:
							response.getWriter().write(bean.decode()?StringSupport.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						case RESPONSE_METHOD.REDIRCET:
							response.sendRedirect(bean.decode()?StringSupport.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						case RESPONSE_METHOD.FORWARD:
							request.getRequestDispatcher(bean.decode()?StringSupport.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue()).forward(request, response);
							break;
						default :
							response.getWriter().write(bean.decode()?StringSupport.decodeVar(result.getValue(),loader.getLoadedObject()):result.getValue());
							break;
						}
				} else {
					switch(bean.getType()){
					case RESPONSE_METHOD.OUTPUT:
						response.getWriter().write(bean.decode()?StringSupport.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					case RESPONSE_METHOD.REDIRCET:
						response.sendRedirect(bean.decode()?StringSupport.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					case RESPONSE_METHOD.FORWARD:
						request.getRequestDispatcher(bean.decode()?StringSupport.decodeVar(callResult,loader.getLoadedObject()):callResult).forward(request, response);
						break;
					default :
						response.getWriter().write(bean.decode()?StringSupport.decodeVar(callResult,loader.getLoadedObject()):callResult);
						break;
					}
				}
			//destroy
			if (loader!=null&&loader.hasMethod("destroy"))
					loader.invokeMethod("destroy");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ServletException | IOException e) {
			e.printStackTrace();
		}finally{
			loader=null;
			System.gc();
		}
	}

}
