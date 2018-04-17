package com.YaNan.frame.core.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.WebResourceRoot;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

import com.YaNan.frame.core.servlet.ParameterDescription.ParameterType;;

/**
 * 	Restful 核心调配器
 * @version 1.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class RestfulDispatcher extends HttpServlet {
	private static final long serialVersionUID = -1089658849875241044L;
	public static final String DEFAULT_METHOD_PARAM = "_method";
	protected transient WebResourceRoot resources = null;
	// 日志类，用于输出日志
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, RestfulDispatcher.class);
	protected boolean showServerInfo = true;
	protected Servlet servlet;
	
	 /**
     * Receives standard HTTP requests from the public
     * <code>service</code> method and dispatches
     * them to the <code>do</code><i>Method</i> methods defined in
     * this class. This method is an HTTP-specific version of the
     * {@link javax.servlet.Servlet#service} method. There's no
     * need to override this method.
     *
     * @param req   the {@link HttpServletRequest} object that
     *                  contains the request the client made of
     *                  the servlet
     *
     * @param resp  the {@link HttpServletResponse} object that
     *                  contains the response the servlet returns
     *                  to the client
     *
     * @exception IOException   if an input or output error occurs
     *                              while the servlet is handling the
     *                              HTTP request
     *
     * @exception ServletException  if the HTTP request
     *                                  cannot be handled
     *
     * @see javax.servlet.Servlet#service
     */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		//获得相对路径
		String urlMapping = CoreDispatcher.getRelativePath(request, true);
		String path = urlMapping;
		/**
		 * 这里应该组装Servlet
		 * 对servelt进行组装，判断请求方式
		 * post情况下  包含  _method = delete put 则为对应的方法，否则为post
		 * @ 符号与数字组合表示url的类型 
		 */
		String method = request.getMethod();
		if(method.equals("GET")){
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.GET).toString();
		}else if(method.equals("POST")){
			method = request.getParameter(DEFAULT_METHOD_PARAM);
			log.debug(method);
			if(method==null){
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.POST).toString();
			}else if(method.toUpperCase().equals("DELETE")){
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.DELETE).toString();
			}else if(method.toUpperCase().equals("PUT")){
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.PUT).toString();
			}else if(method.toUpperCase().equals("POST")){
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.POST).toString();
			}else{
				log.warn("servlet handle not process this request method ! request url :"+urlMapping+",method:"+method);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			//组装post delete put 请求
		}else if(method.equals("DELETE")){
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.DELETE).toString();
		}else if(method.equals("PUT")){
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.PUT).toString();
		}else if(method.equals("HEAD")){
			this.doHead(request, response);
			return;
		}else if(method.equals("OPTIONS")){
			this.doOptions(request, response);
			return;
		}else if(method.equals("TRACE")){
			this.doTrace(request, response);
			return;
		}else{
			log.warn("servlet handle not process this request method ! request url :"+urlMapping+",method:"+method);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		log.debug(method);
		log.debug(urlMapping);
		//获取ServletBean映射实例以及获取ServletBean
		ServletBean bean = DefaultServletMapping.getInstance().getServlet(urlMapping);
		//判断ServletBean是否存在  
		if(bean==null){
			log.warn("servlet handle not found! request url :"+urlMapping);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
			}
		//判断ServletBean的类型并分发给对应的处理方式  此处之所以没有独立出来是因为 switch(int)效率非常高   可以达到1000W*50/s的转换率
		switch(bean.getRequestMethod()){
		  	case REQUEST_METHOD.DELETE:
	        	doDelete(request, response, bean,path);
	        	break;
	        case REQUEST_METHOD.GET:
	        	doGet(request, response, bean,path);
	        	break;
	        case REQUEST_METHOD.POST:
	        	doPost(request, response, bean,path);
	        	break;
	        case REQUEST_METHOD.PUT:
	        	doPut(request, response, bean,path);
	        	break;
			case REQUEST_METHOD.HEAD:
				doHead(request, response);
				break;
			case REQUEST_METHOD.OPTIONS:
				doOptions(request, response);
				break;
			case REQUEST_METHOD.TRACE:
				doTrace(request, response);
				break;
			default:
				response.sendError(506);
			break;
			}
	    }
	/**
     * Return the relative path associated with this servlet.
     *
     * @param request The servlet request we are processing
     * @return the relative path
     */
    protected String getRelativePath(HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        // IMPORTANT: DefaultServlet can be mapped to '/' or '/path/*' but always
        // serves resources from the web app root with context rooted paths.
        // i.e. it cannot be used to mount the web app root under a sub-path
        // This method must construct a complete context rooted path, although
        // subclasses can change this behaviour.

        String servletPath;
        String pathInfo;
        if (request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null) {
            // For includes, get the info from the attributes
            pathInfo = (String) request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
            servletPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
        } else {
            pathInfo = request.getPathInfo();
            servletPath = request.getServletPath();
        }
        StringBuilder result = new StringBuilder();
        if (servletPath.length() > 0) {
            result.append(servletPath);
        }
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0 && !allowEmptyPath) {
            result.append('/');
        }
        return result.toString();
    }
    /**
     * 获取路径中的变量    需要传入一个对变量的描述的集合
     * 返回根据路径获取的变量后的集合 该集合存储类型   变量名==》变量值
     * @param pathVariables
     * @param path
     * @return
     */
    public Map<String, String> getPathValues(final Map<Integer, String> pathVariables,String path){
		Map<String,String> pathValues = new LinkedHashMap<String, String>();
		int Index = path.indexOf("/");
		int count = 0;
		while(Index>=0){
			count ++;
			String pvi = pathVariables.get(count);
			int Endex = path.indexOf("/",Index+1);
			if(Endex<0){
				if(pvi!=null)
					pathValues.put(pvi,path.substring(Index+1));
				break;
			}
			if(pvi!=null)
				pathValues.put(pvi,path.substring(Index+1,Endex));
			Index =Endex;
		}
		return pathValues;
    }
	/**
	* Servlet get方法
	* 支持注解 PathVariable RequestParam赋值
	*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,String resource)throws ServletException, IOException {
	  
		//获得bean中的参数  该集合类型为  参数 ==》参数注解
		Map<Parameter, ParameterDescription> parameterDescription = servletBean.getParameters();
		//如果没有参数  直接执行方法并返回数据
		if(parameterDescription.size()==0){
			try {
				Object servletObject = servletBean.getServletClass().newInstance();
				Object result = servletBean.getMethod().invoke(servletObject);
				response.getWriter().write(result.toString());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				response.sendError(500,e.getMessage());
			}
			return;
		}
		//如果存在参数  则新建一个bean方法的参数的集合  该集合为  参数类型==》参数值  且为有序集合  
		Map<Object,Class<?>> parameters = new LinkedHashMap<Object,Class<?>>();
		//获取Path
		Map<Integer, String> pathVariables = servletBean.getPathVariable();
		//建一个路径变量集合
		Map<String,String> pathValues = new LinkedHashMap<String, String>();
		//如果PathVariable不为空
		if(pathVariables.size()>0)
			pathValues= this.getPathValues(pathVariables, resource);
		//分析参数
		Iterator<Parameter> paramIterator = parameterDescription.keySet().iterator();
		//获取PathVariables 值的迭代器
		Iterator<String> pathIterator = pathValues.values().iterator();
		//获取请求头的迭代器
		Enumeration<String> headerIterator = request.getHeaderNames();
		//获取请求参数集合的枚举
		Enumeration<String> requestParameter = request.getParameterNames();
		//获取session attribute 名称迭代器
		Enumeration<String> sessionIterator = request.getSession().getAttributeNames();
		//获取请求参数的值得迭代器  封装调用参数
		while(paramIterator.hasNext()){
			Parameter param =paramIterator.next(); 
			ParameterDescription pade = parameterDescription.get(param);
			if(pade==null){
				//如果注解为空  按循序获取参数
				//判断pathValues的迭代器是否有下一个值，如果有则赋值
				if(pathIterator.hasNext()){
					parameters.put(pathIterator.next(),param.getType());
					log.debug("==================");
					log.debug(parameters.toString());
				}
				continue;
			}else{
				log.debug("annos:"+pade.toString());
				//如果参数名为空
				if(pade.getName()==null){
					//判断参数取值类型
					switch(pade.getType()){
					case ParameterType.CookieValue:
						parameters.put(request.getCookies(),param.getType());//此语句有问题  暂时这样写
						break;
					case ParameterType.PathVariable:
						if(pathIterator.hasNext())
						parameters.put(pathIterator.next(),param.getType());
						break;
					case ParameterType.RequestBody:
						response.sendError(506,"Request url "+resource+" method type GET not support @RequestBody");
						break;
					case ParameterType.RequestHeader:
						if(headerIterator.hasMoreElements())
						parameters.put(request.getHeader(headerIterator.nextElement()),param.getType());
						break;
					case ParameterType.RequestParam:
						if(requestParameter.hasMoreElements())
						parameters.put(request.getParameter(requestParameter.nextElement()),param.getType());
						break;
					case ParameterType.SessionAttributes:
						if(sessionIterator.hasMoreElements())
							parameters.put(request.getSession().getAttribute(sessionIterator.nextElement()),param.getType());
						break;
					default:
						response.sendError(506,"Request url "+resource+" method GET not support parameter type :"+pade.getType());
						break;
					}
					continue;
				}else{
					//判断参数取值类型
					switch(pade.getType()){
					case ParameterType.CookieValue:
						parameters.put( request.getCookies(),param.getType());
						break;
					case ParameterType.PathVariable:
						if(pathValues.get(pade.getName())!=null)
							parameters.put(pathValues.get(pade.getName()),param.getType());
						else
							parameters.put(pade.getValue(),param.getType());
						break;
					case ParameterType.RequestBody:
						response.sendError(506,"Request url "+resource+" method GET not support @RequestBody");
						break;
					case ParameterType.RequestHeader:
						if(request.getHeader(pade.getName())!=null)
							parameters.put(request.getHeader(pade.getName()),param.getType());
						else
							parameters.put( pade.getValue(),param.getType());
						break;
					case ParameterType.RequestParam:
						if(request.getParameter(pade.getName())!=null)
							parameters.put(request.getParameter(pade.getName()),param.getType());
						else
							parameters.put(pade.getValue(),param.getType());
						break;
					case ParameterType.SessionAttributes:
						if(request.getSession().getAttribute(pade.getName())!=null)
							parameters.put(request.getSession().getAttribute(pade.getName()),param.getType());
						else
							parameters.put(pade.getValue(),param.getType());
						break;
					default:
						response.sendError(506,"Request url "+resource+" method GET not support parameter type :"+pade.getType());
						break;
					}
				}
			}
		}
		//判断需要的参数是否与获得的参数匹配
		if(parameterDescription.size()!=parameters.size()){
			log.debug(parameterDescription.toString());
			log.debug(parameters.toString());
			log.debug(pathValues.toString());
			response.sendError(506,"Request url "+resource+" method GET parameter not match");
			return;
		}
		log.debug(parameters.toString());
		//重组准备参数 需要准备参数类型数组和参数数组
		Class<?>[] parameterTypes = new Class<?>[parameters.size()];
		Object[] parameter = new Object[parameters.size()];
		int i = 0;
		Iterator<Entry<Object, Class<?>>> paraEn = parameters.entrySet().iterator();
		while(paraEn.hasNext()){
			Entry<Object, Class<?>> entry = paraEn.next();
			parameterTypes[i] = entry.getValue();
			parameter[i] = entry.getKey();
			i++;
		}
		try {
			Object servletObject = servletBean.getServletClass().newInstance();
			Object result = servletBean.getMethod().invoke(servletObject, parameter);
			response.getWriter().write(result.toString());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			response.sendError(500,e.getMessage());
		}
		
			
			
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,String resource)throws ServletException, IOException {
	    
	}
	protected void doPut(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,String resource)throws ServletException, IOException {
	    
	}
	protected void doDelete(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,String resource)throws ServletException, IOException {
	    
	}

}
