package com.YaNan.frame.core.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.ActionResults;
import com.YaNan.frame.core.servlet.annotations.PathVariable;
import com.YaNan.frame.core.servlet.annotations.RequestBody;
import com.YaNan.frame.core.servlet.annotations.RequestHeader;
import com.YaNan.frame.core.servlet.annotations.ActionResults.Result;
import com.YaNan.frame.core.servlet.annotations.CookieValue;
import com.YaNan.frame.core.servlet.annotations.RequestMapping;
import com.YaNan.frame.core.servlet.annotations.RequestParam;
import com.YaNan.frame.core.servlet.annotations.SessionAttributes;
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
			bean.setServletClass(method.getDeclaringClass());
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
		bean.setRequestMethod(requestMapping.method());
		/**
		 * 获取url映射
		 */
		String urlPath = requestMapping.value().trim().equals("")?"/"+method.getName():requestMapping.value().trim();
		if(parentRequestMaping!=null)
			urlPath = parentRequestMaping.value()+urlPath;
		String urlMapping =urlPath+"@"+requestMapping.method();;
		int varIndex = urlMapping.indexOf("{");
		while(varIndex>=0){
			int varEndex = urlMapping.indexOf("}");
			if(varEndex<0)
				throw new Exception("url mapping "+ urlPath
				+ " error,the Variable descriptors are not equal,please check :["+method+" or use @Action parse to action style]");
			urlMapping =urlMapping .substring(0,varIndex)+"*"+urlMapping .substring(varEndex+1);
			log.debug(urlMapping);
			varIndex = urlMapping.indexOf("{");
		}
		bean.setUrlmapping(urlMapping);
		bean.setPathRegex(urlPath);
		bean.setMethod(method);
		bean.setServletClass(method.getDeclaringClass());
		log.debug(urlPath);
		/**
		 * 添加方法的参数,格式为参数类型，参数注解
		 */
		if (method.getParameterCount()!=0) {
			Parameter[] paras = method.getParameters();
			for(int i = 0;i<paras.length;i++){
				ParameterDescription paraDes = null;
				PathVariable pava = paras[i].getAnnotation(PathVariable.class);
				if(pava!=null)
					paraDes=new ParameterDescription(pava.value(),ParameterDescription.ParameterType.PathVariable,pava.defaultValue());
				RequestParam repa=paras[i].getAnnotation(RequestParam.class);
				if(repa!=null)
					paraDes=new ParameterDescription(repa.value(),ParameterDescription.ParameterType.RequestParam,repa.defaultValue());
				CookieValue	coka=paras[i].getAnnotation(CookieValue.class);
				if(coka!=null)
					paraDes=new ParameterDescription(coka.value(),ParameterDescription.ParameterType.CookieValue,coka.defaultValue());
				SessionAttributes seat=paras[i].getAnnotation(SessionAttributes.class);
				if(seat!=null)
					paraDes=new ParameterDescription(seat.value(),ParameterDescription.ParameterType.SessionAttributes,seat.defaultValue());
				RequestHeader rehe = paras[i].getAnnotation(RequestHeader.class);
				if(rehe!=null)
					paraDes=new ParameterDescription(rehe.value(),ParameterDescription.ParameterType.RequestHeader,rehe.defaultValue());
				RequestBody	rebo=paras[i].getAnnotation(RequestBody.class);
				if(rebo!=null)
					paraDes=new ParameterDescription(rebo.value(),ParameterDescription.ParameterType.RequestBody,rebo.defaultValue());
				bean.addParameter(paras[i],paraDes);
				log.debug(paras[i].getName());
			}
		}
		/**
		 * 添加PathVariable的描述
		 */
		int Index = urlPath.indexOf("/");
		int count = 0;
		while(Index>=0){
			count ++;
			int Endex = urlPath.indexOf("/",Index+1);
			if(Endex<0){
				String regTmp = urlPath.substring(Index+1);
				if(regTmp.indexOf("{")>=0&&regTmp.indexOf("}")>=0)
					bean.addPathVariable(count, regTmp.substring(1,regTmp.length()-1));
				break;
			}
			String regTmp = urlPath.substring(Index+1,Endex);
			if(regTmp.indexOf("{")>=0&&regTmp.indexOf("}")>=0)
				bean.addPathVariable(count, regTmp.substring(1,regTmp.length()-1));
			Index =Endex;
		}
		return bean;
	}

}
