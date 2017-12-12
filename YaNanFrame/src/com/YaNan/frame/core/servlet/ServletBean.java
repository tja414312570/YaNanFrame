package com.YaNan.frame.core.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class ServletBean {
	private Map<String, ServletResult> result = new HashMap<String,ServletResult>();
	private Class<?> className;
	private String namespace;
	private Method method;
	private int type;
	private boolean output;
	private boolean decode;
	private boolean corssOrgin;
	private String[] args={};
	public Class<?> getClassName() {
		return this.className;
	}
	public void setClassName(Class<?> className) {
		this.className = className;
	}
	public String getNameSpace() {
		return this.namespace;
	}
	public void setNameSpace(String namespace) {
		this.namespace=namespace;
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
	
}
