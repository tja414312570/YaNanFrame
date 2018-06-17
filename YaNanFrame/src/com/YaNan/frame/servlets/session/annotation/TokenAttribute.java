package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAttribute {

	String value() default "";

}
