package com.YaNan.frame.servlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface Groups {
	Class<?>[] value() default {};
	}
