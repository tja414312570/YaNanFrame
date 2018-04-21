package com.YaNan.frame.servlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionResults{
	Result[] value();
	@Target(ElementType.METHOD)
	@Repeatable(ActionResults.class)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Result {
		String name();
		String value();
		int method() default RESPONSE_METHOD.FORWARD;
		
	}
}
