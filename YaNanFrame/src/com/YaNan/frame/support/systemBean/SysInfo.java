package com.YaNan.frame.support.systemBean;

import com.YaNan.frame.Native.Files;
import com.YaNan.frame.Native.JavaEnv;
import com.YaNan.frame.Native.SystemInfo;
import com.YaNan.frame.core.servletSupport.DefaultServlet;
import com.YaNan.frame.hibernate.Path;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.json.Class2Json;
import com.YaNan.frame.permission.permissionManager;
import com.YaNan.frame.service.Log;

public class SysInfo extends DefaultServlet {
	private boolean permission() {
		permissionManager p = new permissionManager(this);
		return false;
	}

	@Override
	public String execute() {
		try {
			return Class2Json.toJson(new SystemInfo());
		} catch (IllegalAccessException | SecurityException
				| IllegalArgumentException e) {
			Log.getSystemLog().write(e.toString());
			e.printStackTrace();
			return "inner error";
		}
	}

	public Object javainfo() {
		try {
			return Class2Json.toJson(new JavaEnv());
		} catch (IllegalAccessException | SecurityException
				| IllegalArgumentException e) {
			Log.getSystemLog().write(e.toString());
			e.printStackTrace();
			return "inner error";
		}
	}

	public Object errorInfo() {
		String path = Log.getSystemLog().getLogPath();
		Path wPath = WebPath.getWebPath().getLogPath();
		try {
			return new Files(path).read().replace("\r\n", "<p>")
					.replace("ERROR::", "<a style='color:red;'>ERROR::</a>")
					+ "<h2><a href='"
					+ ServicePath
					+ "\\"
					+ path.replace(wPath.realPath, wPath.originPath)
					+ "'>下载日志</a></h2>";
		} catch (Exception e) {

			e.printStackTrace();
		}
		return "读取数据时出错";
	}
}
