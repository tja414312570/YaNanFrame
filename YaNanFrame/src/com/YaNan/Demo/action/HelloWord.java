package com.YaNan.Demo.action;

import com.YaNan.frame.servlets.annotations.Action;
import com.YaNan.frame.servlets.servletSupport.DefaultServlet;

public class HelloWord extends DefaultServlet{
	@Action
	public String sayHello(){
		return "hello";
	}
}
