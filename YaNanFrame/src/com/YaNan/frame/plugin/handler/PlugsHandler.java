package com.YaNan.frame.plugin.handler;                                                                                                                                                                       
                                                                                                                                                                                                          
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;                                                                                                                                                                          
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.PlugsFactory;                                                                                                                                                                           

/**
 * 组件实例处理器，用于对代理对象的处理，采用jdk方式                                                                                                                                                                                                     
 * @author yanan
 *
 */
public class PlugsHandler  implements InvocationHandler{    
	private Object proxyObject;
	private Class<?> proxyClass;
	private Class<?> interfaceClass;
	private Map<Method,InvokeHandler> handlerMapping;
	public Object getProxyObject() {
		return proxyObject;
	}
	public void setProxyObject(Object proxyObject) {
		this.proxyObject = proxyObject;
	}
	public Class<?> getProxyClass() {
		return proxyClass;
	}
	public void setProxyClass(Class<?> proxyClass) {
		this.proxyClass = proxyClass;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	private Method method;
	private Object[] args;
	public PlugsHandler(Object target, Class<?> mapperInterface) {
		super();
		this.proxyObject = target;
		this.proxyClass = target.getClass();
		this.interfaceClass=mapperInterface;
	}
	private void initHandler(){
		handlerMapping= new HashMap<Method,InvokeHandler>();
		Method[] methods = this.interfaceClass.getMethods();
		for(Method method :methods){
			InvokeHandler handler = PlugsFactory.getPlugsInstanceByAttributeStrict(InvokeHandler.class,this.interfaceClass.getName()+"."+method.getName());
			if(handler!=null)
				handlerMapping.put(method, handler);
		}
	}
	@Override                                                                                                                                                                                             
    public Object invoke(Object proxy, Method method, Object[] args){
		if(this.handlerMapping==null)this.initHandler();
		InvokeHandler handler = this.handlerMapping.get(method);
		MethodHandler mh=null;
		if(handler!=null){
			mh= new MethodHandler(this,method,args);
			mh.setHeaderResult(handler.before(mh));
			if(!mh.chain())
				return mh.getHeaderResult();
		}
		try {
			Object result = method.invoke(this.proxyObject, args);
			if(mh!=null){
				mh.setOriginResult(result);
				mh.setFootResult(handler.after(mh));
				if(!mh.chain())
					return mh.getFootResult();
			}
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if(mh!=null){
				return handler.error(mh,e);
			}else
				e.printStackTrace();
			return null;
		}     
    }                                                                                                
    @SuppressWarnings("unchecked")                                                                                                                                                                        
	public static <T> T newMapperProxy(Class<T> mapperInterface,Object target) { 
        ClassLoader classLoader = mapperInterface.getClassLoader();                                                                                                                                       
        Class<?>[] interfaces = new Class[]{mapperInterface};                                                                                                                                             
        PlugsHandler proxy = new PlugsHandler(target,mapperInterface);    
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);                                                                                                                                
      }
	public Object[] getRequestParameter() {
		return args;
	}
	public void setRequestParameter(Object[] args) {
		this.args = args;
	}                                                                                                                                                                                                   
}                                                                                                                                                                                                         