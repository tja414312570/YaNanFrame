package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MethodHelper {
	private Method method;
	private Map<Class<? extends Annotation>,Annotation> declaredAnnotationMap = new HashMap<Class<? extends Annotation>,Annotation>();
	private Map<Class<? extends Annotation>,Annotation> annotationMap = new HashMap<Class<? extends Annotation>,Annotation>();
	private Map<String,Parameter> parameterMap = new HashMap<String,Parameter>();
	private Map<Parameter,ParameterHelper> parameterHelpers = new HashMap<Parameter,ParameterHelper>();
	private Parameter[] parameters;
	private int modifiers;
	private Class<?> returnType;
	private String name;
	private Class<?> declaringClass;
	private Class<?>[] parameterTypes;
	private int parameterCount;
	private Type genericReturnType;
	private Type[] genericParameterTypes;
	private Type[] genericExceptionTypes;
	public Method getMethod() {
		return method;
	}
	public Map<String, Parameter> getParameterMap() {
		return parameterMap;
	}
	public Map<Parameter, ParameterHelper> getParameterHelpers() {
		return parameterHelpers;
	}
	public int getModifiers() {
		return modifiers;
	}
	public Class<?> getReturnType() {
		return returnType;
	}
	public String getName() {
		return name;
	}
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public int getParameterCount() {
		return parameterCount;
	}
	public Type getGenericReturnType() {
		return genericReturnType;
	}
	public Type[] getGenericParameterTypes() {
		return genericParameterTypes;
	}
	public Type[] getGenericExceptionTypes() {
		return genericExceptionTypes;
	}
	MethodHelper(Method method){
		this.method = method;
		this.modifiers = this.method.getModifiers();
		this.returnType  =  this.method.getReturnType();
		this.name = this.method.getName();
		this.declaringClass  =  this.method.getDeclaringClass();
		this.parameterTypes  = this.method.getParameterTypes();
		this.parameterCount  = this.method.getParameterCount();
		this.genericReturnType = this.method.getGenericReturnType();
		this.genericParameterTypes = this.method.getGenericParameterTypes();
		this.genericExceptionTypes = this.method.getGenericExceptionTypes();
  		this.initHelper();
	}
	private void initHelper(){
		Annotation[] annotations = method.getAnnotations();
		for(Annotation annotation : annotations){
			annotationMap.put(annotation.annotationType(), annotation);
		}
		annotations = method.getDeclaredAnnotations();
		for(Annotation annotation : annotations){
			declaredAnnotationMap.put(annotation.annotationType(), annotation);
		}
		this.parameters = method.getParameters();
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
