package com.YaNan.frame.servlet.view;

import java.util.Map;

public interface View {
	//默认视图名称
	public static final String ViewName = View.class.getName()+".view";
	default String getContentType() {
		return null;
	}
	void setModel(Map<String,?> map);
	default Map<String,?> getModel(){
		return null;
	}
	void setViewName(String viewName);
	default String getViewName(){
		return ViewName;
	}
}
