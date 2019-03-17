package com.YaNan.frame.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.reflect.ClassLoader;

public class ParameterUtils {
	/**
	 * 获取参数的类型
	 * @param parmType
	 * @return
	 */
	public static Class<?> getParameterType(String parmType){
		parmType = parmType.trim().toLowerCase();
		switch(parmType){
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
	 * @param constructorList
	 * @param values
	 * @return 
	 */
	public static Constructor<?> getEffectiveConstructor(List<Constructor<?>> constructorList, List<? extends Object> values) {
		Constructor<?> constructor = null;
		//遍历所有的构造器
con:	for(Constructor<?> cons : constructorList){
			//获取构造器的参数类型的集合
			Class<?>[] parameterType = cons.getParameterTypes();
			//遍历构造器
			for(int i = 0;i<parameterType.length;i++){
				Class<?> type = parameterType[i];
				Object value = values.get(i);
				if(!isEffectiveParameter(type,value))
					continue con;
			}
			constructor = cons;
		}
		return constructor;
	}
	private static boolean isEffectiveParameter(Class<?> type, Object value) {
		try{
			if(value==null && (
					type==int.class || type == long.class || type == short.class ||
					type==boolean.class ||
					type==float.class || type==double.class
					))
				return false;
			if(type.equals(value.getClass())||ClassLoader.extendsOf(value.getClass(), type)||ClassLoader.implementsOf(value.getClass(),type))
				return true;
			if(type==int.class || type == long.class ||  type == short.class){
				Integer.valueOf(value.toString());
//				if(value.toString().indexOf(".")>-1||
//						value.getClass().equals(float.class)||value.getClass().equals(double.class)||
//						value.getClass().equals(Float.class)||value.getClass().equals(Double.class))
//					throw new RuntimeException("value "+value+" should be float or double or string rather than int");
			}else if(type==boolean.class){
				Boolean.valueOf(value.toString());
			}else if(type==float.class || type==double.class){
				Boolean.valueOf(value.toString());
			}else if(type==byte.class || type == Byte.class){
				if(value!=null)value.toString().getBytes();
			}else if(type==char.class || type == Character.class){
				if(value!=null)value.toString().toCharArray();
			}else if(type==String.class){
				if(value!=null)value.toString();
			}
			return true;
		}catch(Throwable t){
			t.printStackTrace();
			return false;
		}
	}
}
