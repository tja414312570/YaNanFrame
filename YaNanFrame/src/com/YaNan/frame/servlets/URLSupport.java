package com.YaNan.frame.servlets;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class URLSupport{

	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/
	 * 
	 * @param request
	 * @return
	 */
	public static String getRootURL(ServletRequest request) {
		String cUrl = ResourceCache.getResource(request.getScheme(),request.getServerName(),request.getServerPort()+"");
		if(cUrl==null){
			StringBuilder sb  = new StringBuilder(request.getScheme()).append("://").append(request.getServerName());
			if(request.getServerPort()!=80&&request.getServerPort()!=443)
				sb.append(":").append(request.getServerPort());
			cUrl = sb.toString();
		}
		ResourceCache.putResource(cUrl,request.getScheme(),request.getServerName(),request.getServerPort()+"");
		return cUrl;
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/
	 * 
	 * @param request
	 * @return
	 */
	public static String getContextURL(ServletRequest request) {
		String rootPath = getRootURL(request);
		String cUrl = ResourceCache.getResource(rootPath,((HttpServletRequest) request).getContextPath());
		if(cUrl==null){
			cUrl = new StringBuilder(getRootURL(request)).append(((HttpServletRequest) request).getContextPath()).append("/").toString();
			ResourceCache.putResource(cUrl,rootPath,((HttpServletRequest) request).getContextPath());
		}
		return cUrl;
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ---->Http://YaNan.photo/YaNan/r.do
	 * 
	 * @param request
	 * @return
	 */
	public static String getURL(HttpServletRequest request) {
		String cUrl = ResourceCache.getResource(request.getRequestURL().toString(),request.getServerPort()+"");
		if(cUrl==null){
			cUrl= request.getRequestURL().toString();
			int port = request.getServerPort();
			if(port==80&&cUrl.indexOf(":80")>=0)
				cUrl = new StringBuilder(cUrl.substring(0,cUrl.indexOf(":80"))).append(cUrl.indexOf(":80")+3).toString();
			if(port==443&&cUrl.indexOf(":443")>=0)
				cUrl = new StringBuilder(cUrl.substring(0,cUrl.indexOf(":443"))).append(cUrl.indexOf(":443")+4).toString();
			ResourceCache.putResource(cUrl,request.getRequestURL().toString(),request.getServerPort()+"");
		}
		return cUrl;
		
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> r
	 * 
	 * @param request
	 * @return
	 */
	public static String getServlet(HttpServletRequest request) {
		String url = ResourceCache.getResource(getURL(request),"servlet");
		if(url==null){
			url = getURL(request);
			url = new StringBuilder(url).substring(url.lastIndexOf("/")+1);
			ResourceCache.putResource(url,getURL(request),"servlet");
		}
		return url;
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> /YaNan/r.do
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request) {
		String url = ResourceCache.getResource(getURL(request),"RequestPath"); ;
		if(url==null){
			url = getURL(request);
			url = new StringBuilder().substring(url.lastIndexOf(getContextURL(request)));
			ResourceCache.putResource(url,getURL(request),"RequestPath");
		}
		return url;
	}

	/**
	 * Http://YaNan.photo/YaNan/r.do ----> /YaNan
	 * 
	 * @param request
	 * @return
	 */
	public static String getNameSpace(HttpServletRequest request) {
		String url = getURL(request);
		String cUrl = getContextURL(request);
		if (url.equals(cUrl))
			return "/";
		if(!url.substring(cUrl.length(), url.length()).contains("/"))
			return "/";
		return url.substring(cUrl.length()-1,
				url.lastIndexOf("/")+1);
	}
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
	public static Object getRequestFile(ServletRequest request) {
		String url = getURL((HttpServletRequest) request);
		if (url.length() == url.lastIndexOf("/"))
			return "";
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	public static String getContextIP(ServletRequest request) {
		System.out.println("localAddr:"+request.getLocalAddr());
		System.out.println("LocalName:"+request.getLocalName());
		System.out.println("LocalPort:"+request.getLocalPort());
		System.out.println("Protocol:"+request.getProtocol());
		System.out.println("RemoteAddr:"+request.getRemoteAddr());
		System.out.println("RemoteHost:"+request.getRemoteHost());
		System.out.println("RemotePort:"+request.getRemotePort());
		System.out.println("Scheme:"+request.getScheme());
		System.out.println("ServerName:"+request.getServerName());
		System.out.println("ServerPort:"+request.getServerPort());
		
		return null;
	}

	public static String getRequestFileType(HttpServletRequest request) {
		String url = getURL(request);
		String fileType = ResourceCache.getResource(url,"FileType");
		if(fileType==null){
			int fileMarkIndex =url.indexOf(".");  
			fileType =fileMarkIndex>0?new StringBuilder(url).substring(fileMarkIndex+1):"" ;
			ResourceCache.putResource(fileType,url,"FileType");
		}
		return fileType;
	}
}
