package com.YaNan.Demo.action;

import com.YaNan.frame.servlets.REQUEST_METHOD;
import com.YaNan.frame.servlets.annotations.RequestMapping;

public class RestfulTest {
	@RequestMapping
	public String testGet(String parameter){
		return parameter;
	}
	@RequestMapping(method=REQUEST_METHOD.POST)
	public String testPost(String parameter){
		return parameter;
	}
	@RequestMapping(method=REQUEST_METHOD.POST)
	public String testArray(String[] parameter){
		System.out.println(parameter.length);
		String result = "array length :"+parameter.length+" array content:";
		for(String str : parameter){
			System.out.println(str);
			result+=str+",";
		}
		return result;
	}

}
