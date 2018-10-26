package com.a.encrypt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Methods {
	private DynamicJava dynamicJava;
	//注释
	private String comment;
	//修饰符
	private List<String> modifs = new ArrayList<String>();
	//注解
	private List<String> annos = new ArrayList<String>();
	//名称
	private String name;
	//返回值类型
	private String returnType;
	//参数
	private List<String> parameters = new ArrayList<String>();
	//方法体内容
	private String method;
	//生成的代码
	private String javaCode = "";
	public List<String> getModifs() {
		return modifs;
	}
	public void setModif(String modif) {
		if(modif==null)
			throw new RuntimeException("modif is null");
		this.modifs.clear();
		String[] modifStrs = modif.split(" ");
		for(String modifS : modifStrs)
			this.modifs.add(modifS);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public void addModif(String modif) {
		this.modifs.add(modif);
	}
	public String build(){
		if(this.comment!=null){
			this.javaCode+=DynamicJava.TAB+"/**"+this.comment+"**/"+DynamicJava.SIMPLE_WRAP;
		}
		for(String modif : modifs)
			this.javaCode+=modif+" ";
		this.javaCode+=this.returnType==null?"void":this.returnType;
		if(this.name==null)
			throw new RuntimeException("class name is null");
		this.javaCode+=" "+this.name+"(";
		Iterator<String> parameterIterator = this.parameters.iterator();
		while(parameterIterator.hasNext()){
			this.javaCode+=parameterIterator.next()+(parameterIterator.hasNext()?",":"");
		}
		this.javaCode+=this.name+")";
		if(this.method==null)
			this.javaCode+=";";
		else
			this.javaCode=DynamicJava.SIMPLE_WRAP+DynamicJava.TAB+this.javaCode+DynamicJava.BODY_START+DynamicJava.TAB+DynamicJava.TAB+this.method+DynamicJava.SIMPLE_WRAP+DynamicJava.TAB+DynamicJava.BODY_END;
		return this.javaCode;
	}
	public DynamicJava getDynamicJava() {
		return dynamicJava;
	}
	public void setDynamicJava(DynamicJava dynamicJava) {
		this.dynamicJava = dynamicJava;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<String> getAnnos() {
		return annos;
	}
	public void setAnnos(List<String> annos) {
		this.annos = annos;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getJavaCode() {
		return javaCode;
	}
	public void setJavaCode(String javaCode) {
		this.javaCode = javaCode;
	}
	public void setModifs(List<String> modifs) {
		this.modifs = modifs;
	}
	public void addParameter(String string) {
		this.parameters.add(string);
	}
}
