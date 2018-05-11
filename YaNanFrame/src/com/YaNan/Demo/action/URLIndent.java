package com.YaNan.Demo.action;

import com.YaNan.frame.servlet.view.DefaultViewSolver;
import com.YaNan.frame.servlets.annotations.RequestMapping;

public class URLIndent {
	@RequestMapping(value = "/{path}",viewSolver=DefaultViewSolver.class)
	public String gotoUrl(String path){
		return path;
	}
}	
