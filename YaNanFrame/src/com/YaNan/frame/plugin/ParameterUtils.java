package com.YaNan.frame.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.beans.BeanContainer;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.ClassHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

public class ParameterUtils {
	public static class MethodDesc {
		private Method method;
		private Object[] parameter;

		public MethodDesc(Method method, Object[] parameter) {
			super();
			this.method = method;
			this.parameter = parameter;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object[] getParameter() {
			return parameter;
		}

		public void setParameter(Object[] parameter) {
			this.parameter = parameter;
		}
	}

	/**
	 * 获取参数的类型
	 * 
	 * @param parmType
	 * @return
	 */
	public static Class<?> getParameterType(String parmType) {
		parmType = parmType.trim().toLowerCase();
		switch (parmType) {
		case "string":
			return String.class;
		case "int":
			return int.class;
		case "integer":
			return int.class;
		case "float":
			return float.class;
		case "double":
			return double.class;
		case "boolean":
			return boolean.class;
		case "file":
			return File.class;
		case "ref":
			return Service.class;
		}
		try {
			return Class.forName(parmType);
		} catch (ClassNotFoundException e) {
			throw new PluginInitException(e);
		}
	}

	/**
	 * 获取一个有效的构造器
	 * 
	 * @param constructorList
	 * @param values
	 * @return
	 */
	public static Constructor<?> getEffectiveConstructor(List<Constructor<?>> constructorList,
			List<? extends Object> values) {
		Constructor<?> constructor = null;
		// 遍历所有的构造器
		con: for (Constructor<?> cons : constructorList) {
			// 获取构造器的参数类型的集合
			Class<?>[] parameterType = cons.getParameterTypes();
			// 遍历构造器
			for (int i = 0; i < parameterType.length; i++) {
				Class<?> type = parameterType[i];
				Object value = values.get(i);
				if (!isEffectiveParameter(type, value))
					continue con;
			}
			constructor = cons;
		}
		return constructor;
	}
	/**
	 * 获取一个合适的方法。匹配规则是参数可以转换为对应的参数
	 * @param methods
	 * @param parameters
	 * @return
	 */
	public static Method getEffectiveMethod(Method[] methods,
			Object[] parameters) {
		Method method = null;
		// 遍历所有的构造器
		con: for (Method cons : methods) {
			if(cons.getParameterCount()!=parameters.length)
				continue con;
			// 获取构造器的参数类型的集合
			Class<?>[] parameterType = cons.getParameterTypes();
			// 遍历构造器
			for (int i = 0; i < parameterType.length; i++) {
				Class<?> type = parameterType[i];
				Object value = parameters[i];
				if (!isEffectiveParameter(type, value))
					continue con;
			}
			method = cons;
		}
		return method;
	}

