package com.YaNan.frame.servlets.parameter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface Date {
	String format() default "yyyy-MM-dd HH:mm:ss";
	Class<?>[] groups() default {};
	}
