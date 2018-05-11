package com.YaNan.Demo.action;

import com.YaNan.Demo.pojo.Array;
import com.YaNan.Demo.pojo.User;
import com.YaNan.frame.servlets.REQUEST_METHOD;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.annotations.RequestParam;
import com.google.gson.Gson;

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
	public String testArray(String[] net,String[] types){
		String result = "array length :"+(types.length+net.length)+" array content:";
		for(String str : types){
			result+=str+",";
		}
		for(String str : net){
			result+=str+",";
		}
		return result;
	}
	@RequestMapping(method=REQUEST_METHOD.POST)
	public String testPojo(Array array){
		return new Gson().toJson(array);
	}
	@RequestMapping(method=REQUEST_METHOD.POST)
	public String testPojoAnno(@RequestParam("array") Array array){
		return new Gson().toJson(array);
	}
	@RequestMapping(method=REQUEST_METHOD.POST)
	public String testPojouser(@RequestParam("user") User user){
		return new Gson().toJson(user);
	}

}
