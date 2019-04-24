package com.YaNan.frame.plugin.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.RegisterDescription;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


/**
 * 组件实例处理器，用于对代理对象的处理，采用jdk方式
 * v1.1 支持cglib代理方式
 * 
 * @author yanan
 *
 */
public class ProxyHandler implements MethodInterceptor {
	private volatile Map<Method,Method> methods = new HashMap<Method,Method>();
	private RegisterDescription registerDescription;// 注册描述类
	private Object proxyObject;// 代理对象
	private Class<?> proxyClass;// 代理类
	private Class<?> interfaceClass;// 接口类
	private Object linkObject;//链接对象

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

	/**
	 * cglib proxy PlugsHandler constructor
	 * 
	 * @param proxyClass
	 * @param parameters
	 */
	public ProxyHandler(Class<?> proxyClass,Class<?>[] parameterType, Object[] parameters, RegisterDescription registerDescription,Object linkProxy) {
		this.proxyClass = proxyClass;
		this.linkObject = linkProxy;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(proxyClass);
		enhancer.setCallback(this);
		this.registerDescription = registerDescription;
		if (parameters.length == 0)
			this.proxyObject = enhancer.create();
		else
			this.proxyObject = enhancer.create(parameterType,
					parameters);
	}


	public static <T> T newCglibProxy(Class<?> proxyClass, RegisterDescription registerDescription,Class<?>[] parameterType,Object linkProxy,
			Object... parameters) {
		return new ProxyHandler(proxyClass, parameterType,parameters, registerDescription,linkProxy).getProxyObject();
	}

	@Override
	public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy)
			throws Throwable {
			Method linkMethod = this.methods.get(method);
			if(linkMethod==null){
				synchronized (this) {
					if(linkMethod==null){
						RegisterDescription linkRegister = this.registerDescription.getLinkRegister();
						linkMethod = linkRegister.getRegisterClass().getMethod(method.getName(), method.getParameterTypes());
						this.methods.put(method, linkMethod);
					}
				}
			}
			return linkMethod.invoke(linkObject, parameters);
	}
}