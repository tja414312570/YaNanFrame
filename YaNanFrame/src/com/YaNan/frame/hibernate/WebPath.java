package com.YaNan.frame.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.YaNan.frame.service.Log;

/**
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class WebPath {
	private static WebPath webpath;
	private Map<String, Path> map = new HashMap<String, Path>();

	private WebPath() {
	}

	public static WebPath getWebPath() {
		webpath = (webpath == null ? new WebPath() : webpath);
		return webpath;
	}

	public boolean hasPath(String string) {
		return map.containsKey(string);
	}

	public void set(String name, String path) {
		if (!this.map.containsKey(name))
			this.map.put(name, new Path(path));
	}

	public Path get(String name) {
		if(!this.map.containsKey(name))Log.getSystemLog().exception(new Exception("path id ["+name+"] is not exists,please check init configure file at <init.xml>!"));
		return this.map.get(name);
	}

	public Iterator<String> iterator() {
		return this.map.keySet().iterator();
	}

	public void setRoot(String path) {
		if (!this.map.containsKey("Root"))
			this.map.put("Root", new Path(path));
		if (!this.map.containsKey("systemLog"))
			this.map.put("systemLog", new Path("systemLog/"));
		if (!this.map.containsKey("classPath"))
			this.map.put("classPath", new Path("WEB-INF/classes/"));
		if (!this.map.containsKey("tmp"))
			this.map.put("tmp", new Path("Tmp/"));
	}

	public Path getRoot() {
		return this.map.get("Root");
	}
	public Path getTmp(){
		return this.map.get("Tmp");
	}
	public Path getLogPath() {
		return this.map.get("systemLog");
	}

	public Path getClassPath() {
		return this.map.get("classPath");
	}
}
