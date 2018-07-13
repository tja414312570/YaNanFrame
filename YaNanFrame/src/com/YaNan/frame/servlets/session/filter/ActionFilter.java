package com.YaNan.frame.servlets.session.filter;

import java.io.IOException;

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
import com.YaNan.frame.servlets.session.annotation.Chain;

@WebFilter(filterName = "actionFilter", urlPatterns = "/*")
public class ActionFilter implements Filter{
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
				//.getRequestPath((HttpServletRequest) request);
		//判断是否是servlet
		ServletBean servletBean = ServletMapping.getInstance().getAsServlet(url);
		if(servletBean!=null){//这一步对于restful风格不能准确判断
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
				com.YaNan.frame.servlets.session.annotation.Authentication itoken =servletBean.getMethod().getAnnotation(com.YaNan.frame.servlets.session.annotation.Authentication.class);//从当前方法获取iToken注解
				if (itoken == null)
					itoken = cls.getAnnotation(com.YaNan.frame.servlets.session.annotation.Authentication.class);
				// 要求token验证
				if (itoken != null) {
					//0获取Token注解中的chain
					if (itoken.chain().length != 0) {
						for(String action : itoken.chain()){
							if(servletBean.getMethod().getName().equals(action)){//如果找到，就直接放行
								chain.doFilter(request, response);
								return true;
							}
						}
					}
					// 1获取类中的chain注解
					Chain c = cls.getAnnotation(Chain.class);
					if (c != null) {
						for(String action : itoken.chain()){
							if(servletBean.getMethod().getName().equals(action)){//如果找到，就直接放行
								chain.doFilter(request, response);
								return true;
							}
						}
					}
					//2获取token注解中的roles
					if (itoken.roles().length != 0) {
						if (token.containerRole(itoken.roles())) {
							chain.doFilter(request, response);
							return true;
						} else {
							response.getWriter().write(itoken.onFailed());
							return false;
						}
					}
					//3 获取Token注解中的exroles
					if (itoken.exroles().length != 0) {
						if (token.containerRole(itoken.exroles())) {
							response.getWriter().write(itoken.onFailed());
							return false;
						} else {
							chain.doFilter(request, response);
							return true;
						}
					}
					chain.doFilter(request, response);
					return true;
				} else {
					chain.doFilter(request, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
