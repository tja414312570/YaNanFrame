package com.YaNan.frame.plugin.handler;                                                                                                                                                                       
                                                                                                                                                                                                          
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;                                                                                                                                                                          
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private Map<Method,InvokeHandlerSet> handlerMapping;//方法拦截器
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
				InvokeHandlerSet ihs = handlerMapping.get(method);
				List<InvokeHandler> handlers = PlugsFactory.getPlugsInstanceListByAttribute(InvokeHandler.class,clzz.getName()+"."+method.getName());
				for(int i = 0,len = handlers.size();i<len;i++){
					if(ihs==null){
						ihs = new InvokeHandlerSet(handlers.get(i));
						handlerMapping.put(method, ihs);
					}
					else
						ihs.getLast().addInvokeHandlerSet(new InvokeHandlerSet(handlers.get(i)));
				}
			}
		}
	}
	private void initHandler(){
		handlerMapping= new HashMap<Method,InvokeHandlerSet>();
		this.initHandlerMapping(interfaceClass);
		this.initHandlerMapping(proxyClass);
	}
	@Override                                                                                                                                                                                             
    public Object invoke(Object proxy, Method method, Object[] args){
		if(this.interfaceClass.equals(InvokeHandler.class))
		try {
			return method.invoke(this.proxyObject, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		Object handlerResult;
		if(this.handlerMapping==null)this.initHandler();
		InvokeHandlerSet handler = this.handlerMapping.get(method);
		MethodHandler mh= new MethodHandler(this,method,args);
		if(handler!=null){
			Iterator<InvokeHandlerSet> iterator =  handler.iterator();
			InvokeHandlerSet hs ;
			while(iterator.hasNext()){
				hs = iterator.next();
				handlerResult = hs.getInvokeHandler().before(mh);
				if(!mh.isChain())
					return handlerResult;
				mh.setChain(false);
			}
			
		}
		try {
			Object result = method.invoke(this.proxyObject, args);
			mh.setOriginResult(result);
			if(handler!=null){
				Iterator<InvokeHandlerSet> iterator =  handler.iterator();
				InvokeHandlerSet hs ;
				while(iterator.hasNext()){
					hs = iterator.next();
					handlerResult = hs.getInvokeHandler().after(mh);
					if(!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			if(handler!=null){
				Iterator<InvokeHandlerSet> iterator =  handler.iterator();
				InvokeHandlerSet hs ;
				while(iterator.hasNext()){
					hs = iterator.next();
					handlerResult = hs.getInvokeHandler().error(mh,e);
					if(!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
			}else
				log.error(e);
			return null;
		}     
    }                                                                                                
    @SuppressWarnings("unchecked")                                                                                                                                                                        
	public static <T> T newMapperProxy(Class<T> mapperInterface,Object target) { 
        ClassLoader classLoader = mapperInterface.getClassLoader();                                                                                                                                       
        Class<?>[] interfaces = new Class[1];
        interfaces[0] = mapperInterface;
        WeakReference<PlugsHandler> wr =  new WeakReference<PlugsHandler>(new PlugsHandler(target,mapperInterface));
        return (T) Proxy.newProxyInstance(classLoader, interfaces, wr.get());                                                                                                                                
      }
    public static Object newCglibProxy(Class<?> proxyClass,Object...parameters) {
        return new WeakReference<PlugsHandler>(new PlugsHandler(proxyClass,parameters)).get().getProxyObject();
	}    
	@Override
	public void excute(PlugsFactory plugsFactory) {
		log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class,PlugsHandler.class);
	}
	@Override
	public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {
		if(this.handlerMapping==null)this.initHandler();
		Object handlerResult;
		InvokeHandlerSet handler = this.handlerMapping.get(method);
		MethodHandler mh= new MethodHandler(this,method,parameters);
		if(handler!=null){
			Iterator<InvokeHandlerSet> iterator =  handler.iterator();
			InvokeHandlerSet hs ;
			while(iterator.hasNext()){
				hs = iterator.next();
				handlerResult = hs.getInvokeHandler().before(mh);
				if(!mh.isChain())
					return handlerResult;
				mh.setChain(false);
			}
		}
		try {
			Object result = methodProxy.invokeSuper(object, parameters);
			if(handler!=null){
				Iterator<InvokeHandlerSet> iterator =  handler.iterator();
				InvokeHandlerSet hs ;
				while(iterator.hasNext()){
					hs = iterator.next();
					handlerResult = hs.getInvokeHandler().after(mh);
					if(!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
				
			}
			return result;
		} catch (Exception e) {
			if(handler!=null){
				Iterator<InvokeHandlerSet> iterator =  handler.iterator();
				InvokeHandlerSet hs ;
				while(iterator.hasNext()){
					hs = iterator.next();
					handlerResult = hs.getInvokeHandler().error(mh, e);
					if(!mh.isChain())
						return handlerResult;
					mh.setChain(false);
				}
			}else
				log.error(e);
			return null;
		}    
	}
	public ProxyType getProxyType() {
		return proxyType;
	}
}                                                                                                                                                                                                         