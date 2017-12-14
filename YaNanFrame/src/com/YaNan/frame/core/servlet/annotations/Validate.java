package com.YaNan.frame.core.servlet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	String RegExpression() default "([\\s\\S]*)";
	String Failed() default "";
	int method() default RESPONSE_METHOD.OUTPUT;
	String isNull() default "";
}
