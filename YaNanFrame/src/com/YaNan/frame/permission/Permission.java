package com.YaNan.frame.permission;

public class Permission {
	private boolean system;
	private String name;
	private String group;
	private boolean hibernate;

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isHibernate() {
		return hibernate;
	}

	public void setHibernate(boolean hibernate) {
		this.hibernate = hibernate;
	}
}
