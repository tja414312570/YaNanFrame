package com.YaNan.frame.plugin.autowired.plugin;

import java.lang.reflect.Array;
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
import com.YaNan.frame.plugin.autowired.plugin.PluginWiredStack.WiredType;
import com.YaNan.frame.plugin.beans.BeanContainer;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.ClassLoader;

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
		PluginWiredStack.setWrideType(WiredType.METHOD_WRIDE);
		PluginWiredStack.setMethodHandler(methodHandler);
		Parameter[] parameters = methodHandler.getMethod().getParameters();
		Object[] arguments = methodHandler.getParameters();
		for(int i = 0;i<parameters.length;i++){
			Parameter parameter = parameters[i];
			service = parameter.getAnnotation(Service.class);
			if(service!=null){
				PluginWiredStack.setParameter(parameter);
				if(arguments[i]==null){
					Class<?> type = parameters[i].getType();
					if(!service.id().trim().equals("")){
						arguments[i] = BeanContainer.getContext().getBean(service.id());
						if(arguments[i]==null)
							throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" for parameter \""+parameter.getName()+"\" for method \""+methodHandler.getMethod().getName() +"\" for "+methodHandler.getPlugsProxy().getRegisterDescription().getRegisterClass());
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
							obj = BeanContainer.getContext().getBean(type);
						}
						if(obj==null)
							throw new PluginRuntimeException("could not found register or bean for parameter \""+parameter.getName()+"\" type \""+parameter.getType() +"\" for method \""+methodHandler.getMethod().getName() +"\" for "+methodHandler.getPlugsProxy().getRegisterDescription().getRegisterClass());
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
			PluginWiredStack.setWrideType(WiredType.FIELD_WRIDE);
			PluginWiredStack.setRegisterDescription(registerDescription,proxy,target,desc);
			Field field = desc.getField();
			Service service = desc.getAnnotation();
			try {
				field.setAccessible(true);
				Class<?> type = field.getType();
				if(!service.id().trim().equals("")){
					Object object = BeanContainer.getContext().getBean(service.id());
					if(object==null)
						throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" for field \""+field.getName() +"\" for "+registerDescription.getRegisterClass());
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
						obj = BeanContainer.getContext().getBean(type);
					}
					if(obj==null)
						throw new PluginRuntimeException("could not found register or bean for field \""+field.getName()+"\" type \""+field.getType() +"\" for "+registerDescription.getRegisterClass());
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
			Object... arguments) {
		Service service;
		PluginWiredStack.setWrideType(WiredType.CONSTRUCTOR_WRIDE);
		PluginWiredStack.setRegisterDescription(registerDescription,plugClass,constructor);
		//获取构造方法所有的参数
		Parameter[] parameters = constructor.getParameters();
		for(int i = 0;i<parameters.length;i++){
			Parameter parameter = parameters[i];
			//获取参数的Service注解
			service = parameter.getAnnotation(Service.class);
			if(service!=null){
				PluginWiredStack.setParameter(parameter);
				//如果参数不为Null时，不注入此参数
				if(arguments[i]==null){
					//获取参数的类型
					Class<?> type = parameters[i].getType();
					//如果Service的注解具有id属性，说明为Bean注入
					if(!service.id().trim().equals("")){
						//将该参数为设置为获取到的Bean
						arguments[i] = BeanContainer.getContext().getBean(service.id());
						if(arguments[i]==null)
							throw new PluginRuntimeException("could not found bean id for \""+service.id()+"\" type \""+parameter.getType() +"\" for parameter \""+parameter.getName() + "\" for construct \""+ constructor+"\" for "+registerDescription.getRegisterClass());
					//如果获取到的参数类型为数组
					}else if(type.isArray()){
						//获得数组的真实类型
						Class<?> typeClass = ClassLoader.getListGenericType(parameters[i]);
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(typeClass, service.attribute());
						Object[] arr = (Object[]) Array.newInstance(typeClass, obj.size());
						arguments[i] = obj.toArray(arr);
					}else if(type.getClass().equals(List.class)){
						//获取数组参数的类型
						Class<?> typeClass = ClassLoader.getListGenericType(parameters[i]);
						//获取服务返回的所有实现的实例
						List<?> obj = PlugsFactory.getPlugsInstanceListByAttribute(typeClass, service.attribute());
						arguments[i] = obj;
					}else{
						//如果以上都没有匹配到，则首先从服务里获取，没有获取到时重Bean容器里获取。
						Object obj = null;
						try{
							obj=  PlugsFactory.getPlugsInstanceByAttributeStrict(type,service.attribute());
						}catch(Throwable t){
							obj = BeanContainer.getContext().getBean(type);
						}
						if(obj==null)
							throw new PluginRuntimeException("could not found register or bean for parameter \""+parameter.getName()+"\" type \""+parameter.getType()  + "\" for construct \""+ constructor+"\" for "+registerDescription.getRegisterClass());
						arguments[i] = obj;
					}
				}
			}
		}
	}

	@Override
	public void after(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... arguments) {
	}

	@Override
	public void exception(RegisterDescription registerDescription, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		
	}
}
