package com.YaNan.frame.plugin.autowired.property;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.FieldDesc;
import com.YaNan.frame.plugin.PluginRuntimeException;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.ClassLoader;

@Support(Property.class)
@Register(attribute = "*", description = "Property文件的属性的注入")
public class PropertyWiredHandler implements InvokeHandler, InstanceHandler, FieldHandler {
	private static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class,
			PropertyWiredHandler.class);

	@Override
	public void before(MethodHandler methodHandler) {
		// 遍历所有Field
		Property property;
		String propertyName;
		String propertyValue;
		Parameter[] parameters = methodHandler.getMethod().getParameters();
		Object[] arguments = methodHandler.getParameters();
		for(Object obj : arguments)
			System.out.println(obj);
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			property = parameter.getAnnotation(Property.class);
			System.out.println(parameter+":"+property);
			if (property != null) {
				propertyName = property.value();
				if (property != null) {
					propertyName = property.value();
					if (propertyName.equals(""))
						propertyName = parameter.getName();
					propertyValue = PropertyManager.getInstance().getProperty(propertyName);
					try {
						if (propertyValue == null && property.defaultValue().equals(""))
							throw new RuntimeException("failed to autowired parameter ! property name \"" + propertyName
									+ "\" value is null\r\n at class " + methodHandler.getPlugsProxy().getProxyClass()
									+ "\r\n at parameter " + parameter.getName());
						if (propertyValue == null && !property.defaultValue().equals(""))
							propertyValue = property.defaultValue();
						if (arguments[i] == null)
							arguments[i] = parameter.getType().isArray() ? ClassLoader
									.parseBaseTypeArray(parameter.getType(), propertyValue.split(","), null)
									: ClassLoader.parseBaseType(parameter.getType(), propertyValue, null);
					} catch (Exception e) {
						log.error("Error to process property \r\nat class:"
								+ methodHandler.getPlugsProxy().getProxyClass() + "\r\nat parameter:" + parameter
								+ "\r\nat property:" + propertyName + "\r\nat property value:" + propertyValue, e);
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
		Property property = desc.getAnnotation();
		String propertyName = null;
		String propertyValue = null;
		propertyName = property != null ? property.value() : desc.getValue();
		if (propertyName.equals(""))
			propertyName = desc.getField().getName();
		try {
			propertyValue = PropertyManager.getInstance().getProperty(propertyName);
			if (propertyValue == null &&property!=null&& !property.defaultValue().equals(""))
				propertyValue = property.defaultValue();
			if (propertyValue == null)
				throw new RuntimeException("failed to autowired parameter ! property name \"" + propertyName
						+ "\" value is null\r\nat class : " + registerDescription.getRegisterClass().getName()
						+ "\r\nat field : " + desc.getField().getName());
			new ClassLoader(target).set(desc.getField(),
					desc.getField().getType().isArray()
							? ClassLoader.parseBaseTypeArray(desc.getField().getType(), propertyValue.split(","), null)
							: propertyValue);
		} catch (Exception e) {
			log.error("Error to process property ! \r\nat class : " + registerDescription.getRegisterClass().getName()
					+ "\r\nat field : " + desc.getField().getName() + "\r\nat property : " + propertyName
					+ "\r\nat property value : " + propertyValue, e);
		}
	}

	@Override
	public void before(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object... args) {
		Property property;
		String propertyName;
		String propertyValue;
		Parameter[] parameters = constructor.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			property = parameter.getAnnotation(Property.class);
			if (property != null) {
				propertyName = property.value();
				if (propertyName.equals(""))
					propertyName = parameter.getName();
				propertyValue = PropertyManager.getInstance().getProperty(propertyName);
				try {
					if (propertyValue == null && property.defaultValue().equals(""))
						throw new RuntimeException("failed to autowired parameter ! property name \"" + propertyName
								+ "\" value is null\r\n at class : " + registerDescription.getRegisterClass().getName()
								+ "\" value is null\r\n at constructor : "
								+ registerDescription.getRegisterClass().getName() + "\r\n at parameter : "
								+ parameter.getName());
					if (propertyValue == null && !property.defaultValue().equals(""))
						propertyValue = property.defaultValue();
					args[i] = parameter.getType().isArray()
							? ClassLoader.parseBaseTypeArray(parameter.getType(), propertyValue.split(","), null)
							: ClassLoader.parseBaseType(parameter.getType(), propertyValue, null);
				} catch (Exception e) {
					log.error("Error to process property ! \r\nat class : "
							+ registerDescription.getRegisterClass().getName()
							+ "\" value is null\r\n at constructor : "
							+ registerDescription.getRegisterClass().getName() + "\r\nat parameter : "
							+ parameter.getName() + "\r\nat property : " + propertyName + "\r\nat property value : "
							+ propertyValue, e);
				}
			}
		}
	}

	@Override
	public void after(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exception(RegisterDescription registerDescription, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		// TODO Auto-generated method stub
		
	}

}
