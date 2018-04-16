package com.YaNan.frame.core.servlet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.core.servlet.RequestMethod;
@Target(ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	boolean output() default false;//输出流
	String extend() default "servlet-default";//继承包类
	String value() default "";//action 名称
	boolean decode() default false;//是否需要解析结果
	boolean CorssOrgin() default false;//是否需要跨域
	String[] args() default {};//所需参数
	String description() default"";//action 描述
	RequestMethod method() default RequestMethod.METHOD_GET;
	}
