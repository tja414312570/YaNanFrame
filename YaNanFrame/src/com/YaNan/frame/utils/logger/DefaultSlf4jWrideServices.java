package com.YaNan.frame.utils.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.autowired.plugin.PluginWiredStack;

/**
 * 默认SLS4J日志对象生成服务
 * @author yanan
 *
 */
public class DefaultSlf4jWrideServices{
	//此类用来缓存日志对象
	private static Map<Class<?>,Logger> logMapping = new HashMap<Class<?>,Logger>();
	/**
	 * 通过此方法生成对应的服务，即不同Logger
	 * @return
	 */
	public Logger getLogger(){
		Logger logger;
		//判断是否通过注入的方式调用此方法，
		if(PluginWiredStack.getStack().getWrideType()!=null){
			//通过PluginWiredStack拿到依赖此对象的原始对象的信息
			Class<?> invokeClass = PluginWiredStack.getStack().getRegisterDescription().getRegisterClass();
			logger = logMapping.get(invokeClass);
			if(logger==null){
				logger = LoggerFactory.getLogger(invokeClass);
				logMapping.put(invokeClass, logger);
			}
		}else
			logger = LoggerFactory.getLogger(DefaultSlf4jWrideServices.class);
		return logger;
	}
}
