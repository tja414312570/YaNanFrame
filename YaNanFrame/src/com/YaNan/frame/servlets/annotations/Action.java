package com.YaNan.frame.servlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	boolean output() default false;//输出流
	String namespace() default "/";//命名空间
	String extend() default "servlet-default";//继承包类
	String value() default "";//action 名称
	boolean decode() default false;//是否需要解析结果
	boolean CorssOrgin() default false;//是否需要跨域
	String[] args() default {};//所需参数
	int method() default RESPONSE_METHOD.OUTPUT;//输出方式
	String description() default"";//action 描述
	}
