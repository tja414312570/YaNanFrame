package com.YaNan.frame.servlets.session.entity;

public class Result {
	private String name;
	private String command;
	private String value;
	private String role;
	private String DEFAULT;
	public String getDEFAULT() {
		return DEFAULT;
	}
	public void setDEFAULT(String dEFAULT) {
		DEFAULT = dEFAULT;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Result [name=" + name + ", command=" + command + ", value=" + value + ", role=" + role + ", DEFAULT="
				+ DEFAULT+ "]";
	}
	
		
}
