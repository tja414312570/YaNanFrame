package com.YaNan.Demo.action;

import com.YaNan.frame.core.servlet.annotations.RequestMapping;

public class HelloWord {
	@RequestMapping
	public String sayHello(){
		return "hello";
	}
}
