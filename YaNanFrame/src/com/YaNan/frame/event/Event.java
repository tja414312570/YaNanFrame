package com.YaNan.frame.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.event.Event.eventUpdate;

/**
 * need:jdk 1.6 + encoding = "UTF-8" 此类为抽象类 被监听的对象将继承自此类 被监听的对象可以自己添加新的方法如
 * onError，onStart 子弟添加的新的方法如果需要调用事件对象通过
 * addEventListener方法获得监听，需要调用此类里的addEvent方法添加事件
 */
public abstract class Event {
	private Map<String, EventUnit> map = new HashMap<String, EventUnit>();

	/**
	 * 添加一个事件监听，传入一个事件名，执行方法，执行对象
	 * 
	 * @param event
	 * @param method
	 * @param obj
	 */
	public void addEventListener(String event, String method, Object obj) {
		ClassLoader loader = new ClassLoader(obj);
		if (loader.hasMethod(method, Object.class)) {
			if (this.map.containsKey(event)) {
				try {
					Method newMethod = loader.getMethod(method, Object.class);
					this.map.get(event).setMethod(newMethod).setObj(obj);
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * addEventListener(String event,eventUpdate e) 添加事件监听，传入一个事件名，以及一个匿名对象
	 * 
	 * @param event
	 * @param e
	 */
	public void addEventListener(String event, eventUpdate e) {
		this.map.get(event).setUpdate(e);
	}

	/**
	 * 增加事件，用于子类
	 * 
	 * @param event
	 * @param eObj
	 */
	protected void addEvent(String... event) {
		for (int i = 0; i < event.length; i++)
			this.map.put(event[i], new EventUnit());
	}

	/**
	 * 移除事件监听，传入一个或多个事件名
	 * 
	 * @param event
	 * @param eObj
	 */
	public void removeEvent(String... event) {
		for (String e : event)
			this.map.remove(e);
	}

	public void on(String event, Method method) {

	}

	/**
	 * 内部类，用于匿名对象使用
	 * 
	 * @author
	 *
	 */
	public static abstract class eventUpdate {
		public abstract void update(Object e);
	}

	/**
	 * 更新事件 子类方法的
	 * 
	 * @param event
	 * @param para
	 */
	protected void update(String event, Object para) {
		if (this.map.containsKey(event)) {
			EventUnit E = this.map.get(event);
			if (E.getUpdate() != null)
				E.getUpdate().update(para);
			if (E.getObj() != null) {
				try {
					E.getMethod().invoke(E.getObj(), para);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

	}
}

class EventUnit {
	private eventUpdate update;
	private Method method;
	private Object obj;

	public eventUpdate getUpdate() {
		return update;
	}

	public void setUpdate(eventUpdate e) {
		this.update = e;
	}

	public Method getMethod() {
		return method;
	}

	public EventUnit setMethod(Method method) {
		this.method = method;
		return this;
	}

	public Object getObj() {
		return obj;
	}

	public EventUnit setObj(Object obj) {
		this.obj = obj;
		return this;
	}
}