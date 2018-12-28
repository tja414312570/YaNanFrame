package com.YaNan.frame.plugin.beans;

import java.util.HashMap;
import java.util.Map;


public class BeanContext {
	private static BeanContext beanContext;
	private Map<String,Object> beanContainer;
	public static BeanContext getContext(){
		if(beanContext==null)
			synchronized (BeanContext.class) {
				if(beanContext==null){
					beanContext = new BeanContext();
				}
			}
		return beanContext;
	}
	public void addBean(String id,Object bean){
		if(this.beanContainer==null)
			synchronized (beanContext) {
				if(this.beanContainer==null)
					this.beanContainer = new HashMap<String, Object>();
			}
		if(bean==null)
			throw new RuntimeException("bean id \""+id+"\" is null");
		if(this.beanContainer.containsKey(id))
			throw new RuntimeException("bean id \""+id+"\" is exists!");
		this.beanContainer.put(id, bean);
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanId) {
		if(this.beanContainer==null)
			throw new RuntimeException("bean context is not init or no bean defined");
		return (T) this.beanContainer.get(beanId);
	}
}
