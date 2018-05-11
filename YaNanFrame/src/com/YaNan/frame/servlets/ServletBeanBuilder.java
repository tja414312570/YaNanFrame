package com.YaNan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlet.view.ViewSolver;
import com.YaNan.frame.servlets.annotations.Action;
import com.YaNan.frame.servlets.annotations.ActionResults;
import com.YaNan.frame.servlets.annotations.CookieValue;
import com.YaNan.frame.servlets.annotations.PathVariable;
import com.YaNan.frame.servlets.annotations.RequestBody;
import com.YaNan.frame.servlets.annotations.RequestHeader;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.annotations.RequestParam;
import com.YaNan.frame.servlets.annotations.SessionAttributes;
import com.YaNan.frame.servlets.annotations.ActionResults.Result;

public class ServletBeanBuilder implements ServletMappingBuilder{
	public static final String ACTION_STYLE="ACTION_STYLE";
	public static final String RESTFUL_STYLE="RESTFUL_STYLE";
	static Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class,ServletBeanBuilder.class);

	public static boolean builderAction(Action action, Method method, RequestMapping parentRequestMaping, ServletMapping servletMannager){
		ServletBean bean = new ServletBean();
		if (method.getParameterCount()!=0) 
			log.error("the Parameters at action method ["
					+ method
					+ "] should be null,please check :["+method+" or use @RequestMapping parse to restful style]");
			bean.setMethod(method);
			bean.setStyle(ACTION_STYLE);
			String urlPath = action.namespace();
			if(parentRequestMaping!=null){
				String namespace = parentRequestMaping.value().trim();
				if(namespace.equals(""))
					namespace = "/"+method.getDeclaringClass().getSimpleName();
				else if(namespace.equals("/"))
					namespace="";
				urlPath = namespace+urlPath;
			}
			String actionName = action.value().equals("")?method.getName():action.value();
			urlPath =urlPath+actionName+".do";
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
			servletMannager.add(bean);
		return true;
	}
	public static boolean builderRestful(RequestMapping requestMapping, Method method, RequestMapping parentRequestMaping, ServletMapping servletMannager){
		if(requestMapping.method().length==0)
			return true;
		for(int type : requestMapping.method()){
			try {
				ServletBean bean = builder(requestMapping,method,parentRequestMaping,type);
				servletMannager.add(bean);
			} catch (Exception e) {
				log.error(e);
				continue;
			}
		}
		return true;
	}
	public static ServletBean builder(RequestMapping requestMapping, Method method, RequestMapping parentRequestMaping,int type) throws Exception {
		ServletBean bean = new ServletBean();
		bean.setStyle(RESTFUL_STYLE);
		bean.setRequestMethod(type);
		/**
		 * 获取url映射
		 */
		String urlPath = requestMapping.value().trim().equals("")?"/"+method.getName():requestMapping.value().trim();
		bean.setViewSolver(requestMapping.viewSolver());
		if(parentRequestMaping!=null){
			String namespace = parentRequestMaping.value().trim();
			if(namespace.equals(""))//如果父类命名空间为空时，父类命名空间为当前类名
				namespace = "/"+method.getDeclaringClass().getSimpleName();
			else if(namespace.equals("/"))//如果父类命名空间为/时，设置命名空间为空，因为子命名空间可能包含了/
				namespace="";
			urlPath = namespace+urlPath;
			if(!parentRequestMaping.viewSolver().equals(ViewSolver.class)&&bean.getViewSolver().equals(ViewSolver.class))//如果父类有实例的视图解析器
				bean.setViewSolver(parentRequestMaping.viewSolver());
		}
		String urlMapping =urlPath+"@"+type;
		int varIndex = urlMapping.indexOf("{");
		while(varIndex>=0){
			int varEndex = urlMapping.indexOf("}");
			if(varEndex<0)
				throw new Exception("url mapping "+ urlPath
				+ " error,the Variable descriptors are not equal,please check :["+method+" or use @Action parse to action style]");
			urlMapping =urlMapping .substring(0,varIndex)+"*"+urlMapping .substring(varEndex+1);
			varIndex = urlMapping.indexOf("{");
		}
		bean.setUrlmapping(urlMapping);
		bean.setPathRegex(urlPath);
		bean.setMethod(method);
		bean.setServletClass(method.getDeclaringClass());
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
				if(paras[i].getType().isArray()&&paraDes!=null&&paraDes.getName().length()>0&&paraDes.getName().indexOf("[]")<0)
					paraDes.setName(paraDes.getName()+"[]");
				bean.addParameter(paras[i],paraDes);
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
	@Override
	public boolean builder(Class<? extends Annotation> annotationClass,Annotation annotation, Class<?> beanClass,Method beanMethod,
			ServletMapping servletMannager) {
		if(annotationClass.equals(RequestMapping.class)){
			builderRestful((RequestMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}else if(annotationClass.equals(Action.class)){
			builderAction((Action) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		return false;
	}

}
