package com.YaNan.frame.plugin.handler;

import java.util.Iterator;

/**
 * 方法拦截器链表
 * @author yanan
 * @date 2018-07-12
 */
public class InvokeHandlerSet {
	private InvokeHandler invokeHandler;
	private InvokeHandlerSet first;
	private InvokeHandlerSet before;
	private InvokeHandlerSet last;
	private InvokeHandlerSet next;
	
	public InvokeHandlerSet(InvokeHandler handler){
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
	public InvokeHandler getInvokeHandler() {
		return invokeHandler;
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
