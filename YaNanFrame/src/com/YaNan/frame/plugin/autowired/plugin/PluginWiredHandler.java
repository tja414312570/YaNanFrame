package com.YaNan.frame.plugin.autowired.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;

@Register(attribute="*",description="Property文件的属性的注入")
public class PluginWiredHandler implements InvokeHandler{
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class,PluginWiredHandler.class);

	@Override
	public void before(MethodHandler methodHandler) {
		//遍历所有Field
		Field[] fields = methodHandler.getPlugsProxy().getProxyClass().getDeclaredFields();
		Object instance = methodHandler.getPlugsProxy().getProxyObject();
		Service service;
		for(Field field : fields){
			service = field.getAnnotation(Service.class);
			if(service!=null){
				try {
					field.setAccessible(true);
					Class<?> type = field.getType();
					if(type.isArray()){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						field.set(instance, obj.toArray());
					}else if(type.getClass().equals(List.class)){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						field.set(instance, obj);
					}else{
						Object obj =  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
						field.set(instance, obj);
					}
					field.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					log.error("Error to process property \r\nat class:"
					+methodHandler.getPlugsProxy().getProxyClass()+"\r\nat field:",e);
				}
			}
		}
		Parameter[] parameters = methodHandler.getMethod().getParameters();
		Object[] arguments = methodHandler.getParameters();
		for(int i = 0;i<parameters.length;i++){
			Parameter parameter = parameters[i];
			service = parameter.getAnnotation(Service.class);
			if(service!=null){
				if(arguments[i]==null){
					Class<?> type = parameters[i].getType();
					if(type.isArray()){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else if(type.getClass().equals(List.class)){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else{
						Object obj =  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
						arguments[i] = obj;
					}
					
				}
			}
		}
	}

	@Override
	public void after(MethodHandler methodHandler) {
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

}
