package com.YaNan.frame.core.servlet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.YaNan.frame.stringSupport.StringUtil;

public class DefaultServletMapping {
	/**
	 * url ==>servletBean; 
	 * url such as *action.do  /action.do  /user/name/${id}
	 * linkedHashMap 遍历速度最快 查询速度最快
	 */
	private Map<String,ServletBean> servletMapping = new LinkedHashMap<String,ServletBean>();
	private Map<String,ServletBean> urlMappingCache = new LinkedHashMap<String, ServletBean>();
	private Map<String,ServletBean> urlAsMappingCache = new LinkedHashMap<String, ServletBean>();
	//action存储：<namespace,<servletName,servletBean>>
//	private Map<String, Map<String, ServletBean>> servletMapping = new LinkedHashMap<String, Map<String, ServletBean>>();
	private static DefaultServletMapping dsm;
	public Map<String, ServletBean> getServletMapping() {
		return servletMapping;
	}
	/*
	 * 存储形式：
	 * 1，查看命名空间是否存在
	 * 2，存在则直接在命名空间存储
	 * 3，否者创建一个新的命名空间
	 */
	public void add(ServletBean bean) {
		this.servletMapping.put(bean.getUrlmapping(), bean);
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
		ServletBean bean = this.urlMappingCache.get(url);
		if(bean==null){
			if(this.urlMappingCache.containsKey(url))
				return null;
			Iterator<String> iterator = this.servletMapping.keySet().iterator();
			while (iterator.hasNext()) {
				String urlreg = iterator.next();
				if(StringUtil.matchURI(url, urlreg))
					bean = this.servletMapping.get(urlreg);
			}
			if(bean==null&&url.indexOf(".")>=0)
				bean = getServlet(url.substring(0,url.indexOf(".")));
			this.urlMappingCache.put(url,bean);
		}
		return bean;
	}
	/**
	 * 模糊查找ServletBean
	 * @param url
	 * @return
	 */
	public ServletBean getAsServlet(String url) {
		ServletBean bean = this.urlAsMappingCache.get(url);
		if(bean==null){
			if(this.urlAsMappingCache.containsKey(url))
				return null;
			Iterator<String> iterator = this.servletMapping.keySet().iterator();
			while (iterator.hasNext()) {
				String urlreg = iterator.next();
				String urlMapping = urlreg;
				if(urlMapping.indexOf("@")>=0)
					urlMapping=urlMapping.substring(0, urlreg.length()-2);
				if(StringUtil.matchURI(url, urlMapping))
					bean = this.servletMapping.get(urlreg);
			}
			if(bean==null&&url.indexOf(".")>=0)
				bean = getServlet(url.substring(0,url.indexOf(".")));
			this.urlAsMappingCache.put(url,bean);
		}
		return bean;
	}
	public boolean includeServlet(String url) {
		return getServlet(url)!=null;
	}
	public boolean asIncludeServlet(String url) {
		return getAsServlet(url)!=null;
	}
	private DefaultServletMapping() {
	}

	public static DefaultServletMapping getInstance() {
		dsm = (dsm == null ? new DefaultServletMapping() : dsm);
		return dsm;
	}
	public Iterator<String> getActionKSIterator() {
		return this.servletMapping.keySet().iterator();
	}

	public void init() {

	}
}