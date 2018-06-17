package com.YaNan.frame.servlets.parameter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface RequestParam {
	/**
	 * 参数名称
	 * @return
	 */
	String value() default "";
	/**
	 * 默认值
	 * @return
	 */
	String defaultValue() default "";
	
	
	}
