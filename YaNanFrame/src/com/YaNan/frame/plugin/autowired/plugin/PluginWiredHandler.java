package com.YaNan.frame.plugin.autowired.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

import com.YaNan.frame.plugin.FieldDesc;
import com.YaNan.frame.plugin.PluginRuntimeException;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.plugin.handler.PlugsHandler;

@Support(Service.class)
@Register(attribute="*",description="Service服务的注入")
public class PluginWiredHandler implements InvokeHandler,FieldHandler,InstanceHandler{

	@Override
	public void before(MethodHandler methodHandler) {
		Service service;
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
						PlugsHandler handler = PlugsFactory.getPlugsHandler(obj);
						handler.setAttribute("INVOKER_INSTANCE", methodHandler.getPlugsProxy());
						handler.setAttribute("INVOKER_METHOD", methodHandler.getMethod());
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

	@Override
	public void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args) {
			Field field = desc.getField();
			Service service = desc.getAnnotation();
			try {
				field.setAccessible(true);
				Class<?> type = field.getType();
				if(type.isArray()){
					List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
					field.set(target, obj.toArray());
				}else if(type.getClass().equals(List.class)){
					List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
					field.set(target, obj);
				}else{
					Object obj =  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
					PlugsHandler handler = PlugsFactory.getPlugsHandler(obj);
					PlugsHandler invokeHandler = PlugsFactory.getPlugsHandler(target);
					handler.setAttribute("INVOKER_INSTANCE", invokeHandler.getProxyObject());
					handler.setAttribute("INVOKER_FIELD",field);
					field.set(target, obj);
				}
				field.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("failed to autowired service ! "
						+ "\"service is null\r\nat class : " + registerDescription.getRegisterClass().getName()
						+ "\r\nat field : " + desc.getField().getName());
			}
	}

	@Override
	public void before(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object... args) {
		
	}

	@Override
	public void after(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... arguments) {
		Service service;
		Parameter[] parameters = constructor.getParameters();
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
						PlugsHandler handler = PlugsFactory.getPlugsHandler(obj);
						PlugsHandler invokeHandler = PlugsFactory.getPlugsHandler(proxyObject);
						handler.setAttribute("INVOKER_INSTANCE", invokeHandler.getProxyObject());
						handler.setAttribute("INVOKER_CONSTRUCTOR",constructor);
						arguments[i] = obj;
					}
					
				}
			}
		}
	}

	@Override
	public void exception(RegisterDescription registerDescription, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		
	}
}
