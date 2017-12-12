package com.YaNan.frame.support.systemBean;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.Native.Files;
import com.YaNan.frame.core.servletSupport.DefaultServlet;
import com.YaNan.frame.hibernate.Path;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.json.Map2Json;

public class FileScanner extends DefaultServlet {
	public String path;
	public String type;

	@Override
	public String execute() {
		if (this.path == null) {
			this.path = WebPath.getWebPath().getRoot().realPath;
			this.type = "dir";
			SessionContext.setAttribute("root", this.path);
		} else {
			this.path = (String) SessionContext.getAttribute("root")
					+ this.path;
		}
		System.out.println(this.path);
		if (type.equals("dir")) {
			File[] fs = new File(this.path).listFiles();
			Map<String, String> map = new HashMap<String, String>();
			for (File f : fs)
				map.put("'" + f.getName() + "'", "" + f.isDirectory());
			System.out.println(Map2Json.mapToJson(map));
			return "(" + Map2Json.mapToJson(map) + ")";
		} else {
			Files f = new Files(this.path);
			try {
				return f.read().replace("\r\n", "<p>");
			} catch (Exception e) {
				e.printStackTrace();
				return "inner error";
			}
		}

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object browerServer() {
		Path tmpPath = WebPath.getWebPath().get("uploadTmp");
		File[] fs = new File(tmpPath.realPath).listFiles();
		StringBuffer buffer = new StringBuffer();
		buffer.append("");
		for (File f : fs)
			buffer.append("<div style='width:240px;height:340px;border:1px solid #888;float:left;margin:10px;overflow:hidden;padding:4px;background:#f1f1f1;'><img width='240' height='320px' src='"
					+ ServicePath
					+ tmpPath.path
					+ f.getName()
					+ "'></img><a href='"
					+ ServicePath
					+ tmpPath.path
					+ f.getName()
					+ "'style='display:block;text-align:center;background:#fff;margin-top:4px;border-radius:4px;'>"
					+ f.getName() + "</a></div>");
		return buffer.toString();
	}
}
