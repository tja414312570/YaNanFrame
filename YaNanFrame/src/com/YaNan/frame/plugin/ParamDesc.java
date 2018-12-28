package com.YaNan.frame.plugin;

public class ParamDesc {
	/**
	 *参数类型
	 */
	protected String type;
	/**
	 * 参数值
	 */
	protected String value;
	/**
	 * 参数名称
	 */
	protected String name;
	/**
	 * 
	 * @param type 参数类型
	 * @param value 参数值
	 * @param field 参数名称
	 */
	public ParamDesc(String type, String value, String field) {
		super();
		this.type = type;
		this.value = value;
		this.name = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}