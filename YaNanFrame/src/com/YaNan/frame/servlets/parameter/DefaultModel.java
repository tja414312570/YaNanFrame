package com.YaNan.frame.servlets.parameter;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.YaNan.frame.plugin.annotations.Register;

/**
 * 默认实现的Model
 * {@link Model}
 * @author yanan
 *
 */
@Register(priority=Integer.MAX_VALUE,signlTon=false)
public class DefaultModel implements Model{
	private HttpServletRequest request;
	public DefaultModel(HttpServletRequest request){
		this.request = request;
	}
	@Override
	public void setServletRequest(HttpServletRequest request){
		this.request = request;
	}
	@Override
	public HttpServletRequest getServletRequest(){
		return this.request;
	}
	@Override
	public void set(Object value) {
		if(value!=null)
			this.set(value.getClass().getSimpleName(),value);
	}
	@Override
	public void set(String key, Object value) {
		if(key!=null)
			this.request.setAttribute(key, value);
	}
	@Override
	public void addAll(Map<String, Object> map) {
		if(map!=null){
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, Object> entry = iterator.next();
				this.set(entry.getKey(),entry.getValue());
			}
		}
			
	}
	@Override
	public Object get(String key) {
		return this.request.getAttribute(key);
	}
	@Override
	public boolean has(String key) {
		return this.request.getAttribute(key)!=null;
	}
}
