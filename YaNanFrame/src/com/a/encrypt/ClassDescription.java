package com.a.encrypt;

import java.util.ArrayList;
import java.util.List;

public class ClassDescription {
	private DynamicJava dynamicJava;
	//注释
	private String comment;
	//名称
	private String name;
	//修饰符
	private List<String> modifs = new ArrayList<String>();
	//继承的父类
	private String extend;
	//实现的接口
	private List<String> impls = new ArrayList<String>();
	//注解
	private List<String> annos = new ArrayList<String>();
	//生成的代码
	private String javaCode = "";
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public List<String> getImpls() {
		return impls;
	}
	public void setImpls(List<String> impls) {
		this.impls = impls;
	}
	public List<String> getAnnos() {
		return annos;
	}
	public void setAnnos(List<String> annos) {
		this.annos = annos;
	}
	public void setModif(String modif) {
		if(modif==null)
			throw new RuntimeException("modif is null");
		this.modifs.clear();
		String[] modifStrs = modif.split(" ");
		for(String modifS : modifStrs)
			this.modifs.add(modifS);
	}
	public void addModif(String modif) {
		this.modifs.add(modif);
	}
	public String build(){
		if(this.comment!=null){
			this.javaCode+="/**"+this.comment+"**/"+DynamicJava.SIMPLE_WRAP;
		}
		for(String modif : modifs)
			this.javaCode+=modif+" ";
		if(this.name==null)
			throw new RuntimeException("class name is null");
		this.javaCode+=this.name;
		if(this.extend!=null)
			this.javaCode+=" extends "+this.extend;
		if(this.impls.size()!=0)
			this.javaCode+="implements";
		for(String impl : this.impls){
			this.javaCode+=" "+impl;
		}
		return this.javaCode;
	}
	public String getJavaCode() {
		return javaCode;
	}
	public void setJavaCode(String javaCode) {
		this.javaCode = javaCode;
	}
	
}
