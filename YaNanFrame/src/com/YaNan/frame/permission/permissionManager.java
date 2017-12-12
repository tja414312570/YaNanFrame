package com.YaNan.frame.permission;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.YaNan.frame.core.servletSupport.DefaultServlet;

public class permissionManager {
	private HttpSession session;
	private Map<String, Permission> map = new HashMap<String, Permission>();

	public permissionManager(DefaultServlet javaBean) {
		session = javaBean.SessionContext;
	}

	public void addPermission() {

	}

	public Permission getPermission() {
		return map.get(session.getAttribute(""));
	}

	public boolean isPermission(Permission ps) {
		return map.get(session.getAttribute("")).equals(ps);
	}
}
