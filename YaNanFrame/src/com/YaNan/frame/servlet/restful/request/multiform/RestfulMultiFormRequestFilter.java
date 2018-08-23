package com.YaNan.frame.servlet.restful.request.multiform;

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

import org.apache.commons.fileupload.FileUploadException;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;

@WebFilter(filterName = "restful_upload_filter", urlPatterns = "/*")
public class RestfulMultiFormRequestFilter  extends HttpServlet implements Filter {
	Log log = PlugsFactory.getPlugsInstanceAllowNull(Log.class, this.getClass());
	/**
	 */
	private static final long serialVersionUID = -3877282673186471728L;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(new RestfulMultiFormRequestWrapper((HttpServletRequest) request),
					response);
		} catch (FileUploadException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
