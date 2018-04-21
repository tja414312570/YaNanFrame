package com.YaNan.frame.servlets.session;

public interface Role {
	public static Class<?> cls=null;

	public String getRole();

	public String getDescription();

	public int getLevel();

	public void setRole(String role);

	public void setDescription(String desc);

	public void setLevel(int lev);

	public Role getRole(String key);
}
