package com.YaNan.frame.servlet.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ServletBean;

@Register(priority=Integer.MAX_VALUE)
public class DefaultViewSolver implements ViewSolver{
	private String prefix="";
	private String suffix="jsp";


	/**
	 * 默认视图渲染，首先会判断文件是否存在，如果存在的话会调用各默认的DispatcherServlet处理，如果不存在，会直接输出内容
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, View view, ServletBean servletBean) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder(prefix).append(view.getViewName()).append(".").append(suffix);
		request.getRequestDispatcher(sb.toString()).forward(request, response);
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public String getSuffix() {
		return suffix;
	}


	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
