package com.YaNan.frame.web.restful;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@WebFilter(filterName = "restful_request_filter", urlPatterns = "/*")
public class RestfulRequestFilter  extends HttpServlet implements Filter {
	/**
	 */
	private static final long serialVersionUID = -3877282673186471728L;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new RestfulRequestWrapper((HttpServletRequest) request),
				response);
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
