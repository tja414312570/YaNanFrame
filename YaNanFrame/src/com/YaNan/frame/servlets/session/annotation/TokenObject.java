package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TokenObject {

	String ServiceName() default "";

	int Type() default 0;

	String key() default "";

	String isNull() default "";
	
}
