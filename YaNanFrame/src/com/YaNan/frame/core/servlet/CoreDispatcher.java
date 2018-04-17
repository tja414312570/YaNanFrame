package com.YaNan.frame.core.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

/**
 * 	CoreDispatcher 核心调配器
 * @version 2.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
@WebServlet("/")
public class CoreDispatcher extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1089658849875241044L;
	protected transient WebResourceRoot resources = null;
	// 日志类，用于输出日志
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, CoreDispatcher.class);
	protected boolean showServerInfo = true;
	protected Servlet servlet;
	
	
	@Override
    public void init() throws ServletException {
		//获取servletDispatcher实例列表
		List<Servlet> servlets = PlugsFactory.getPlugsInstanceList(Servlet.class);
		//对所有servlet进行
		for(int i = 0 ;i<servlets.size();i++){
			servlets.get(i).init(getServletConfig());
			log.debug(servlets.get(i).toString());
		}
	 // Load the web resources
        resources = (WebResourceRoot) getServletContext().getAttribute(
                Globals.RESOURCES_ATTR);

        if (resources == null) {
            throw new UnavailableException("No resources");
        }

        if (getServletConfig().getInitParameter("showServerInfo") != null) {
            showServerInfo = Boolean.parseBoolean(getServletConfig().getInitParameter("showServerInfo"));
        }
    }
	
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
	protected void service(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
		//获得资源相对路径
		String path = getRelativePath(req, true);
		DefaultServletMapping servletMap = DefaultServletMapping.getInstance();
		/**
		 * 通过模糊的方式查询servlet，此种方式目的在与获取Servlet类型，因此有以下缺点
		 * 1、不能存在同一种URL映射同时存在多种风格
		 * 2、模糊匹配的Servlet的类型为restful类型时可能通过getServlet()获取并不存在
		 * 
		 */
		ServletBean servletBean = servletMap.getAsServlet(path);
		String resourceType = "";
		// 如果ServletBean存在
		if(servletBean!=null){
			resourceType = servletBean.getStyle();
		}else{//获取文件后缀，主要是调派jsp 所以Jsp的优先级高于Dispatcher的优先级
			WebResource resource = resources.getResource(path);
			String name = resource.getName();
			int pointIndex = name.indexOf(".");
			if(pointIndex>=0)
				resourceType = name.substring(pointIndex);
		}
		log.debug(path);
		log.debug(resourceType);
		Servlet servlet = PlugsFactory.getPlugsInstanceByAttribute(Servlet.class, resourceType);
		log.debug(servlet.toString());
		servlet.service(req, resp);
	    }
	/**
     * Return the relative path associated with this servlet.
     *
     * @param request The servlet request we are processing
     * @return the relative path
     */
    public static  String getRelativePath(HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    public static String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
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

}
