package com.YaNan.frame.plugin.handler;                                                                                                                                                                       
                                                                                                                                                                                                          
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;                                                                                                                                                                          
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.interfacer.PlugsListener;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;                                                                                                                                                                           

/**
 * 组件实例处理器，用于对代理对象的处理，采用jdk方式                                                                                                                                                                                                     
 * @author yanan
 *
 */
public class PlugsHandler  implements InvocationHandler,PlugsListener,MethodInterceptor{    
	public static enum ProxyType{
		JDK,CGLIB
	}
	private Object proxyObject;//代理对象
	private Class<?> proxyClass;//代理类
	private Class<?> interfaceClass;//接口类
	private Map<Method,InvokeHandler> handlerMapping;//方法拦截器
	private ProxyType proxyType= ProxyType.JDK;//代理模式
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class,PlugsHandler.class);
	@SuppressWarnings("unchecked")
	public <T> T getProxyObject() {
		return (T) proxyObject;
	}
	public Class<?> getProxyClass() {
		return proxyClass;
	}
	/**
	 * jdk 模式的PlugsHandler构造器
	 * @param target
	 * @param mapperInterface
	 */
	public PlugsHandler(Object target, Class<?> mapperInterface) {
		super();
		this.proxyObject = target;
		this.proxyClass = target.getClass();
		this.interfaceClass=mapperInterface;
	}
	/**
	 * cglib 模式的PlugsHandler构造器
	 * @param proxyClass
	 * @param parameters
	 */
	public PlugsHandler(Class<?> proxyClass, Object[] parameters) {
		this.proxyClass = proxyClass;
		this.proxyType = ProxyType.CGLIB;
		Enhancer enhancer =new Enhancer();  
        enhancer.setSuperclass(proxyClass);  
        enhancer.setCallback(this);  
        if(parameters.length==0)
			this.proxyObject=enhancer.create();
		else
			this.proxyObject = enhancer.create(com.YaNan.frame.reflect.ClassLoader.getParameterTypes(parameters), parameters);
	}
	private void initHandlerMapping(Class<?> clzz){
		if(clzz!=null){
			Method[] methods = clzz.getMethods();
			for(Method method :methods){
				InvokeHandler handler = PlugsFactory.getPlugsInstanceByAttributeStrict(InvokeHandler.class,clzz.getName()+"."+method.getName());
				if(handler!=null)
					handlerMapping.put(method, handler);
			}
		}
	}
	private void initHandler(){
		handlerMapping= new HashMap<Method,InvokeHandler>();
		this.initHandlerMapping(interfaceClass);
		this.initHandlerMapping(proxyClass);
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
		} catch (Exception e) {
			if(mh!=null){
				return handler.error(mh,e);
			}else
				log.error(e);
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
    public static Object newCglibProxy(Class<?> proxyClass,Object...parameters) {
        PlugsHandler proxy = new PlugsHandler(proxyClass,parameters);
        return proxy.getProxyObject();
	}    
	@Override
	public void excute(PlugsFactory plugsFactory) {
		log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class,PlugsHandler.class);
	}
	@Override
	public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {
		if(this.handlerMapping==null)this.initHandler();
		InvokeHandler handler = this.handlerMapping.get(method);
		MethodHandler mh=null;
		if(handler!=null){
			mh= new MethodHandler(this,method,parameters);
			mh.setHeaderResult(handler.before(mh));
			if(!mh.chain())
				return mh.getHeaderResult();
		}
		try {
			Object result = methodProxy.invokeSuper(object, parameters);
			if(mh!=null){
				mh.setOriginResult(result);
				mh.setFootResult(handler.after(mh));
				if(!mh.chain())
					return mh.getFootResult();
			}
			return result;
		} catch (Exception e) {
			if(mh!=null){
				return handler.error(mh,e);
			}else
				log.error(e);
			return null;
		}    
	}
	public ProxyType getProxyType() {
		return proxyType;
	}
}                                                                                                                                                                                                         