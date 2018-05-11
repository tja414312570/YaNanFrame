package com.YaNan.Demo.pojo;

public class User {
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Array getArray() {
		return array;
	}
	public void setArray(Array array) {
		this.array = array;
	}
	private String name;
	private Array array;
}
