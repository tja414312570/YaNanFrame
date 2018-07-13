package com.YaNan.frame.plugin.autowired.property;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;

@Register(attribute="*",description="Property文件的属性的注入")
public class PropertyWiredHandler implements InvokeHandler{
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class);

	@Override
	public Object before(MethodHandler methodHandler) {
		//遍历所有Field
		Field[] fields = methodHandler.getPlugsProxy().getProxyClass().getDeclaredFields();
		Object instance = methodHandler.getPlugsProxy().getProxyObject();
		Property property;
		String propertyName;
		String propertyValue;
		for(Field field : fields){
			property = field.getAnnotation(Property.class);
			if(property!=null){
				propertyName = property.value();
				if(propertyName.equals(""))
					propertyName = field.getName();
				propertyValue =  PropertyManager.getInstance().getProperty(propertyName);
				if(propertyValue==null&&!property.defaultValue().equals(""))
					propertyValue = property.defaultValue();
				try {
					field.setAccessible(true);
					field.set(instance, propertyValue);
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
			property = parameter.getAnnotation(Property.class);
			if(property!=null){
				propertyName = property.value();
				if(property!=null){
					propertyName = property.value();
					if(propertyName.equals(""))
						propertyName = parameter.getName();
					propertyValue =  PropertyManager.getInstance().getProperty(propertyName);
					if(propertyValue==null&&!property.defaultValue().equals(""))
						propertyValue = property.defaultValue();
					if(arguments[i]==null)
						arguments[i] = propertyValue;
				}
			}
		}
		methodHandler.chain();
		return null;
	}

	@Override
	public Object after(MethodHandler methodHandler) {
		methodHandler.chain();
		return null;
	}

	@Override
	public Object error(MethodHandler methodHandler, Exception e) {
		methodHandler.chain();
		return null;
	}

}
