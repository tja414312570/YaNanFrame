package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ParameterHelper {
	private Parameter parameter;
	private Map<Class<? extends Annotation>,Annotation> declaredAnnotationMap = new HashMap<Class<? extends Annotation>,Annotation>();
	private Map<Class<? extends Annotation>,Annotation> annotationMap = new HashMap<Class<? extends Annotation>,Annotation>();
	private int modifiers;
	private Class<?> type;
	public Parameter getParameter() {
		return parameter;
	}
	public int getModifiers() {
		return modifiers;
	}
	public Class<?> getType() {
		return type;
	}
	ParameterHelper(Parameter parameter){
		this.parameter = parameter;
		this.modifiers = parameter.getModifiers();
		this.type = parameter.getType();
		this.initHelper();
	}
	private void initHelper(){
		Annotation[] annotations = parameter.getAnnotations();
		for(Annotation annotation : annotations){
			annotationMap.put(annotation.annotationType(), annotation);
		}
		annotations = parameter.getDeclaredAnnotations();
		for(Annotation annotation : annotations){
			declaredAnnotationMap.put(annotation.annotationType(), annotation);
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
}
