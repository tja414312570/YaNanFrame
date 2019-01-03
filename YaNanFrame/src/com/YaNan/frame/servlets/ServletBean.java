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
	private Map<String, ServletResult> result;
	/**
	 * 域缓存 action风格有效
	 */
	private Map<String, Field> fieldMap;
	/**
	 * 参数缓存 restful风格有效 存储类型为 参数 ==》<Array>注解
	 */
	private Map<Parameter, Map<Class<Annotation>, List<Annotation>>> parameter;
	/**
	 * 路径变量信息记录 restful风格有效
	 */
	private Map<Integer, String> pathVariable;
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
	private Map<Class<Annotation>, List<Annotation>> methodAnnotation;
	/**
	 * 类的注解
	 */
	private Map<Class<Annotation>, List<Annotation>> classAnnotation;
	/**
	 * 接口返回类型
	 */
	private int type;
	/**
	 * 接口请求方式 get post put delete ...
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
	 * 接口需要验证的参数 action风格有效
	 */
	private String[] args = {};
	/**
	 * 接口描述
	 */
	private String description;
	/**
	 * 
	 * @return
	 */
	private String pathRegex;
	private Map<String, Object> attributes;

	public Class<?> getServletClass() {
		return this.className;
	}

	public void setServletClass(Class<?> className) {
		this.className = className;
		List<MethodAnnotationType> phs = PlugsFactory.getPlugsInstanceList(MethodAnnotationType.class);
		List<Class<Annotation>> annos = new ArrayList<Class<Annotation>>();
		for (MethodAnnotationType responseType : phs) {
			for (Class<Annotation> anno : responseType.getSupportAnnotationType())
				annos.add(anno);
		}
		// 获取所有支持的组件的集合
		this.classAnnotation = PlugsFactory.getAnnotationGroup(className, annos);
	}

	public void setOutputStream(boolean output) {
		this.output = output;
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
		for (MethodAnnotationType responseType : phs) {
			for (Class<Annotation> anno : responseType.getSupportAnnotationType())
				annos.add(anno);
		}
		// 获取所有支持的组件的集合
		this.methodAnnotation = PlugsFactory.getAnnotationGroup(method, annos);
	}

	public Map<String, ServletResult> getResultMap() {
		return this.result;
	}

	public boolean hasResult() {
		return this.result == null;
	}

	public boolean hasResultName(String resultName) {
		return this.result != null && this.result.containsKey(resultName);
	}

	public Iterator<String> getResultIterator() {
		if (this.result == null)
			return null;
		return this.result.keySet().iterator();
	}

	public ServletResult getResult(String resultName) {
		if (this.result == null)
			return null;
		return this.result.get(resultName);
	}

	public void setDecode(boolean decode) {
		this.decode = decode;
	}

	public boolean decode() {
		return this.decode;
	}

	public void addResult(ServletResult result) {
		if (this.result == null)
			synchronized (this) {
				if (this.result == null)
					this.result = new HashMap<String, ServletResult>();
			}
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

	public Map<String, Field> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(Map<String, Field> fieldMap) {
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

	public void addParameter(Parameter parameter) {
		// 获取所有的注解
		// 从组件工厂查询当前servlet解析所支持的注解类型，，可能该注解并不需要用到
		List<ParameterAnnotationType> phs = PlugsFactory.getPlugsInstanceList(ParameterAnnotationType.class);
		List<Class<Annotation>> annos = new ArrayList<Class<Annotation>>();
		for (ParameterAnnotationType parameterAnnotation : phs) {
			for (Class<Annotation> anno : parameterAnnotation.getSupportAnnotationType())
				annos.add(anno);
		}
		// 获取所有支持的组件的集合
		Map<Class<Annotation>, List<Annotation>> maps = PlugsFactory.getAnnotationGroup(parameter, annos);
		// 获取参数类型
		// 将参数信息添加到参数集合中
		if (this.parameter == null)
			this.parameter = new LinkedHashMap<Parameter, Map<Class<Annotation>, List<Annotation>>>();
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

	public Map<Integer, String> getPathVariable() {
		return pathVariable;
	}

	public void setPathVariable(Map<Integer, String> pathVariable) {
		this.pathVariable = pathVariable;
	}

	public void addPathVariable(Integer index, String paraName) {
		if (this.pathVariable == null)
			synchronized (this) {
				if (this.pathVariable == null)
					this.pathVariable = new HashMap<Integer, String>();
			}
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

	public List<Annotation> getParameterAnnotation(Parameter param, Class<? extends Annotation> annoType) {
		if (this.parameter == null)
			return null;
		Map<Class<Annotation>, List<Annotation>> annoMap = this.parameter.get(param);
		if (annoMap == null)
			return null;
		return annoMap.get(annoType);
	}

	public List<Annotation> getMethodAnnotation(Class<? extends Annotation> annoType) {
		if (this.methodAnnotation == null)
			return null;
		return this.methodAnnotation.get(annoType);
	}
	public List<Annotation> getClassAnnotation(Class<? extends Annotation> annoType) {
		if (this.classAnnotation == null)
			return null;
		return this.classAnnotation.get(annoType);
	}
	public void setAttribute(String key, Object value) {
		if (attributes == null)
			synchronized (this) {
				if (attributes == null)
					attributes = new HashMap<String, Object>();
			}
		attributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return attributes == null ? null : (T) attributes.get(key);
	}
}
