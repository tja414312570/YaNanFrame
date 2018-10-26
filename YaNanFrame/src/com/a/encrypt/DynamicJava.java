package com.a.encrypt;

import java.util.ArrayList;
import java.util.List;

import com.YaNan.frame.plugin.PlugsFactory;

public class DynamicJava {
	public static final String SIMPLE_WRAP = "\r\n";
	public static final String END_WRAP = ";\r\n";
	public static final String BODY_START = "{\r\n";
	public static final String BODY_END = "}";
	public static final String TAB = "\t";
	//包
	private String packages;
	//导入类
	private List<String> imports = new ArrayList<String>();
	//类名称
	private String Class;
	private ClassDescription classDescription;
	//继承的父类
	private List<Methods> methods = new ArrayList<Methods>();
	//生成的类型的描述
	private String javaCodes="";
	public static void main(String[] args) {
		DynamicJava java = PlugsFactory.getPlugsInstance(DynamicJava.class);
		java.packages = "com.YaNan.test.java";
		java.imports.add("java.util.List");
		java.imports.add("java.util.ArrayList");
		java.classDescription = new ClassDescription();
		java.classDescription.setName("DynamicJava");
		java.classDescription.setModif("public class");
		java.classDescription.setComment("simple dynamic java test");
		Methods method = new Methods();
		method.addModif("public static");
		method.setName("main");
		method.addParameter("String[] args");
		method.setMethod("System.out.print(\"hello world\");");
		java.methods.add(method);
		System.out.println(java.build());
	}
	public String build(){
		if(this.packages==null)
			throw new RuntimeException("package is null");
		this.javaCodes +="package "+packages+END_WRAP+SIMPLE_WRAP;
		for(String string : imports)
			this.javaCodes+="import "+string+END_WRAP;
		this.javaCodes+=SIMPLE_WRAP+classDescription.build()+DynamicJava.BODY_START;
		for(Methods me : methods)
			this.javaCodes+=me.build();
		return javaCodes+DynamicJava.SIMPLE_WRAP+DynamicJava.SIMPLE_WRAP+DynamicJava.BODY_END;
	}
}
