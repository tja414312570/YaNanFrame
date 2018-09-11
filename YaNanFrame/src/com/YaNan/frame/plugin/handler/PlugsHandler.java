package com.YaNan.frame.plugin.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.interfacer.PlugsListener;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 组件实例处理器，用于对代理对象的处理，采用jdk方式
 * v1.1 支持cglib代理方式
 * v1.2 支持方法拦截
 * v1.2.1 修复方法拦截时无限调用bug
 * v1.2.2 添加获取RegisterDescription方法
 * v1.2.3 添加每个MethodHandler对应的InvokeHandler
 * 
 * @author yanan
 *
 */
public class PlugsHandler implements InvocationHandler, PlugsListener, MethodInterceptor {
	public static enum ProxyType {
		JDK, CGLIB
	}

	private RegisterDescription registerDescription;// 注册描述类
	private Object proxyObject;// 代理对象
	private Class<?> proxyClass;// 代理类
	private Class<?> interfaceClass;// 接口类
	private ProxyType proxyType = ProxyType.JDK;// 代理模式
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class, PlugsHandler.class);

	/**
	 * return the proxy object
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxyObject() {
		return (T) proxyObject;
	}

	public RegisterDescription getRegisterDescription() {
		return registerDescription;
	}

	public void setRegisterDescription(RegisterDescription registerDescription) {
		this.registerDescription = registerDescription;
	}

	/**
	 * return the proxy class
	 * 
	 * @return
	 */
	public Class<?> getProxyClass() {
		return proxyClass;
	}

	/**
	 * return the proxy interface
	 * 
	 * @return
	 */
	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	/**
	 * return the proxy method handler mapping
	 * 
	 * @return
	 */
	public Map<Method, InvokeHandlerSet> getHandlerMapping() {
		return registerDescription == null ? null : registerDescription.getHandlerMapping();
	}

	public static void setLog(Log log) {
		PlugsHandler.log = log;
	}

	/**
	 * jdk proxy PlugsHandler constructor
	 * 
	 * @param target
	 * @param mapperInterface
	 */
	public PlugsHandler(Object target, Class<?> mapperInterface, RegisterDescription registerDescription) {
		super();
		this.proxyObject = target;
		this.proxyClass = target.getClass();
		this.interfaceClass = mapperInterface;
		this.registerDescription = registerDescription;
	}

	/**
	 * cglib proxy PlugsHandler constructor
	 * 
	 * @param proxyClass
	 * @param parameters
	 */
	public PlugsHandler(Class<?> proxyClass, Object[] parameters, RegisterDescription registerDescription) {
		this.proxyClass = proxyClass;
		this.proxyType = ProxyType.CGLIB;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(proxyClass);
		enhancer.setCallback(this);
		this.registerDescription = registerDescription;
		if (parameters.length == 0)
			this.proxyObject = enhancer.create();
		else
			this.proxyObject = enhancer.create(com.YaNan.frame.reflect.ClassLoader.getParameterTypes(parameters),
					parameters);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		// if the interface class is InvokeHandler class ,jump the method filter
		if (this.interfaceClass.equals(InvokeHandler.class))
			try {
				return method.invoke(this.proxyObject, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		Object handlerResult;
		InvokeHandlerSet handler = null;
		if (registerDescription != null && registerDescription.getHandlerMapping() != null) {
			handler = registerDescription.getHandlerMapping().get(method);
		}
		MethodHandler mh = null;
		if (handler != null) {
			mh = new MethodHandler(this, method, args);
			Iterator<InvokeHandlerSet> iterator = handler.iterator();
			InvokeHandlerSet hs;
			while (iterator.hasNext()) {
				hs = iterator.next();
				mh.setInvokeHandlerSet(hs);
				handlerResult = hs.getInvokeHandler().before(mh);
				if (!mh.isChain())
					return handlerResult;
				mh.setChain(false);
			}
		}
		try {
			method.setAccessible(true);
			Object result = method.invoke(this.proxyObject, args);
			method.setAccessible(false);
			if (handler != null) {
				mh.setOriginResult(result);
				Iterator<InvokeHandlerSet> iterator = handler.iterator();
				InvokeHandlerSet hs;
				while (iterator.hasNext()) {
					hs = iterator.next();
					mh.setInvokeHandlerSet(hs);
					handlerResult = hs.getInvokeHandler().after(mh);
					if (!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
			}
			return result;
		} catch (Exception e) {
			if (handler != null) {
				Iterator<InvokeHandlerSet> iterator = handler.iterator();
				InvokeHandlerSet hs;
				while (iterator.hasNext()) {
					hs = iterator.next();
					mh.setInvokeHandlerSet(hs);
					handlerResult = hs.getInvokeHandler().error(mh, e);
					if (!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
				e.printStackTrace();
			} else {
				log.error(e);
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newMapperProxy(Class<T> mapperInterface, RegisterDescription registerDescription,
			Object target) {
		ClassLoader classLoader = mapperInterface.getClassLoader();
		Class<?>[] interfaces = new Class[1];
		interfaces[0] = mapperInterface;
		PlugsHandler plugsHandler = new PlugsHandler(target, mapperInterface, registerDescription);
		return (T) Proxy.newProxyInstance(classLoader, interfaces, plugsHandler);
	}

	public static Object newCglibProxy(Class<?> proxyClass, RegisterDescription registerDescription,
			Object... parameters) {
		return new PlugsHandler(proxyClass, parameters, registerDescription).getProxyObject();
	}

	@Override
	public void excute(PlugsFactory plugsFactory) {
		log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class, PlugsHandler.class);
	}

	@Override
	public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy)
			throws Throwable {
		Object handlerResult;
		InvokeHandlerSet handler = null;
		if (registerDescription != null && registerDescription.getHandlerMapping() != null) {
			handler = registerDescription.getHandlerMapping().get(method);
		}
		MethodHandler mh = null;
		if (handler != null) {
			mh = new MethodHandler(this, method, parameters);
			Iterator<InvokeHandlerSet> iterator = handler.iterator();
			InvokeHandlerSet hs;
			while (iterator.hasNext()) {
				hs = iterator.next();
				mh.setInvokeHandlerSet(hs);
				handlerResult = hs.getInvokeHandler().before(mh);
				if (!mh.isChain())
					return handlerResult;
				mh.setChain(false);
			}
		}
		try {
			Object result = methodProxy.invokeSuper(object, parameters);
			if (handler != null) {
				mh.setOriginResult(result);
				Iterator<InvokeHandlerSet> iterator = handler.iterator();
				InvokeHandlerSet hs;
				while (iterator.hasNext()) {
					hs = iterator.next();
					mh.setInvokeHandlerSet(hs);
					handlerResult = hs.getInvokeHandler().after(mh);
					if (!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}

			}
			return result;
		} catch (Exception e) {
			if (handler != null) {
				Iterator<InvokeHandlerSet> iterator = handler.iterator();
				InvokeHandlerSet hs;
				while (iterator.hasNext()) {
					hs = iterator.next();
					mh.setInvokeHandlerSet(hs);
					handlerResult = hs.getInvokeHandler().error(mh, e);
					if (!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
			} else {
				log.error(e);
			}
			return null;
		}
	}

	public ProxyType getProxyType() {
		return proxyType;
	}
}