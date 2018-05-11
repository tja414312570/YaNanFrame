package com.YaNan.frame.RPC.ServletSupport;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.YaNan.frame.RPC.Implements.RequestType;
import com.YaNan.frame.RPC.TokenSupport.RPCToken;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.session.annotation.TokenObject;
import com.YaNan.frame.servlets.session.servletSupport.TokenServlet;
/**
 * 
 * 
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public abstract class DistributedTokenServlet extends TokenServlet{
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, DistributedTokenServlet.class);
	public void doOther(ClassLoader loader){
		Field[] fields = loader.getDeclaredFields();
		for(Field field :fields){
			if(field.getAnnotation(TokenObject.class)!=null){
				Class<?> cls = field.getType();
				String method = ClassLoader.createFieldSetMethod(field);
					if (loader.hasMethod(method,cls))
					try {
						if(TokenContext.get(cls)!=null){
							loader.set(field.getName(),TokenContext.get(cls));
						}else{
							TokenObject tokenObj = field.getAnnotation(TokenObject.class);
							String serviceName = tokenObj.ServiceName();
							int soaType = tokenObj.Type();
							RPCToken request = RPCService.getRPCToken(TokenContext,serviceName,cls,soaType,RequestType.GET);
							Object rpcResult = request.request();
							if(rpcResult==null)
								continue;
							TokenContext.set(cls, rpcResult);
							loader.set(field.getName(),rpcResult);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						log.error(e);
					}
				}
			}
	}
}
