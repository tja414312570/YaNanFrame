package com.YaNan.frame.servlet.view;

import java.util.Map;

public class DefaultView implements View{
	private String viewName;
	private Map<String, ?> model;
	@Override
	public void setModel(Map<String, ?> model) {
		this.model= model;
	}

	@Override
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	@Override
	public String getViewName(){
		return viewName;
	}
	@Override
	public Map<String,?> getModel(){
		return model;
	}

}
