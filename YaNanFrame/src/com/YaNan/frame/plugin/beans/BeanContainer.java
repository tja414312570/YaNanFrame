package com.YaNan.frame.plugin.beans;

import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.RegisterDescription;


public class BeanContainer {
	private static BeanContainer beanContext;
	private Map<String,Object> beanContainer;
	private Map<Class<?>,Object> beanClassContainer;
	public static BeanContainer getContext(){
		if(beanContext==null)
			synchronized (BeanContainer.class) {
				if(beanContext==null){
					beanContext = new BeanContainer();
				}
			}
		return beanContext;
	}
	public void addBean(String id,Object bean,RegisterDescription description){
		if(this.beanContainer==null)
			synchronized (beanContext) {
				if(this.beanContainer==null){
					this.beanContainer = new HashMap<String, Object>();
					this.beanClassContainer = new HashMap<Class<?>, Object>();
				}
			}
		if(bean==null)
			throw new RuntimeException("bean id \""+id+"\" is null");
		if(this.beanContainer.containsKey(id))
			throw new RuntimeException("bean id \""+id+"\" is exists!");
		this.beanContainer.put(id, bean);
		this.beanClassContainer.put(description.getRegisterClass(), bean);
		for(Class<?> clzz : description.getPlugs()){
			this.beanClassContainer.put(clzz,bean);
		}
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanId) {
		if(this.beanContainer==null)
			throw new RuntimeException("bean context is not init or no bean defined");
		return (T) this.beanContainer.get(beanId);
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<?> beanClass) {
		if(this.beanContainer==null)
			throw new RuntimeException("bean context is not init or no bean defined");
		return (T) this.beanClassContainer.get(beanClass);
	}
}
