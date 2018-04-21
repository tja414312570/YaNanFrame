package com.YaNan.frame.servlets;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.YaNan.frame.util.StringUtil;

public class ServletMapping {
	/**
	 * url ==>servletBean; 
	 * url such as *action.do  /action.do  /user/name/${id}
	 * linkedHashMap 遍历速度最快 查询速度最快
	 */
	private Map<String,ServletBean> servletMapping = new LinkedHashMap<String,ServletBean>();
	private Map<String,LinkedHashMap<String, ServletBean>> typeServletMapping = new LinkedHashMap<String,LinkedHashMap<String, ServletBean>>();
	//action存储：<namespace,<servletName,servletBean>>
//	private Map<String, Map<String, ServletBean>> servletMapping = new LinkedHashMap<String, Map<String, ServletBean>>();
	private static ServletMapping dsm;
	public Map<String, ServletBean> getServletMapping() {
		return servletMapping;
	}
	/*
	 * 存储形式：
	 * 1，查看命名空间是否存在
	 * 2，存在则直接在命名空间存储
	 * 3，否者创建一个新的命名空间
	 */
	public synchronized void add(final ServletBean bean) {
		this.servletMapping.put(bean.getUrlmapping(), bean);
		LinkedHashMap<String, ServletBean> servletBeanList = this.typeServletMapping.get(bean.getStyle());
		if(servletBeanList==null){
			servletBeanList = new LinkedHashMap<String, ServletBean>();
			servletBeanList.put(bean.getUrlmapping(),  bean);
			this.typeServletMapping.put(bean.getStyle(), servletBeanList);
		}else servletBeanList.put(bean.getUrlmapping(),  bean);
	}

	public void setServletMapping(Map<String, ServletBean> servletMapping) {
		this.servletMapping = servletMapping;
	}
	
	/**
	 * 精确的查找ServletBean
	 * @param url
	 * @return
	 */
	public ServletBean getServlet(String url) {
		Iterator<Entry<String, ServletBean>> iterator = this.servletMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			if(StringUtil.matchURI(url, entry.getKey()))
				return entry.getValue();
		}
		return null;
	}
	/**
	 * 模糊查找ServletBean
	 * @param url
	 * @return
	 */
	public ServletBean getAsServlet(String url) {
		Iterator<Entry<String, ServletBean>> iterator = this.servletMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			String urlMapping = entry.getKey();
			if(urlMapping.indexOf("@")>=0)
				urlMapping=urlMapping.substring(0, urlMapping.length()-2);
			if(StringUtil.matchURI(url, urlMapping))
				return entry.getValue();
		}
		return null;
	}
	public boolean includeServlet(String url) {
		return getServlet(url)!=null;
	}
	public boolean asIncludeServlet(String url) {
		return getAsServlet(url)!=null;
	}
	private ServletMapping() {
	}

	public static ServletMapping getInstance() {
		dsm = (dsm == null ? new ServletMapping() : dsm);
		return dsm;
	}
	public Iterator<String> getActionKSIterator() {
		return this.servletMapping.keySet().iterator();
	}
	public Map<String, ServletBean> getServletMappingByStype(String type) {
		return this.typeServletMapping.get(type);
	}
	public ServletBean getServlet(String actionStyle, String url) {
		Map<String, ServletBean> map = this.typeServletMapping.get(actionStyle);
		if(map==null)
			return null;
		Iterator<Entry<String, ServletBean>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			if(StringUtil.matchURI(url,entry.getKey()))
				return entry.getValue();
		}
		return null;
	}
}