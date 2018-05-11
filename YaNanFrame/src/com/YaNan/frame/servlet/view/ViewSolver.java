package com.YaNan.frame.servlet.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.servlets.ServletBean;

public interface ViewSolver {
	void render(HttpServletRequest request,HttpServletResponse response,View view, ServletBean servletBean) throws ServletException, IOException;
}
