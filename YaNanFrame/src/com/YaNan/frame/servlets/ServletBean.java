package com.YaNan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.parameter.ParameterAnnotationType;
import com.YaNan.frame.servlets.response.MethodAnnotationType;
public class ServletBean {
	/**
	 * 返回类型
	 */
	private Map<String, ServletResult> result = new HashMap<String,ServletResult>();
	/**
	 * 域缓存 action风格有效
	 */
	private Map<String,Field> fieldMap = new HashMap<String,Field>();
	/**
	 * 参数缓存 restful风格有效  存储类型为  参数  ==》<Array>注解
	 */
	private Map<Parameter,Map<Class<Annotation>,List<Annotation>>> parameter = new LinkedHashMap<Parameter,Map<Class<Annotation>,List<Annotation>>>();
	/**
	 * 路径变量信息记录 restful风格有效
	 */
	private Map<Integer,String> pathVariable = new HashMap<Integer,String>();
	/**
	 * 接口类
	 */
	private Class<?> className;
	/**
	 * 接口url
	 */
	private String urlmapping;
	/**
	 * 接口方法
	 */
	private Method method;
	/**
	 * 方法的注解
	 */
	private Map<Class<Annotation>,List<Annotation>> methodAnnotation = new HashMap<Class<Annotation>, List<Annotation>>();
	/**
	 * 接口返回类型
	 */
	private int type;
	/**
	 * 接口请求方式  get post put delete ...
	 */
	private int requestMethod;
	/**
	 * 接口风格
	 */
	private String style;
	/**
	 * 接口是否需要输出
	 */
	private boolean output;
	/**
	 * 接口是否需要转换
	 */
	private boolean decode;
	/** 
	 * 接口是否跨域
	 */
	private boolean corssOrgin;
	/**
	 * 接口需要验证的参数   action风格有效
	 */
	private String[] args={};
	/**
	 * 接口描述
	 */
	private String description;
	/**
	 * 
	 * @return
	 */
	private String pathRegex;
	public Class<?> getServletClass() {
		return this.className;
	}
	public void setServletClass(Class<?> className) {
		this.className = className;
	}
	public void setOutputStream(boolean output) {
		this.output=output;
	}
	public boolean hasOutputStream() {
		return this.output;
	}
	public Method getMethod() {
		return this.method;
	}
	public void setMethod(Method method) {
		this.method = method;
		List<MethodAnnotationType> phs = PlugsFactory.getPlugsInstanceList(MethodAnnotationType.class);
		List<Class<Annotation>> annos = new ArrayList<Class<Annotation>>();
		for(MethodAnnotationType responseType : phs){
			for(Class<Annotation> anno : responseType.getSupportAnnotationType())
				annos.add(anno);
		}
		//获取所有支持的组件的集合
		this.methodAnnotation = PlugsFactory.getAnnotationGroup(method,annos);
	}
	public Map<String, ServletResult> getResultMap() {
		return this.result;
	}
	public boolean hasResult() {
		return this.result.size()!=0;
	}
	public boolean hasResultName(String resultName) {
		return this.result.containsKey(resultName);
	}
	public Iterator<String> getResultIterator() {
		return this.result.keySet().iterator();
	}
	public ServletResult getResult(String resultName) {
		return this.result.get(resultName);
	}
	public void setDecode(boolean decode){
		this.decode = decode;
	}
	public boolean decode() {
		return this.decode;
	}
	public void addResult(ServletResult result) {
		this.result.put(result.getName(), result);
	}
	public boolean isCorssOrgin() {
		return corssOrgin;
	}
	public void setCorssOrgin(boolean corssOrgin) {
		this.corssOrgin = corssOrgin;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String,Field> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String,Field> fieldMap) {
		this.fieldMap = fieldMap;
	}
	public String getUrlmapping() {
		return urlmapping;
	}
	public void setUrlmapping(String urlmapping) {
		this.urlmapping = urlmapping;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public void addParameter(Parameter parameter){
		//获取所有的注解
		//从组件工厂查询当前servlet解析所支持的注解类型，，可能该注解并不需要用到
		List<ParameterAnnotationType> phs = PlugsFactory.getPlugsInstanceList(ParameterAnnotationType.class);
		List<Class<Annotation>> annos = new ArrayList<Class<Annotation>>();
		for(ParameterAnnotationType parameterAnnotation : phs){
			for(Class<Annotation> anno : parameterAnnotation.getSupportAnnotationType())
				annos.add(anno);
		}
		//获取所有支持的组件的集合
		Map<Class<Annotation>,List<Annotation>> maps = PlugsFactory.getAnnotationGroup(parameter,annos);
		//获取参数类型
		//将参数信息添加到参数集合中
		this.parameter.put(parameter, maps);
	}
	public String getPathRegex() {
		return pathRegex;
	}
	public void setPathRegex(String pathRegex) {
		this.pathRegex = pathRegex;
	}
	@Override
	public String toString() {
		return "ServletBean [result=" + result + ", fieldMap=" + fieldMap + ", parameterMap=" + parameter
				+ ", className=" + className + ", urlmapping=" + urlmapping + ", method=" + method + ", type=" + type
				+ ", style=" + style + ", output=" + output + ", decode=" + decode + ", corssOrgin=" + corssOrgin
				+ ", args=" + Arrays.toString(args) + ", description=" + description + ", pathRegex=" + pathRegex + "]";
	}
	public Map<Integer,String> getPathVariable() {
		return pathVariable;
	}
	public void setPathVariable(Map<Integer,String> pathVariable) {
		this.pathVariable = pathVariable;
	}
	public void addPathVariable(Integer index,String paraName) {
		this.pathVariable.put(index, paraName);
	}
	public int getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(int requestMethod) {
		this.requestMethod = requestMethod;
	}
	public Map<Parameter, Map<Class<Annotation>, List<Annotation>>> getParameters() {
		return parameter;
	}
	public void setParameter(Map<Parameter, Map<Class<Annotation>, List<Annotation>>> parameter) {
		this.parameter = parameter;
	}
	public  List<Annotation> getParameterAnnotation(Parameter param ,Class<? extends Annotation> annoType) {
		Map<Class<Annotation>,List<Annotation>> annoMap = this.parameter.get(param);
		if(annoMap==null)
			return null;
		return annoMap.get(annoType);
	}
	public List<Annotation> getMethodAnnotation(Class<? extends Annotation> annoType) {
		if(this.methodAnnotation==null)
			return null;
		return this.methodAnnotation.get(annoType);
	}
}