	private static boolean isEffectiveParameter(Class<?> type, Object value) {
		try {
			if (value == null && (type == int.class || type == long.class || type == short.class
					|| type == boolean.class || type == float.class || type == double.class))
				return false;
			if (type.equals(value.getClass()) || ClassLoader.extendsOf(value.getClass(), type)
					|| ClassLoader.implementsOf(value.getClass(), type))
				return true;
			if (type == int.class || type == long.class || type == short.class) {
				Integer.valueOf(value.toString());
				// if(value.toString().indexOf(".")>-1||
				// value.getClass().equals(float.class)||value.getClass().equals(double.class)||
				// value.getClass().equals(Float.class)||value.getClass().equals(Double.class))
				// throw new RuntimeException("value "+value+" should be float
				// or double or string rather than int");
			} else if (type == boolean.class) {
				Boolean.valueOf(value.toString());
			} else if (type == float.class || type == double.class) {
				Boolean.valueOf(value.toString());
			} else if (type == byte.class || type == Byte.class) {
				if (value != null)
					value.toString().getBytes();
			} else if (type == char.class || type == Character.class) {
				if (value != null)
					value.toString().toCharArray();
			} else if (type == String.class) {
				if (value != null)
					value.toString();
			}
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	/**
	 * 将config转化为参数和方法的集
	 * @param config
	 * @param register
	 * @return
	 */
	public static MethodDesc[] transformToMethod(Config config, RegisterDescription register) {
		ClassHelper helper = ClassHelper.getClassHelper(register.getRegisterClass());
		MethodDesc[] methodDescs = new MethodDesc[config.entrySet().size()];
		int index = 0;
		// 获取config的第一个key
		Iterator<Entry<String, ConfigValue>> citerator = config.entrySet().iterator();
		while (citerator.hasNext()) {
			Entry<String, ConfigValue> centry = citerator.next();
			ConfigValue configValue = centry.getValue();
			// 获取到数据的key作为方法名
			String methodName = centry.getKey();
			// 判断实体的数据类型
			if (configValue.valueType().equals(ConfigValueType.OBJECT)) {
				// 获取到config
				Set<Entry<String, ConfigValue>> entrySet = config.getConfig(methodName).entrySet();
				int argSize = entrySet.size();
				Class<?>[] parameterTypes = new Class<?>[argSize];
				Object[] parameters = new Object[argSize];
				int i = 0;
				Iterator<Entry<String, ConfigValue>> argIterator = entrySet.iterator();
				while (argIterator.hasNext()) {
					Entry<String, ConfigValue> entry = argIterator.next();
					parameterTypes[i] = ParameterUtils.getParameterType(entry.getKey());
					Object value = entry.getValue().unwrapped();
					parameters[i] = getParameter(parameterTypes[i], value);
					i++;
				}
				Method method = helper.getMethod(methodName, parameterTypes);
				if (method == null)
					throw new PluginInitException("could not found method name is \"" + methodName + "\" for parameter "
							+ entrySet + " at bean id " + register.getBeanId());
				methodDescs[index] = new MethodDesc(method,parameters);
			} else if (configValue.valueType().equals(ConfigValueType.NULL)) {
				Method method = helper.getMethod(methodName);
				if (method == null)
					throw new PluginInitException("could not found no parameter method name is \"" + methodName
							+ "\" for at bean id " + register.getBeanId());
				methodDescs[index] = new MethodDesc(method,new Object[0]);
			} else if (configValue.valueType().equals(ConfigValueType.LIST)) {
				if (config.isConfigList(methodName)) {
					List<? extends Config> values = config.getConfigList(methodName);
					Object[] parameters = new Object[values.size()];
					Class<?>[] parameterTypes = new Class<?>[values.size()];
					for (int i = 0; i < values.size(); i++) {
						Config conf = values.get(i);
						Iterator<Entry<String, Object>> iterator = conf.simpleObjectEntrySet().iterator();
						if (iterator.hasNext()) {
							Entry<String, Object> entry = iterator.next();
							String key = entry.getKey();
							Object value = entry.getValue();
							parameterTypes[i] = ParameterUtils.getParameterType(key);
							parameters[i] = getParameter(parameterTypes[i], value);
						}
					}
					Method method = helper.getMethod(methodName, parameterTypes);
					if (method == null)
						throw new PluginInitException("could not found method name is \"" + methodName
								+ "\" for parameter " + values + " at bean id " + register.getBeanId());
					methodDescs[index] = new MethodDesc(method,parameters);
				} else {// 普通数据列表
					List<? extends Object> values = config.getSimpleObjectList(methodName);
					Object[] parameters = new Object[values.size()];
					Class<?>[] parameterTypes = new Class<?>[values.size()];
					for (int i = 0; i < values.size(); i++) {
						parameters[i] = values.get(i);
					}
					Method method = getEffectiveMethod(helper.getMethods(), parameters);//helper.getMethod(methodName, parameterTypes);
					if (method == null){
						String parameterType = "[";
						for(int i = 0;i<parameterTypes.length;i++)
							parameterType=parameterType+parameterTypes[i]+(i<parameterTypes.length-1?",":"]");
						throw new PluginInitException("could not found method name is \"" + methodName
								+ "\" for parameter " + values+" types " +parameterType+ " at bean id " + register.getBeanId());
					}
					parameterTypes = method.getParameterTypes();
					for (int i = 0; i < values.size(); i++) {
						parameters[i] = ClassLoader.castType(parameters[i], parameterTypes[i]);
					}
					methodDescs[index] = new MethodDesc(method,parameters);
				}
			} else {
				Object value = config.getSimpleObject(methodName);
				Method method = helper.getMethod(methodName, value.getClass());
				if (method == null)
					throw new PluginInitException("could not found method name is \"" + methodName + "\" for parameter "
							+ value + " at bean id " + register.getBeanId());
				methodDescs[index] = new MethodDesc(method,new Object[]{value});
			}
			index++;
		}
		return methodDescs;
	}

	public static Object getParameter(Class<?> type,Object value) {
		if (type.equals(File.class)) {// 文件类型特俗处理
			File file;
			try {
				List<File> files = ResourceManager.getResource(value.toString());
				file = files.get(0);

			} catch (Throwable t) {
				file = new File(ResourceManager.getPathExress(value.toString()));
			}
			value = file;
		} else if (type.equals(Service.class)) {// bean类型
			String beanId = value.toString();
			value = BeanContainer.getContext().getBean(beanId);
		} else {
			value = ClassLoader.castType(value,
					type);
		}
		return value;
	}

	public static ParameterInfo getParameterInfo(Config config) {
		ParameterInfo info = null;
		Class<?> type;
		Object value;
		if (config.isList("args")) {
			// 判断是否是conf集合
			if (config.isConfigList("args")) {
				List<? extends Config> values = config.getConfigList("args");
				info = new ParameterInfo(values.size());
				for (int i = 0; i < values.size(); i++) {
					Config conf = values.get(i);
					Iterator<Entry<String, Object>> iterator = conf.simpleObjectEntrySet()
							.iterator();
					if (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						String key = entry.getKey();
						type = ParameterUtils.getParameterType(key);
						value = getParameter(type,entry.getValue());
					}
					info.addParameter( values.get(i).getClass(),values.get(i));
				}
			} else {// 普通数据列表
				List<? extends Object> values = config.getSimpleObjectList("args");
				info = new ParameterInfo(values.size());
				for (int i = 0; i < values.size(); i++) 
					info.addParameter( values.get(i).getClass(),values.get(i));
			}
		}else {// 单参数
			ConfigValueType valueType = config.getType("args");
			// 如果是config类型
			if (valueType == ConfigValueType.OBJECT) {
				// 获取到config
				// 获取参数的数量
				Set<Entry<String, ConfigValue>> entrySet = config.getConfig("args").entrySet();
				int argSize = entrySet.size();
				info = new ParameterInfo(argSize);
				// 排除数量不同的构造器
				Iterator<Entry<String, ConfigValue>> argIterator = entrySet.iterator();
				while (argIterator.hasNext()) {
					Entry<String, ConfigValue> entry = argIterator.next();
					type = ParameterUtils.getParameterType(entry.getKey());
					value = getParameter(type,entry.getValue().unwrapped());
					info.addParameter(type, value);
				}
			} else {
				info = new ParameterInfo(1);
				value = config.getSimpleObject("args");
				info.addParameter(value.getClass(),value);
			}
		}
		return info;
	}
}
