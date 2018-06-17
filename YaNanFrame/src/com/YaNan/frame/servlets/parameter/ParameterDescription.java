package com.YaNan.frame.servlets.parameter;

public class ParameterDescription {
	public static interface ParameterType{
		public static final int PathVariable =0;
		public static final int RequestParam =1;
		public static final int CookieValue	=2;
		public static final int SessionAttributes =3;
		public static final int RequestHeader =4;
		public static final int RequestBody	=5;
	}
	/**
	 * 参数名
	 */
	private String name;
	/**
	 * 参数类型
	 */
	private int type;
	/**
	 * 参数默认值
	 */
	private String value;
	
	public ParameterDescription(String name, int type, String value) {
		super();
		this.name = name.trim().equals("")?null:name.trim();
		this.type = type;
		this.value = value.trim().equals("")?null:value.trim();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "ParameterDescription [name=" + name + ", type=" + type + ", value=" + value + "]";
	}

}
