package com.YaNan.frame.support.reserve;

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

import com.YaNan.frame.support.ReserveManager;

@WebFilter(filterName = "reserveFilter", urlPatterns = "/*")
public class reserveFilter extends HttpServlet implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		String url = ((HttpServletRequest) request).getRequestURI();
		// ||url.contains("/img")||url.contains("/css")||url.contains("/js")||url.contains("/ckeditor")||url.contains("/fonts")
		if (url.contains("/install") || url.contains("/systemLog")
				|| url.contains("/resource")) {
			chain.doFilter(request, response);
			return;
		}
		if (ReserveManager.getReserve().enable()||url.contains("yanan.system.init")) {
			response.getWriter().write(
					"非常抱歉！<p>由于服务器应用或其他异常,导致服务器不能正常启动<p>已进入备用系统");
			response.getWriter().write(
					"<p><a href='install/errorLog.do'>启动日志</a>");
			response.getWriter().write(
					"<p><a href='install/index.html'>重新安装</a>");
			response.getWriter().write(
					"<p><a href='install/syscheck.html'>系统问题检查</a>");
			response.getWriter().write(
					"<p><a href='install/sysinfo.html'>系统信息</a>");
			response.getWriter().write(
					"<p><a href='install/yption.html'>秘钥上传</a>");
			response.getWriter().write(
					"<p><a href='install/backandrecovery.html'>系统备份与恢复</a>");
			// ((HttpServletResponse)response).sendRedirect("install/index.html");
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
