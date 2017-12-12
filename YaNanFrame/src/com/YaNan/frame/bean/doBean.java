package com.YaNan.frame.bean;

import java.util.ArrayList;
import java.util.List;

public class doBean {
	private List<String> filed = new ArrayList<String>();
	private List<String> method = new ArrayList<String>();

	public doBean() {
	}

	public doBean(String[] filed, String[] method) {
		for (String s : filed)
			this.filed.add(s);
		for (String s : method)
			this.method.add(s);
	}

	public void addFiled(String filed) {
		this.filed.add(filed);
	}

	public void addFiled(String[] filed) {
		for (String s : filed)
			this.filed.add(s);
	}

}
