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
import com.YaNan.frame.plugin.beans.BeanContext;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;

/**
 * 组件注入
 * @author yanan
 *
 */
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
					if(!service.id().trim().equals("")){
						arguments[i] = BeanContext.getContext().getBean(service.id());
						if(arguments[i]==null)
							throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" at parameter \""+parameter.getName()+"\" at method \""+methodHandler.getMethod().getName() +"\" at "+methodHandler.getPlugsProxy().getRegisterDescription().getRegisterClass());
					}else if(type.isArray()){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else if(type.getClass().equals(List.class)){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else{
						Object obj = null;
						try{
							obj=  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
						}catch(Throwable t){
							obj = BeanContext.getContext().getBean(type);
						}
						if(obj==null)
							throw new PluginRuntimeException("could not found register or bean at parameter \""+parameter.getName()+"\" at method \""+methodHandler.getMethod().getName() +"\" at "+methodHandler.getPlugsProxy().getRegisterDescription().getRegisterClass());
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
				if(!service.id().trim().equals("")){
					Object object = BeanContext.getContext().getBean(service.id());
					if(object==null)
						throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" at field \""+field.getName() +"\" at "+registerDescription.getRegisterClass());
					field.set(target,object);
				}else if(type.isArray()){
					List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
					field.set(target, obj.toArray());
				}else if(type.getClass().equals(List.class)){
					List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
					field.set(target, obj);
				}else{
					Object obj = null;
					try{
						obj=  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
					}catch(Throwable t){
						obj = BeanContext.getContext().getBean(type);
					}
					if(obj==null)
						throw new PluginRuntimeException("could not found register or bean at field \""+field.getName() +"\" at "+registerDescription.getRegisterClass());
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
					if(!service.id().trim().equals("")){
						arguments[i] = BeanContext.getContext().getBean(service.id());
						if(arguments[i]==null)
							throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" at parameter \""+parameter.getName() + "\" at construct \""+ constructor+"\" at "+registerDescription.getRegisterClass());
					}else if(type.isArray()){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else if(type.getClass().equals(List.class)){
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(type, service.attribute());
						arguments[i] = obj;
					}else{
						Object obj = null;
						try{
							obj=  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
						}catch(Throwable t){
							obj = BeanContext.getContext().getBean(type);
						}
						if(obj==null)
							throw new PluginRuntimeException("could not found register or bean at parameter \""+parameter.getName() + "\" at construct \""+ constructor+"\" at "+registerDescription.getRegisterClass());
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
