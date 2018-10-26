package com.YaNan.frame.plugin.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 方法拦截器链表
 * v1.0 实现InvokeHandler的链表结构
 * v1.1 添加属性annotation，用于对特殊注解的InvokeHandler的
 * 		快速与便捷处理方式
 * @author yanan
 * @date 2018-07-12
 */
public class InvokeHandlerSet {
	private Object invokeHandler;
	private Map<Class<?>,Object> annotations;
	private InvokeHandlerSet first;
	private InvokeHandlerSet before;
	private InvokeHandlerSet last;
	private InvokeHandlerSet next;
	
	public void addAnnotation(Annotation annos){
		if(this.annotations==null){
			this.annotations = new HashMap<Class<?>,Object>();
		}
		this.annotations.put(annos.annotationType(),annos);
	}
	public void setAnnotations(Map<Class<?>, Object> annotations) {
		this.annotations = annotations;
	}
	@SuppressWarnings("unchecked")
	public <T> T getAnnotation(Class<?> annoClzz){
		return this.annotations==null?null:
			(T)this.annotations.get(annoClzz);
	}
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getAnnotations(){
		return this.annotations==null?null:
			(Collection<T>)this.annotations.values();
	}
	public InvokeHandlerSet(Object handler){
		this.invokeHandler=handler;
		this.first = this;
		this.last = this;    
	}
	
	public void addInvokeHandlerSet(InvokeHandlerSet handlerSet){
		handlerSet.setBefore(this);
		handlerSet.setFirst(this.first);
		this.setNext(handlerSet);
		this.setLast(handlerSet);
	}
	public InvokeHandlerSet getBefore() {
		return before;
	}
	public Iterator<InvokeHandlerSet> iterator(){
		return new InvokeHandlerItertor(this);
	}

	void setBefore(InvokeHandlerSet before) {
		this.before = before;
	}
	@SuppressWarnings("unchecked")
	public <T> T getInvokeHandler() {
		return (T) invokeHandler;
	}
	public void setInvokeHandler(InvokeHandler invokeHandler) {
		this.invokeHandler = invokeHandler;
	}
	public InvokeHandlerSet getLast() {
		return last;
	}
	public void setLast(InvokeHandlerSet last) {
		this.last = last;
		if(this.before!=null)
			this.before.setLast(last);
	}
	public InvokeHandlerSet getNext() {
		return next;
	}
	public void setNext(InvokeHandlerSet next) {
		this.next = next;
	}

	public InvokeHandlerSet getFirst() {
		return first;
	}

	public void setFirst(InvokeHandlerSet first) {
		this.first = first;
	}
	
	class InvokeHandlerItertor implements Iterator<InvokeHandlerSet>{
		private InvokeHandlerSet current=null;
		private InvokeHandlerSet next=null;
		public InvokeHandlerItertor(InvokeHandlerSet handlerSet){
			next = handlerSet;
		}
		@Override
		public boolean hasNext() {
			return next!=null;
		}
		@Override
		public InvokeHandlerSet next() {
			current = next;
			next = next.getNext();
			return current;
		}
		
	}
}
