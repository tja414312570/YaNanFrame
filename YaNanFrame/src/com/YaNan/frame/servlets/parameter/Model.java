package com.YaNan.frame.servlets.parameter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 此类用于提供JSTL数据的存储以便于JSP中JSTL页面的渲染。
 * 实现类每次请求将会产生一个新的Model实体，同一请求多个Model参数只有一个Model实体
 * @author yanan
 *
 */
public interface Model {
	/**
	 * 设置HttpServletRequest
	 * @param request
	 */
	void setServletRequest(HttpServletRequest request);
	/**
	 * 获取HttpServletRequest
	 * @param request
	 */
	HttpServletRequest getServletRequest();
	/**
	 * 设置属性，key为对象的类名
	 * @param object
	 */
	void set(Object value);
	/**
	 * 设置属性
	 * @param key
	 * @param value
	 */
	void set(String key,Object value);
	/**
	 * 添加map属性
	 * @param map
	 */
	void addAll(Map<String,Object> map);
	/**
	 * 获取属性
	 * @param key
	 * @return
	 */
	Object get(String key);
	/**
	 * 判断是否有某个属性
	 * @param key
	 * @return
	 */
	boolean has(String key);
}
