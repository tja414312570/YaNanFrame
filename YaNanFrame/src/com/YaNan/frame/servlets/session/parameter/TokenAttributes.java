package com.YaNan.frame.servlets.session.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface TokenAttributes {
	/**
	 * 参数名称
	 * @return
	 */
	String value() default "";
}
