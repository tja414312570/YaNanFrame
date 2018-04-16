package com.YaNan.frame.core.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.ActionResults;
import com.YaNan.frame.core.servlet.annotations.PathVariable;
import com.YaNan.frame.core.servlet.annotations.ActionResults.Result;
import com.YaNan.frame.core.servlet.annotations.RequestMapping;
import com.YaNan.frame.core.servlet.annotations.RequestParam;
import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

public class ServletBeanBuilder {
	public static final String ACTION_STYLE="ACTION_STYLE";
	public static final String RESTFUL_STYLE="RESTFUL_STYLE";
	static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class,ServletBeanBuilder.class);

	public static ServletBean builder(Action action, Method method, RequestMapping parentRequestMaping) throws Exception {
		ServletBean bean = new ServletBean();
		if (method.getParameterCount()!=0) 
			throw new Exception("the Parameters at action method ["
					+ method
					+ "] should be null,please check :["+method+" or use @RequestMapping parse to restful style]");
			bean.setMethod(method);
			bean.setStyle(ACTION_STYLE);
			String urlPath = action.namespace();
			if(parentRequestMaping!=null)
				urlPath = parentRequestMaping.value()+urlPath;
			String actionName = action.value().equals("")?method.getName():action.value();
			urlPath =urlPath+actionName;
			bean.setUrlmapping(urlPath);
			bean.setPathRegex(urlPath);
			bean.setArgs(action.args());
			bean.setClassName(method.getDeclaringClass());
			bean.setOutputStream(action.output());
			bean.setDecode(action.decode());
			bean.setCorssOrgin(action.CorssOrgin());
			bean.setType(action.method());
			bean.setDescription(action.description());
			Result[] results =method.getAnnotationsByType(Result.class);
			if(results.length==0){
				ActionResults actionResults = method.getAnnotation(ActionResults.class);
				if(actionResults!=null)results = actionResults.value();
			}
			for (Result result : results) {
				ServletResult resultObj =new ServletResult();
				resultObj.setName(result.name());
				resultObj.setValue(result.value());
				resultObj.setMethod(result.method());
				bean.addResult(resultObj);
			}
		return bean;
	}

	public static ServletBean builder(RequestMapping requestMapping, Method method, RequestMapping parentRequestMaping) throws Exception {
		
		log.debug(requestMapping.toString());
		log.debug(method.toString());
		ServletBean bean = new ServletBean();
		bean.setStyle(RESTFUL_STYLE);
		/**
		 * 获取url映射
		 */
		String urlPath = requestMapping.value().equals("")?method.getName():requestMapping.value()+"@"+requestMapping.method();;
		if(parentRequestMaping!=null)
			urlPath = parentRequestMaping.value()+urlPath;
		String urlMapping =urlPath;
		int varIndex = urlMapping.indexOf("{");
		while(varIndex>=0){
			int varEndex = urlMapping.indexOf("}");
			if(varEndex<0)
				throw new Exception("url mapping "+ urlPath
				+ " error,the Variable descriptors are not equal,please check :["+method+" or use @Action parse to action style]");
			urlMapping =urlMapping .substring(0,varIndex)+"*"+urlMapping .substring(varEndex+1);
			log.debug(urlMapping);
			varIndex = urlMapping.indexOf("{");
			break;
		}
		bean.setUrlmapping(urlMapping);
		bean.setPathRegex(urlPath);
		bean.setMethod(method);
		bean.setClassName(method.getDeclaringClass());
		log.debug(urlPath);
		/**
		 * 添加方法的参数
		 */
		if (method.getParameterCount()!=0) {
			Parameter[] paras = method.getParameters();
			for(int i = 0;i<paras.length;i++){
				Object annotation = null;
				PathVariable path = paras[i].getAnnotation(PathVariable.class);
				RequestParam param = paras[i].getAnnotation(RequestParam.class);
				if(path!=null)
					annotation=path;
				if(param!=null)
					annotation=param;
				bean.addParameter(paras[i],annotation);
				log.debug(paras[i].toString());
				if(annotation!=null)
				log.debug(annotation.toString());
			}
		}
		return bean;
	}

}
