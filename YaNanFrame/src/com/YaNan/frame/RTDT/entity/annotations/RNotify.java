package com.YaNan.frame.RTDT.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RNotify {
	String value() default "";
	int mark() default 4280;
	String token() default "";
}
