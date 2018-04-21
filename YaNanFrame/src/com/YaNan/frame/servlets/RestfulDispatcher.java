package com.YaNan.frame.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.ParameterDescription.ParameterType;;

/**
 * 	Restful 核心调配器
 * @version 1.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class RestfulDispatcher extends HttpServlet implements ServletDispatcher,ServletContextListener{
	private static final long serialVersionUID = -1089658849875241044L;
	public static final String DEFAULT_METHOD_PARAM = "_method";
	private static final String ActionStyle = "RESTFUL_STYLE";
	// 日志类，用于输出日志
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, RestfulDispatcher.class);
	protected boolean showServerInfo = true;
	protected Servlet servlet;
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Class<?>[] annotations = {com.YaNan.frame.servlets.annotations.RequestMapping.class};
	
	private static final ServletMappingBuilder servletMappingBuilder=new ServletBeanBuilder();
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
		ServletBean servletBean = ServletMapping.getInstance().getServlet(ActionStyle,urlMapping);
		//判断ServletBean是否存在  
		if(servletBean==null){
			log.warn("servlet handle not found! request url :"+urlMapping);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
			}
		//如果ServletBean中没有参数  直接执行方法并返回数据
		if(servletBean.getParameters().size()==0){
			try {
				Object servletObject = servletBean.getServletClass().newInstance();
				Object result = servletBean.getMethod().invoke(servletObject);
				response.getWriter().write(result.toString());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
			}
			return;
		}
		//获取PathVariable对应的参数
		Map<String, String> pathParameter =  new LinkedHashMap<String, String>();
		if(servletBean.getPathVariable().size()>0)
			pathParameter= this.getPathValues(servletBean.getPathVariable(), path);
		//判断ServletBean的类型并分发给对应的处理方式  此处之所以没有独立出来是因为 switch(int)效率非常高   可以达到1000W*50/s的转换率
		try {
			switch(servletBean.getRequestMethod()){
			  	case REQUEST_METHOD.DELETE:
			    	doDelete(request, response, servletBean,pathParameter);
			    	break;
			    case REQUEST_METHOD.GET:
			    	doGet(request, response, servletBean,pathParameter);
			    	break;
			    case REQUEST_METHOD.POST:
			    	doPost(request, response, servletBean,pathParameter);
			    	break;
			    case REQUEST_METHOD.PUT:
			    	doPut(request, response, servletBean,pathParameter);
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
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				break;
				}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ParseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getCause().getMessage());
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
        String servletPath;
        String pathInfo;
        if (request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null) {
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
     * 此方法用于提取 get、post(content type:application/x-www-form-urlencoded)的数据
     * @param request
     * @param response
     * @param servletBean
     * @param pathParameter
     * @return
     * @throws ParseException
     * @throws IOException
     */
    protected List<Object> urlencodedParameterBind(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,Map<String, String> pathParameter) throws ParseException, IOException{
    	//获得servletBean中的参数  该集合类型为  参数 ==》参数注解
    			Map<Parameter, ParameterDescription> parameterDescription = servletBean.getParameters();
    			//分析参数
    			Iterator<Entry<Parameter, ParameterDescription>> paramIterator = parameterDescription.entrySet().iterator();
    			//获取PathVariables 值的迭代器
    			Iterator<String> pathIterator = pathParameter.values().iterator();
    			//获取请求头的迭代器
    			Enumeration<String> headerIterator = request.getHeaderNames();
    			//获取请求参数集合的枚举
    			Iterator<Entry<String, String[]>> entrySetiterator =request.getParameterMap().entrySet().iterator();
    			//获取session attribute 名称迭代器
    			Enumeration<String> sessionIterator = request.getSession().getAttributeNames();
    			//如果存在参数  则新建一个servletBean方法的参数的集合  该集合为  参数类型==》参数值  且为有序集合  
    			List<Object> parameters = new LinkedList<Object>();
    			//获取请求参数的值得迭代器  封装调用参数
    				while(paramIterator.hasNext()){ 
    					Entry<Parameter, ParameterDescription> paramEntry =paramIterator.next(); 
    					log.debug(isBaseType(paramEntry.getKey().getType())+"");
    					//判断参数是否为基本类型
    					if(isBaseType(paramEntry.getKey().getType())){
    						//处理基本类型
    						if(paramEntry.getValue()==null){
    							//如果注解为空  按循序获取参数  只支持 pathVariable 与 requestParameter 如果没有的话会造成HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    							if(pathIterator.hasNext())//判断pathValues的迭代器是否有下一个值，如果有则赋值
    								parameters.add(parseBaseType(paramEntry.getKey().getType(),pathIterator.next(),null));
    							else if(entrySetiterator.hasNext()){//判断requestParameter参数中是否有该参数
    								Entry<String, String[]> entry = entrySetiterator.next();
    								parameters.add(parseBaseType(paramEntry.getKey().getType(),entry.getValue().length==1&&entry.getValue()[0].equals("")?new String[]{entry.getKey()}:entry.getValue(),null));
    							}
    							continue;
    						}else{
    							//如果参数名为空
    							if(paramEntry.getValue().getName()==null){
    								//判断参数取值类型
    								switch(paramEntry.getValue().getType()){
    									case ParameterType.CookieValue:
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),request.getCookies()[0].getValue(),null));//此语句存在bug
    										break;
    									case ParameterType.PathVariable:
    										if(pathIterator.hasNext())
    											parameters.add(parseBaseType(paramEntry.getKey().getType(),pathIterator.next(),null));
    										break;
    									case ParameterType.RequestBody:
    										response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method type GET not support @RequestBody");
    										break;
    									case ParameterType.RequestHeader:
    										if(headerIterator.hasMoreElements())
    											parameters.add(parseBaseType(paramEntry.getKey().getType(),request.getHeader(headerIterator.nextElement()),null));
    										break;
    									case ParameterType.RequestParam:
    										if(entrySetiterator.hasNext()){
    											Entry<String, String[]> entry = entrySetiterator.next();
    											parameters.add(parseBaseType(paramEntry.getKey().getType(),entry.getValue().length==1&&entry.getValue()[0].equals("")?new String[]{entry.getKey()}:entry.getValue(),null));
    										}
    										break;
    									case ParameterType.SessionAttributes:
    										if(sessionIterator.hasMoreElements())
    											parameters.add(parseSessionAttribute(paramEntry.getKey().getType(),request.getSession().getAttribute(sessionIterator.nextElement()),null));
    										break;
    									default:
    										response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method GET not support parameter type :"+paramEntry.getValue().getType());
    										break;
    								}
    								continue;
    							}else{
    								//判断参数取值类型
    								switch(paramEntry.getValue().getType()){
    								case ParameterType.CookieValue:
    									for(Cookie cookie : request.getCookies()){
    										if(cookie.getName().equals(paramEntry.getValue().getName()))
    											parameters.add(parseBaseType(paramEntry.getKey().getType(),cookie.getValue(),null));
    									}
    									break;
    								case ParameterType.PathVariable:
    									if(pathParameter.get(paramEntry.getValue().getName())!=null)
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),pathParameter.get(paramEntry.getValue().getName()),null));
    									else
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),paramEntry.getValue().getValue(),null));
    									break;
    								case ParameterType.RequestBody:
    									response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method GET not support @RequestBody");
    									break;
    								case ParameterType.RequestHeader:
    									if(request.getHeader(paramEntry.getValue().getName())!=null)
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),request.getHeader(paramEntry.getValue().getName()),null));
    									else
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),paramEntry.getValue().getValue(),null));
    									break;
    								case ParameterType.RequestParam:
    									if(request.getParameter(paramEntry.getValue().getName())!=null)
    										parameters.add(paramEntry.getKey().getType().isArray()?
    												parseBaseType(paramEntry.getKey().getType(),request.getParameter(paramEntry.getValue().getName()),null):
    													parseBaseType(paramEntry.getKey().getType(),request.getParameterValues(paramEntry.getValue().getName()),null));
    									else
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),paramEntry.getValue().getValue(),null));
    									break;
    								case ParameterType.SessionAttributes:
    									if(request.getSession().getAttribute(paramEntry.getValue().getName())!=null)
    										parameters.add(parseSessionAttribute(paramEntry.getKey().getType(),request.getSession().getAttribute(paramEntry.getValue().getName()),null));
    									else
    										parameters.add(parseBaseType(paramEntry.getKey().getType(),paramEntry.getValue().getValue(),null));
    									break;
    								default:
    									response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method GET not support parameter type :"+paramEntry.getValue().getType());
    									break;
    								}
    							}
    						}
    					}else{
    						//处理特殊参数 如  pojo  session  cookie  model  request response write header
    						if(paramEntry.getKey().getType().equals(HttpServletRequest.class))//request
    							parameters.add(request);
    						else if(paramEntry.getKey().getType().equals(HttpServletResponse.class))//response
    							parameters.add(response);
    						else if(paramEntry.getKey().getType().equals(PrintWriter.class)||paramEntry.getKey().getType().equals(Writer.class))//writer
    							parameters.add(response.getWriter());
    						else if(paramEntry.getKey().getType().equals(InputStream.class))//inputStream
    							parameters.add(request.getInputStream());
    						else if(paramEntry.getKey().getType().equals(OutputStream.class))//OutputStream
    							parameters.add(response.getOutputStream());
    						else if(paramEntry.getKey().getType().equals(HttpSession.class))//session
    							parameters.add(request.getSession());
    						else if(paramEntry.getKey().getType().equals(Cookie[].class))//cookies
    							parameters.add(request.getCookies());
    						else if(paramEntry.getKey().getType().equals(Locale.class))//Locale
    							parameters.add(request.getLocale());
    						else{
    							
    						}
    					}
    				}
    				return parameters;
    }
	/**
	* Servlet get方法
	* 支持注解 PathVariable RequestParam赋值
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,Map<String, String> pathParameter) throws ParseException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		   
		   List<Object> parameters = this.urlencodedParameterBind(request, response, servletBean, pathParameter);
			//判断需要的参数是否与获得的参数匹配
			if(servletBean.getParameters().size()!=parameters.size()){
				log.debug(servletBean.getParameters().toString());
				log.debug(parameters.toString());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method GET parameter not match");
				return;
			}
			log.debug(parameters.toString());
			//重组准备参数 需要准备参数类型数组和参数数组
			Object[] parameter = new Object[parameters.size()];
			int i = 0;
			Iterator<Object> paraEn = parameters.iterator();
			while(paraEn.hasNext()){
				parameter[i] = paraEn.next();
				i++;
			}
			Object servletObject = servletBean.getServletClass().newInstance();
			Object result = servletBean.getMethod().invoke(servletObject, parameter);
			response.getWriter().write(result.toString());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,Map<String, String> pathParameter)throws ServletException, IOException, ParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Object> parameters = this.urlencodedParameterBind(request, response, servletBean, pathParameter);
		//判断需要的参数是否与获得的参数匹配
		if(servletBean.getParameters().size()!=parameters.size()){
			log.debug(servletBean.getParameters().toString());
			log.debug(parameters.toString());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Request url "+pathParameter+" method GET parameter not match");
			return;
		}
		log.debug(parameters.toString());
		//重组准备参数 需要准备参数类型数组和参数数组
		Object[] parameter = new Object[parameters.size()];
		int i = 0;
		Iterator<Object> paraEn = parameters.iterator();
		while(paraEn.hasNext()){
			parameter[i] = paraEn.next();
			i++;
		}
		Object servletObject = servletBean.getServletClass().newInstance();
		Object result = servletBean.getMethod().invoke(servletObject, parameter);
		response.getWriter().write(result.toString());
	}
	protected void doPut(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,Map<String, String> pathParameter)throws ServletException, IOException {
	    
	}
	protected void doDelete(HttpServletRequest request, HttpServletResponse response,ServletBean servletBean,Map<String, String> pathParameter)throws ServletException, IOException {
	    
	}
	
	/**
	 * 判断类是否为可支持的基本类型
	 * @param clzz
	 * @return
	 */
	protected static boolean isBaseType(Class<?> clzz){
		if(clzz.equals(String.class))  
			return true;
		if(clzz.equals(boolean.class))  
			return true;
		if(clzz.equals(int.class))  
			return true;
		if(clzz.equals(float.class))  
			return true;
		if(clzz.equals(byte.class))  
			return true;
		if(clzz.equals(short.class))  
			return true;
		if(clzz.equals(long.class))  
			return true;
		if(clzz.equals(double.class))  
			return true;
		if(clzz.equals(char.class))  
			return true;
		//八个基本数据类型的包装类型
		if(clzz.equals(Byte.class))  
			return true;
		if(clzz.equals(Short.class))  
			return true;
		if(clzz.equals(Integer.class))  
			return true;
		if(clzz.equals(Long.class))  
			return true;
		if(clzz.equals(Float.class))  
			return true;
		if(clzz.equals(Double.class))  
			return true;
		if(clzz.equals(Boolean.class))  
			return true;
		if(clzz.equals(Character.class))  
			return true;
		//日期
		if(clzz.equals(Date.class))  
			return true;
		
		//以上所有类型的数组类型
		if(clzz.equals(String[].class))  
			return true;
		if(clzz.equals(boolean[].class))  
			return true;
		if(clzz.equals(int[].class))  
			return true;
		if(clzz.equals(float[].class))  
			return true;
		if(clzz.equals(byte[].class))  
			return true;
		if(clzz.equals(short[].class))  
			return true;
		if(clzz.equals(long[].class))  
			return true;
		if(clzz.equals(double[].class))  
			return true;
		if(clzz.equals(char[].class))  
			return true;
		//八个基本数据类型的包装类型
		if(clzz.equals(Short[].class))  
			return true;
		if(clzz.equals(Integer[].class))  
			return true;
		if(clzz.equals(Long[].class))  
			return true;
		if(clzz.equals(Float[].class))  
			return true;
		if(clzz.equals(Double[].class))  
			return true;
		if(clzz.equals(Boolean[].class))  
			return true;
		if(clzz.equals(Character[].class))  
			return true;
		//日期
		if(clzz.equals(Date[].class))  
			return true;
		
		return false;
	}
	/**
	 * 将字符类型转换为目标类型
	 * @param clzz
	 * @return
	 * @throws ParseException 
	 */
	protected static Object parseBaseType(Class<?> clzz,String[] arg,String format) throws ParseException{
		if(!clzz.isArray())
			return parseBaseType(clzz, arg[0], format);
		//匹配时应该考虑优先级 比如常用的String int boolean应该放在前面  其实 包装类型应该分开
		if(clzz.equals(String[].class))
			return arg;
		//8个基本数据类型
		if(clzz.equals(int[].class)||clzz.equals(Integer[].class)){
			int[] args = new int[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] =Integer.parseInt(arg[i]);
			return args;
		}
		if(clzz.equals(boolean[].class)||clzz.equals(Boolean[].class)){
			boolean[] args = new boolean[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = Boolean.parseBoolean(arg[i]);
			return args;
		}
		
		if(clzz.equals(float[].class)||clzz.equals(Float[].class)){
			float[] args = new float[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = Float.parseFloat(arg[i]);
			return args;
		}
		
		if(clzz.equals(short[].class)||clzz.equals(Short[].class)){
			short[] args = new short[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = Short.parseShort(arg[i]);
			return args;
		}
		
		if(clzz.equals(long[].class)||clzz.equals(Long[].class)){
			long[] args = new long[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = Long.parseLong(arg[i]);
			return args;
		}
		
		if(clzz.equals(double[].class)||clzz.equals(Double[].class)){
			double[] args = new double[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = Double.parseDouble(arg[i]);
			return args;
		}
		//日期
		if(clzz.equals(Date.class)){
			Date[] args = new Date[arg.length];
			for(int i = 0;i<args.length;i++)
				args[i] = format==null?
						DATE_FORMAT.parse(arg[i]):
							new SimpleDateFormat(format).parse(arg[i]);
			return args;
		}
		return arg;
	}
	/**
	 * 将字符类型转换为目标类型
	 * @param clzz
	 * @return
	 * @throws ParseException 
	 */
	protected static Object parseBaseType(Class<?> clzz,String arg,String format) throws ParseException{
		//匹配时应该考虑优先级 比如常用的String int boolean应该放在前面  其实 包装类型应该分开
		if(clzz.equals(String.class))
			return arg;
		//8个基本数据类型
		if(clzz.equals(int.class)||clzz.equals(Integer.class))  
			return Integer.parseInt(arg);
		if(clzz.equals(boolean.class)||clzz.equals(Boolean.class))  
			return Boolean.parseBoolean(arg);
		if(clzz.equals(float.class)||clzz.equals(Float.class))  
			return Float.parseFloat(arg);
		if(clzz.equals(short.class)||clzz.equals(Short.class))  
			return Short.parseShort(arg);
		if(clzz.equals(long.class)||clzz.equals(Long.class))  
			return Long.parseLong(arg);
		if(clzz.equals(double.class)||clzz.equals(Double.class))  
			return Double.parseDouble(arg);
		if(clzz.equals(char.class)||clzz.equals(Character.class))  
			return arg.charAt(0);
		if(clzz.equals(char[].class)||clzz.equals(Character[].class))
			return arg.toCharArray();
		if(clzz.equals(byte.class)||clzz.equals(Byte.class))  
			return Byte.parseByte(arg);
		//日期
		if(clzz.equals(Date.class))
			return format==null?
					DATE_FORMAT.parse(arg):
						new SimpleDateFormat(format).parse(arg);
		return arg;
		}
	/**
	 * 将sessionAttribute 转化为目标类型
	 * @param clzz
	 * @param arg
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	protected static Object parseSessionAttribute(Class<?> clzz,Object arg,String format) throws ParseException{
		if(isBaseType(clzz))
			return parseBaseType(clzz, arg.toString(), format);
		return arg;
	}
	@Override
	public ServletMappingBuilder getBuilder() {
		return servletMappingBuilder;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> Class<T>[] getDispatcherAnnotation(){
		return (Class<T>[]) annotations;
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ServletBuilder.getInstance();
		ServletMapping servletManager = ServletMapping
				.getInstance();
		Map<String, ServletBean> servletMapping = servletManager.getServletMappingByStype("RESTFUL_STYLE");
		log.debug("RESTFUL Servlet num:"+servletMapping.size());
		Iterator<Entry<String, ServletBean>> iterator = servletMapping.entrySet().iterator();
		log.debug("==============Traverse the servlet collection===========");
		while (iterator.hasNext()) {
			Entry<String, ServletBean> key = iterator.next();
			ServletBean servletBean = key.getValue();
			log.debug("---------------------------------------------------------");
			log.debug("url mapping:" + key.getKey() + ",servlet method:" + servletBean.getMethod()
					+ ",servlet type:" +servletBean.getType());
			log.debug("---------------------------------------------------------");

		}
	}
}