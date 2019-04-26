package com.YaNan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.servlets.annotations.Action;
import com.YaNan.frame.servlets.annotations.ActionResults;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.annotations.ActionResults.Result;
import com.YaNan.frame.servlets.annotations.DeleteMapping;
import com.YaNan.frame.servlets.annotations.GetMapping;
import com.YaNan.frame.servlets.annotations.PostMapping;
import com.YaNan.frame.servlets.annotations.PutMapping;

public class ServletBeanBuilder implements ServletMappingBuilder{
	public static final String ACTION_STYLE="ACTION_STYLE";
	public static final String RESTFUL_STYLE="RESTFUL_STYLE";
	static Logger log = LoggerFactory.getLogger(ServletBeanBuilder.class);
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
			servletMannager.add(bean);
		return true;
	}
	public static boolean builderRestful(RequestMapping requestMapping, Method method, RequestMapping parentRequestMaping, ServletMapping servletMannager){
		if(requestMapping.method().length==0)
			return true;
		for(int type : requestMapping.method()){
			try {
				ServletBean bean = builder(requestMapping.value(),method,parentRequestMaping==null?null:parentRequestMaping.value(),type);
				servletMannager.add(bean);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				continue;
			}
		}
		return true;
	}
	public static ServletBean builder(String requestMapping, Method method, String parentRequestMaping,int type) throws Exception {
		ServletBean bean = new ServletBean();
		bean.setStyle(RESTFUL_STYLE);
		bean.setRequestMethod(type);
		/**
		 * 获取url映射
		 */
		String urlPath;
		if(parentRequestMaping!=null){
			String namespace = parentRequestMaping.trim();
			if(namespace.equals(""))//如果父类命名空间为空时，父类命名空间为当前类名
				namespace = "/"+method.getDeclaringClass().getSimpleName();
			else if(namespace.equals("/"))//如果父类命名空间为/时，设置命名空间为空，因为子命名空间可能包含了/
				namespace="";
			if(requestMapping.trim().equals("")){
				urlPath = namespace;
			}else{
				urlPath = namespace+requestMapping.trim();
			}
		}else{
			urlPath = requestMapping.trim().equals("")?"/"+method.getName():requestMapping.trim();
		}
		String urlMapping = urlPath+"@"+type;
		bean.setUrlmapping(urlMapping);
		bean.setPathRegex(urlPath);
		bean.setMethod(method);
		bean.setServletClass(method.getDeclaringClass());
		/**
		 * 添加方法的参数,格式为参数类型，参数注解
		 */
		if (method.getParameterCount()!=0) {
			Parameter[] paras = method.getParameters();
			for(int i = 0;i<paras.length;i++)
				bean.addParameter(paras[i]);
		}
			return bean;
	}
	@Override
	public boolean builder(Class<? extends Annotation> annotationClass,Annotation annotation, Class<?> beanClass,Method beanMethod,
			ServletMapping servletMannager) {
		if(annotationClass.equals(RequestMapping.class)){//RequestMapping
			builderRestful((RequestMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(GetMapping.class)){//GetMapping
			builderRestful((GetMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(PutMapping.class)){//PutMapping
			builderRestful((PutMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(PostMapping.class)){//PostMapping
			builderRestful((PostMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(DeleteMapping.class)){//DeleteMapping
			builderRestful((DeleteMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(Action.class)){
			builderAction((Action) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		return false;
	}
	private void builderRestful(DeleteMapping deleteMapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(deleteMapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.DELETE);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(GetMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.GET);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(PutMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.PUT);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(PostMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.POST);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

}
