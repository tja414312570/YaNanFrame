package com.YaNan.frame.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.YaNan.frame.core.ApplicationContext;
import com.YaNan.frame.service.ClassInfo;

@WebFilter(filterName = "SecurityFilter", urlPatterns = "/*")
@ClassInfo(version = 0)
public class SecurityFilter extends HttpServlet implements Filter {
	private static final long serialVersionUID = 1L;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		
		ApplicationContext.init(request, response);
		HttpSession session = ((HttpServletRequest) request).getSession();
		String url = ((HttpServletRequest) request).getRequestURI();
		// ||url.contains("/img")||url.contains("/css")||url.contains("/js")||url.contains("/ckeditor")||url.contains("/fonts")
		if (url.contains("/install") || url.contains("/systemLog")) {
			if (session.getAttribute("ff3d1f1f9eb1ea36fcd9855c229f6451") != null
					&& Objects.equals(session.getAttribute("ff3d1f1f9eb1ea36fcd9855c229f6451"), "aa176a953ca212e7123f1a353d5944fd")) {
				chain.doFilter(request, response);
			} else {
				if (url.contains("adminSignIn.do")
						|| url.contains("admin.html") || url.contains("js")
						|| url.contains("css") || url.contains("img")
						|| url.contains("/fonts") || url.contains("/ckeditor") || url.contains("install/Cryption.html")) {
					chain.doFilter(request, response);
				} else {
					response.getWriter().write("您无权访问此页面，如果您是管理员，请");
					response.getWriter().write(
							"<a href='admin.html'>点击此处登录</a>");
				}
			}
			return;
		}
		// if(url.contains("/admin")){
		// if(session.getAttribute("admin")!=null&&session.getAttribute("admin").equals("true")){
		// chain.doFilter(request, response);
		// }else{
		// if(url.contains("adminSignIn.do")||url.contains("admin.html")||url.contains("js")||url.contains("css")||url.contains("img")||url.contains("/fonts")){
		// chain.doFilter(request, response);
		// }else{
		// response.getWriter().write("您无权访问此页面，如果您是管理员，请");
		// response.getWriter().write("<a href='login.html'>点击此处登录</a>");
		// }
		// }
		// return;
		// }
		chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request),
				response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
