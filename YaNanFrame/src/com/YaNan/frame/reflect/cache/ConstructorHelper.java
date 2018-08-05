package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ConstructorHelper {
	private Constructor<?> constructor;
	private Map<Class<? extends Annotation>,Annotation> declaredAnnotationMap = new HashMap<Class<? extends Annotation>,Annotation>(4);
	private Map<Class<? extends Annotation>,Annotation> annotationMap = new HashMap<Class<? extends Annotation>,Annotation>(4);
	private Map<String,Parameter> parameterMap = new HashMap<String,Parameter>();
	private Map<Parameter,ParameterHelper> parameterHelpers = new HashMap<Parameter,ParameterHelper>();
	private Parameter[] parameters;
	ConstructorHelper(Constructor<?> constructor){
		this.constructor = constructor;
		this.initHelper();
	}
	private void initHelper(){
		Annotation[] annotations = constructor.getAnnotations();
		for(Annotation annotation : annotations){
			annotationMap.put(annotation.annotationType(), annotation);
		}
		annotations = constructor.getDeclaredAnnotations();
		for(Annotation annotation : annotations){
			declaredAnnotationMap.put(annotation.annotationType(), annotation);
		}
		this.parameters = constructor.getParameters();
		for(Parameter param : parameters){
			this.parameterMap.put(param.getName(), param);
			this.parameterHelpers.put(param, new ParameterHelper(param));
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annoType){
		return (T) this.annotationMap.get(annoType);
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annoType){
		return (T) this.declaredAnnotationMap.get(annoType);
	}
	public Parameter[] getParameters(){
		return this.parameters;
	}
	public ParameterHelper getParmeterHelper(Parameter parameter){
		return this.parameterHelpers.get(parameter);
	}
	public Map<Parameter, ParameterHelper> getParameterHelperMap(){
		return this.parameterHelpers;
	}
	public Parameter getParameter(String parameterName){
		return this.parameterMap.get(parameterName);
	}
}
