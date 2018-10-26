package com.YaNan.frame.hibernate.database.entity;


import com.YaNan.frame.util.beans.xml.Attribute;

public class ForEach extends TagSupport{
	@Attribute
	private String test;
	@Override
	public String toString() {
		return "foreach [test=" + test + ", value=" + value + ", xml=" + xml + "]";
	}
}
