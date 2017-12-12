package com.YaNan.frame.hibernate.database.entity;

public class Package {
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPACKAGE() {
		return PACKAGE;
	}
	public void setPACKAGE(String pACKAGE) {
		PACKAGE = pACKAGE;
	}
	private String PACKAGE;
	@Override
	public String toString() {
		return "Package [id=" + id + ", PACKAGE=" + PACKAGE + "]";
	}
}
