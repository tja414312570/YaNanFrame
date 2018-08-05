package com.YaNan.frame.plugin.autowired.property;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.text.ParseException;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.ClassLoader;

@Register(attribute = "*", description = "Property文件的属性的注入")
public class PropertyWiredHandler implements InvokeHandler {
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class);

	@Override
	public Object before(MethodHandler methodHandler) {
		// 遍历所有Field
		Field[] fields = methodHandler.getPlugsProxy().getProxyClass().getDeclaredFields();
		Object instance = methodHandler.getPlugsProxy().getProxyObject();
		Property property;
		String propertyName;
		String propertyValue;
		for (Field field : fields) {
			property = field.getAnnotation(Property.class);
			if (property != null) {
				propertyName = property.value();
				if (propertyName.equals(""))
					propertyName = field.getName();
				propertyValue = PropertyManager.getInstance().getProperty(propertyName);
				if (propertyValue == null && !property.defaultValue().equals(""))
					propertyValue = property.defaultValue();
				try {
					field.setAccessible(true);
					field.set(instance,
							field.getType().isArray()
									? ClassLoader.parseBaseTypeArray(field.getType(), propertyValue.split(","), null)
									: ClassLoader.parseBaseType(field.getType(), propertyValue, null));
					field.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException | ParseException e) {
					log.error("Error to process property \r\nat class:" + methodHandler.getPlugsProxy().getProxyClass()
							+ "\r\nat field:" + field + "\r\nat property:" + propertyName + "\r\nat property value:"
							+ propertyValue, e);
				}
			}
		}
		Parameter[] parameters = methodHandler.getMethod().getParameters();
		Object[] arguments = methodHandler.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			property = parameter.getAnnotation(Property.class);
			if (property != null) {
				propertyName = property.value();
				if (property != null) {
					propertyName = property.value();
					if (propertyName.equals(""))
						propertyName = parameter.getName();
					propertyValue = PropertyManager.getInstance().getProperty(propertyName);
					if (propertyValue == null && !property.defaultValue().equals(""))
						propertyValue = property.defaultValue();
					if (arguments[i] == null)
						try {
							arguments[i] = parameter.getType().isArray() ? ClassLoader
									.parseBaseTypeArray(parameter.getType(), propertyValue.split(","), null)
									: ClassLoader.parseBaseType(parameter.getType(), propertyValue, null);
						} catch (ParseException e) {
							log.error("Error to process property \r\nat class:"
									+ methodHandler.getPlugsProxy().getProxyClass() + "\r\nat parameter:" + parameter
									+ "\r\nat property:" + propertyName + "\r\nat property value:" + propertyValue, e);
						}
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
