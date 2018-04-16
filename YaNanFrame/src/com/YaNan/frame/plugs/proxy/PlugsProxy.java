package com.YaNan.frame.plugs.proxy;                                                                                                                                                                       
                                                                                                                                                                                                          
import java.lang.reflect.InvocationHandler;                                                                                                                                                               
import java.lang.reflect.Method;                                                                                                                                                                          
import java.lang.reflect.Proxy;                                                                                                                                                                           

                                                                                                                                                                                                          
                                                                                                                                                                                                          
/**                                                                                                                                                                                                       
 *  JDK动态代理代理类                                                                                                                                                                                            
 *                                                                                                                                                                                                        
 */                                                                                                                                                                                                       
public class PlugsProxy implements InvocationHandler {       
	private Object object;
	
    public PlugsProxy(Object object) {
		super();
		this.object = object;
	}
	@Override                                                                                                                                                                                             
    public Object invoke(Object proxy, Method method, Object[] args)                                                                                                                                      
            throws Throwable {                                                                                                                                                                            
    	return method.invoke(this.object, args);                                                                                                                                                                             
    }                                                                                                
    @SuppressWarnings("unchecked")                                                                                                                                                                        
	public static <T> T newMapperProxy(Class<T> mapperInterface,Object target) { 
        ClassLoader classLoader = mapperInterface.getClassLoader();                                                                                                                                       
        Class<?>[] interfaces = new Class[]{mapperInterface};                                                                                                                                             
        PlugsProxy proxy = new PlugsProxy(target);                                                                                                                                                            
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);                                                                                                                                
      }                                                                                                                                                                                                   
}                                                                                                                                                                                                         