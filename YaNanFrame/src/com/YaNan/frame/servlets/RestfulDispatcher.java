package com.YaNan.frame.servlets;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.annotations.restful.ParameterType;
import com.YaNan.frame.servlets.exception.ServletExceptionHandler;
import com.YaNan.frame.servlets.parameter.ParameterHandler;
import com.YaNan.frame.servlets.response.ResponseHandler;
import com.YaNan.frame.servlets.response.annotations.ResponseType;

/**
 * Restful 核心调配器
 * 
 * @version 1.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class RestfulDispatcher extends HttpServlet implements ServletDispatcher, ServletContextListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_METHOD_PARAM = "_method";
	private static final String ActionStyle = "RESTFUL_STYLE";
	// 日志类，用于输出日志
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, RestfulDispatcher.class);
	protected boolean showServerInfo = true;
	protected Servlet servlet;
	// 注解类型
	private static final Class<?>[] annotations = { com.YaNan.frame.servlets.annotations.RequestMapping.class,
			com.YaNan.frame.servlets.annotations.GetMapping.class,
			com.YaNan.frame.servlets.annotations.PutMapping.class,
			com.YaNan.frame.servlets.annotations.PostMapping.class,
			com.YaNan.frame.servlets.annotations.DeleteMapping.class };
	private static final ServletMappingBuilder servletMappingBuilder = new ServletBeanBuilder();

	/**
	 * 此方法用于处理restful的整个业务逻辑，包括一下循序 1、获取真实的servletBean（url需要进行重组，否则不会识别 get post
	 * delete put等请求） 2、获取封装参数
	 * （从servletBean.getParameters()方法获取需要的参数，遍历每个参数，通过其注解或属性从PlugsFactory
	 * 获取对应参数处理器，获取参数,并封装，拦截器拦截的是ParameterHandler的getParameter方法，通过对其传入第一个参数
	 * 参数获取到servlet类中对应方法的参数，再获取参数的输入 Constraint.class的注解，对该注解输出结果进行验证并作出对 应响应）
	 * 3、调用方法（将第二步获取到的参数进行校验，没问题之后通过反射调用其方法） 4、处理响应
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获得相对路径

		/**
		 * 这里应该组装Servlet 对servelt进行组装，判断请求方式 post情况下 包含 _method = delete put
		 * 则为对应的方法，否则为post @ 符号与数字组合表示url的类型
		 */
		// 获取ServletBean映射实例以及获取ServletBean
		ServletBean servletBean = getServletBean(request);
		// 判断ServletBean是否存在
		if (servletBean == null) {
			log.warn("servlet handle not found! url mapping :" + getUrlMapping(request));
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		try {
			Map<String, ?> model = null;// 此变量目前不适用
			// 获取Servlet类代理对象的实例
			Object proxyObject = PlugsFactory.getPlugsInstanceNew(servletBean.getServletClass());
			// 获取参数,参数验证通过拦截里面的方法完成
			List<Object> parameters = null;
			if (servletBean.getParameters() != null)
				parameters = this.urlencodedParameterBind(request, response, servletBean, model);
			// 执行完之后，判断是否已将response提交，如果为提交，则判断返回结果
			if (!response.isCommitted()) {
				Object handlerResult = invokeProxyMethhod(request, response, servletBean, proxyObject, parameters);
				// 执行完之后，判断是否已将response提交，如果为提交，则判断返回结果
				if (!response.isCommitted()) {
					// 判断返回结果是否为null,不为null则进行处理
					// 判断是否有ResponseType等注解
					List<Annotation> resultAnnotations = servletBean.getMethodAnnotation(ResponseType.class);
					// 若果没有获得ResponseType的注解，则根据返回结果寻找response handler
					if (resultAnnotations == null || resultAnnotations.isEmpty()) {
						if (handlerResult == null) {
							response.setStatus(200);
							response.getWriter().flush();
							response.getWriter().close();

						} else {
							Class<?> handlerResultType = handlerResult.getClass();
							ResponseHandler responseHandler = PlugsFactory.getPlugsInstanceByAttributeStrict(
									ResponseHandler.class, handlerResultType.getName());
							// 如果handler不为空，则调用handler，否则直接输出
							if (responseHandler != null) {
								responseHandler.render(request, response, handlerResult, null, servletBean);
							} else {
								response.getWriter().write(handlerResult.toString());
								response.getWriter().flush();
								response.getWriter().close();
							}
						}
					} else {
						// 获取第一个ResponseType的注解
						Annotation responseAnnotation = resultAnnotations.get(0);
						Class<?> annotationType = responseAnnotation.annotationType();
						ResponseHandler responseHandler = PlugsFactory
								.getPlugsInstanceByAttributeStrict(ResponseHandler.class, annotationType.getName());
						if (responseHandler == null)
							throw new ServletException(
									"could not found response handler for response type " + annotationType.getName());
						responseHandler.render(request, response, handlerResult, responseAnnotation, servletBean);
					}
				}
			}
			proxyObject = null;
			parameters = null;
		} catch (Exception e) {
			e.printStackTrace();
			ServletExceptionHandler servletExceptionHandler = PlugsFactory
					.getPlugsInstance(ServletExceptionHandler.class);
			servletExceptionHandler.exception(e, request, response);
			log.error(e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Return the relative path associated with this servlet.
	 *
	 * @param request
	 *            The servlet request we are processing
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
	 * 此方法用于提取 get、post(content type:application/x-www-form-urlencoded)的数据
	 * 
	 * @param request
	 * @param response
	 * @param servletBean
	 * @param pathParameter
	 * @param model
	 * @return
	 * @throws Exception
	 */
	protected List<Object> urlencodedParameterBind(HttpServletRequest request, HttpServletResponse response,
			ServletBean servletBean, Map<String, ?> model) throws Exception {
		// 创建一个ParameterHandler的集合
		ParameterHandlerCache parameterHandlerCache = new ParameterHandlerCache(request, response, servletBean);
		// 获得servletBean中的参数 该集合类型为 参数 ==》注解类型 ==》注解
		Iterator<Entry<Parameter, Map<Class<Annotation>, List<Annotation>>>> paramIterator = servletBean.getParameters()
				.entrySet().iterator();
		// 如果存在参数 则新建一个servletBean方法的参数的集合
		List<Object> parameters = new LinkedList<Object>();
		// 获取请求参数的值得迭代器 封装调用参数
		while (paramIterator.hasNext()) {
			// 如果赋值过程强行提交了数据，则返回一个null
			if (response.isCommitted())
				return null;
			// 获取参数和其描述
			Entry<Parameter, Map<Class<Annotation>, List<Annotation>>> paramEntry = paramIterator.next();
			// 获取其中的para
			if (paramEntry.getValue() != null) {
				List<Annotation> annos = paramEntry.getValue().get(ParameterType.class);
				if (annos != null && annos.size() != 0) {
					Annotation parameterAnnotation = annos.get(0);
					ParameterHandler parameterHandler = parameterHandlerCache.getParameterHandler(parameterAnnotation);
					parameters.add(parameterHandler == null ? null
							: parameterHandler.getParameter(paramEntry.getKey(), parameterAnnotation));
					continue;
				}
			}
			ParameterHandler parameterHandler = parameterHandlerCache
					.getParameterHandler(paramEntry.getKey().getType());
			parameters.add(parameterHandler == null ? null : parameterHandler.getParameter(paramEntry.getKey()));
		}
		return parameters;
	}

	/**
	 * 支持注解 PathVariable RequestParam赋值
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public Object invokeProxyMethhod(HttpServletRequest request, HttpServletResponse response,
			ServletBean servletBean, Object proxyObject, List<Object> parameters) throws Exception {
		Object[] parameter = null;
		// 判断需要的参数是否与获得的参数匹配
		if (servletBean.getParameters() != null) {
			if (parameters == null || servletBean.getParameters().size() != parameters.size()) {
				throw new ServletException(
						"url mapping " + this.getUrlMapping(request) + " method parameter not match");
			} else {// 重组准备参数 需要准备参数类型数组和参数数组
				parameter = new Object[parameters.size()];
				int i = 0;
				Iterator<Object> paraEn = parameters.iterator();
				while (paraEn.hasNext()) {
					parameter[i++] = paraEn.next();
				}
			}
		}
		return servletBean.getMethod().invoke(proxyObject, parameter);
	}

	@Override
	public ServletMappingBuilder getBuilder() {
		return servletMappingBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> Class<T>[] getDispatcherAnnotation() {
		return (Class<T>[]) annotations;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ServletBuilder.getInstance();
		ServletMapping servletManager = ServletMapping.getInstance();
		Map<String, ServletBean> servletMapping = servletManager.getServletMappingByStype("RESTFUL_STYLE");
		if (servletMapping == null) {
			log.debug("RESTFUL Servlet num:0");
			return;
		}
		log.debug("RESTFUL Servlet num:" + servletMapping.size());
		Iterator<Entry<String, ServletBean>> iterator = servletMapping.entrySet().iterator();
		log.debug("==============Traverse the servlet collection===========");
		while (iterator.hasNext()) {
			Entry<String, ServletBean> key = iterator.next();
			ServletBean servletBean = key.getValue();
			log.debug("---------------------------------------------------------");
			log.debug("url mapping:" + key.getKey() + ",servlet method:" + servletBean.getMethod() + ",servlet type:"
					+ servletBean.getType());
			if (servletBean.getParameters() != null) {
				Iterator<Entry<Parameter, Map<Class<Annotation>, List<Annotation>>>> iterator1 = servletBean
						.getParameters().entrySet().iterator();
				while (iterator1.hasNext()) {
					Entry<Parameter, Map<Class<Annotation>, List<Annotation>>> e = iterator1.next();
					log.debug(e.getKey().toString());
					log.debug(e.getValue() + "");
				}
			}
			log.debug("------------------------------------------------");
		}
	}

	@Override
	public ServletBean getServletBean(HttpServletRequest request) throws ServletException {
		return ServletMapping.getInstance().getServlet(ActionStyle, getUrlMapping(request));
	}

	public ServletBean getServletBean(String urlMapping) {
		return ServletMapping.getInstance().getServlet(ActionStyle, urlMapping);
	}

	public String getUrlMapping(HttpServletRequest request) throws ServletException {
		String urlMapping = URLSupport.getRelativePath(request);
		String method = request.getMethod();
		if (method.equals("GET")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.GET).toString();
		} else if (method.equals("POST")) {
			method = request.getParameter(DEFAULT_METHOD_PARAM);
			if (method == null) {
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.POST).toString();
			} else if (method.toUpperCase().equals("DELETE")) {
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.DELETE).toString();
			} else if (method.toUpperCase().equals("PUT")) {
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.PUT).toString();
			} else if (method.toUpperCase().equals("POST")) {
				urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.POST).toString();
			} else {
				throw new ServletException("servlet handle not process this request method ! request url :" + urlMapping
						+ ",method:" + method);
			}
			// 组装post delete put 请求
		} else if (method.equals("DELETE")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.DELETE).toString();
		} else if (method.equals("PUT")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.PUT).toString();
		} else if (method.equals("HEAD")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.HEAD).toString();
		} else if (method.equals("OPTIONS")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.OPTIONS).toString();
		} else if (method.equals("TRACE")) {
			urlMapping = new StringBuilder(urlMapping).append("@").append(REQUEST_METHOD.TRACE).toString();
		} else {
			throw new ServletException("servlet handle not process this request method ! request url :" + urlMapping
					+ ",method:" + method);
		}
		return urlMapping;
	}
}