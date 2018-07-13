package com.YaNan.frame.servlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD,ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
public @interface PutMapping {
	boolean output() default false;//输出流
	String extend() default "servlet-default";//继承包类
	String value() default "";//action 名称
	boolean decode() default false;//是否需要解析结果
	boolean CorssOrgin() default false;//是否需要跨域
	String description() default"";//action 描述
	String attribute() default "*";//用于扩展
	}
