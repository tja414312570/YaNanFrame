package com.YaNan.frame.hibernate.database.entity;

import com.YaNan.frame.util.beans.xml.Attribute;

public class Include extends TagSupport{
	@Attribute
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Include [id=" + id + "]";
	}
	
}
