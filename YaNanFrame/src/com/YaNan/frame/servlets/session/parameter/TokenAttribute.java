package com.YaNan.frame.servlets.session.parameter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.YaNan.frame.servlets.annotations.restful.ParameterType;
@ParameterType
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAttribute {

	String value() default "";

}
