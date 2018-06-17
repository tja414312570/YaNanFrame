package com.YaNan.frame.servlets.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.servlets.annotations.RESPONSE_METHOD;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	String RegExpression() default "([\\s\\S]*)";
	String Failed() default "";
	int method() default RESPONSE_METHOD.OUTPUT;
	String isNull() default "";
}
