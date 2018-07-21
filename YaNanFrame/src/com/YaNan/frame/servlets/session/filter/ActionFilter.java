package com.YaNan.frame.servlets.session.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.ServletDispatcher;
import com.YaNan.frame.servlets.ServletMapping;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.servlets.session.annotation.Authentication;
import com.YaNan.frame.servlets.session.annotation.AuthenticationGroups;
import com.YaNan.frame.servlets.session.annotation.Chain;

@WebFilter(filterName = "actionFilter", urlPatterns = "/*")
public class ActionFilter implements Filter{
	Map<ServletBean,Authentication[]> authPools ;
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Token token = Token.getToken((HttpServletRequest) request);
		if(token==null)
			token = Token.addToken(((HttpServletRequest)request),(HttpServletResponse) response);
		String url =URLSupport.getRelativePath((HttpServletRequest) request);//URLSupport
		ServletBean servletBean = ServletMapping.getInstance().getAsServlet(url);
		if(servletBean!=null){
			String resourceType = servletBean.getStyle();
			ServletDispatcher servletDispatcher =  PlugsFactory.getPlugsInstanceByAttribute(ServletDispatcher.class, resourceType);
			servletBean = servletDispatcher.getServletBean((HttpServletRequest)request);
			if(servletBean!=null)
				dispatcherServlet(request, response, chain,servletBean,token);
			else
				chain.doFilter(request, response);
		}else
			chain.doFilter(request, response);
		
		
	}
	private boolean dispatcherServlet(ServletRequest request, ServletResponse response, FilterChain chain,ServletBean servletBean,Token token) {
		if (servletBean!=null) {
			try {
				Class<?> cls = servletBean.getServletClass();
				Authentication[] authGroups =getAuthGroups(servletBean);
				for(Authentication auth : authGroups){
					// 要求token验证
					if (auth != null) {
						//0获取Token注解中的chain
						if (auth.chain().length != 0) {
							for(String action : auth.chain()){
								if(servletBean.getMethod().getName().equals(action)){//如果找到，就直接放行
									chain.doFilter(request, response);
									return true;
								}
							}
						}
						// 1获取类中的chain注解
						Chain c = cls.getAnnotation(Chain.class);
						if (c != null) {
							for(String action : auth.chain()){
								if(servletBean.getMethod().getName().equals(action)){//如果找到，就直接放行
									chain.doFilter(request, response);
									return true;
								}
							}
						}
						//2获取token注解中的roles
						if (auth.roles().length != 0) {
							if (token.containerRole(auth.roles())) {
								chain.doFilter(request, response);
								return true;
							} else {
								response.setContentType("text/html;charset=utf-8");
								response.getWriter().write(auth.message());
								return false;
							}
						}
						//3 获取Token注解中的exroles
						if (auth.exroles().length != 0) {
							if (token.containerRole(auth.exroles())) {
								response.setContentType("text/html;charset=utf-8");
								response.getWriter().write(auth.message());
								return false;
							} else {
								chain.doFilter(request, response);
								return true;
							}
						}
						chain.doFilter(request, response);
						return true;
					}
				}
				chain.doFilter(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
}
	private Authentication[] getAuthGroups(ServletBean servletBean) {
		Authentication[] authGroups;
		if(authPools==null)
			synchronized (ActionFilter.class) {
				if(authPools==null)
					authPools = new HashMap<ServletBean,Authentication[]>();
			}
		authGroups = authPools.get(servletBean);
		if(authGroups==null){
			authGroups = servletBean.getMethod().getAnnotationsByType(Authentication.class);
			if(authGroups.length==0){
				AuthenticationGroups authGroup =servletBean.getMethod().getAnnotation(AuthenticationGroups.class);
				if(authGroup!=null)
					authGroups = authGroup.value();
				else{
					authGroups = servletBean.getServletClass().getAnnotationsByType(Authentication.class);
					if(authGroups.length==0)
						authGroup = servletBean.getServletClass().getAnnotation(AuthenticationGroups.class);
					if(authGroup!=null)
						authGroups = authGroup.value();
				}
			}
			authPools.put(servletBean, authGroups);
		}
		return authGroups;
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
