package com.YaNan.frame.core.servlet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultServletMapping {
	//action存储：<namespace,<servletName,servletBean>>
	private Map<String, Map<String, ServletBean>> servletMapping = new LinkedHashMap<String, Map<String, ServletBean>>();
	private static DefaultServletMapping dsm;
	public Map<String, Map<String, ServletBean>> getServletMapping() {
		return servletMapping;
	}
	/*
	 * 存储形式：
	 * 1，查看命名空间是否存在
	 * 2，存在则直接在命名空间存储
	 * 3，否者创建一个新的命名空间
	 */
	public void add(String name,ServletBean bean) {
		String namespace = bean.getNameSpace();
		if (this.servletMapping.containsKey(namespace)) {
			this.servletMapping.get(namespace).put(name, bean);
		} else {
			Map<String, ServletBean> nameServletMap = new LinkedHashMap<String, ServletBean>();
			nameServletMap.put(name, bean);
			this.servletMapping.put(namespace, nameServletMap);
		}
	}

	public void setServletMapping(
			Map<String, Map<String, ServletBean>> servletMapping) {
		this.servletMapping = servletMapping;
	}

	public ServletBean getServlet(String namespace,String name) {
		if (!this.isExist(namespace, name))
			return null;
		if (this.servletMapping.get(namespace) == null){
			return this.servletMapping.get("*").get(name);
		}else{
			if( this.servletMapping.get(namespace).containsKey(name)){
				return this.servletMapping.get(namespace).get(name);
			}else{
				return this.servletMapping.get("*").get(name);
			}
		}
	}
	private DefaultServletMapping() {
	}

	public static DefaultServletMapping getInstance() {
		dsm = (dsm == null ? new DefaultServletMapping() : dsm);
		return dsm;
	}
	/*
	 * 先从集合里查找命名空间是否存在
	 * 如果命名空间存在，优先读取命名空间里的servlet
	 * 否者从*命名空间读取
	 */
	public boolean isExist(String namespace,String servletName) {
		if (this.servletMapping.containsKey(namespace)){
			if (this.servletMapping.get(namespace).containsKey(servletName)) {
				return true;
			} else {
				return this.servletMapping.get("*").containsKey(servletName);
			}
		}else {
			if(this.servletMapping.containsKey("*"))
				return this.servletMapping.get("*").containsKey(servletName);
		}
		return false;
	}
	public boolean asExist(String namespace,String servletName) {
		if (this.servletMapping.containsKey(namespace))
			if (this.servletMapping.get(namespace).containsKey(servletName)) 
				return true;
		return false;
	}
	public Iterator<String> getActionKSIterator() {
		return this.servletMapping.keySet().iterator();
	}

	public Iterator<Entry<String, Map<String, ServletBean>>> getActionEIterator() {
		return this.servletMapping.entrySet().iterator();
	}

	public Iterator<String> getServletKSIterator(String namespace) {
		return this.servletMapping.get(namespace).keySet().iterator();
	}

	public Iterator<Entry<String, ServletBean>> getServletEIterator(
			String namespace) {
		return this.servletMapping.get(namespace).entrySet().iterator();
	}

	public void init() {

	}
}
