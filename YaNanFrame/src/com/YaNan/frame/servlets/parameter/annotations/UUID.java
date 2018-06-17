package com.YaNan.frame.servlets.parameter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface UUID {
	/**
	 * UUID的长度
	 * @return
	 */
	int beginIndex() default 0;
	int endIndex() default -1;
	int value() default -1;
	Class<?>[] groups() default {};
	}
