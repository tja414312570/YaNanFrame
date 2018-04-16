package com.YaNan.frame.core.servlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
	 * 参数缓存 restful风格有效
	 */
	private Map<Parameter,Object> parameterMap = new LinkedHashMap<Parameter,Object>();
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
	 * 接口返回类型
	 */
	private int type;
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
	public Class<?> getClassName() {
		return this.className;
	}
	public void setClassName(Class<?> className) {
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
	public void addParameter(Parameter parameter,Object annotation){
		this.parameterMap.put(parameter, annotation);
	}
	public String getPathRegex() {
		return pathRegex;
	}
	public void setPathRegex(String pathRegex) {
		this.pathRegex = pathRegex;
	}
	
}
