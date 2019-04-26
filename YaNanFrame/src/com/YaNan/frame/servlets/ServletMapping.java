package com.YaNan.frame.servlets;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.YaNan.frame.utils.PathMatcher;

import java.util.TreeMap;

public class ServletMapping {
	/**
	 * 倒叙排序
	 */
	private static Comparator<String> sort = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return o2.compareTo(o1);
		}
	};
	/**
	 * url ==>servletBean; 
	 * url such as *action.do  /action.do  /user/name/${id}
	 */
	private Map<String,ServletBean> servletMapping = new TreeMap<String,ServletBean>(sort);
	private Map<String,TreeMap<String, ServletBean>> typeServletMapping = new TreeMap<String,TreeMap<String, ServletBean>>(sort);
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
		TreeMap<String, ServletBean> servletBeanList = this.typeServletMapping.get(bean.getStyle());
		if(servletBeanList==null){
			servletBeanList = new TreeMap<String, ServletBean>(sort);
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
			if(PathMatcher.match(entry.getKey(), url).isMatch())
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
			if(PathMatcher.match(urlMapping, url).isMatch())
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
			if(PathMatcher.match(entry.getKey(), url).isMatch())
				return entry.getValue();
		}
		return null;
	}
}