package com.YaNan.frame.utils.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DecodeBean {
	String[] key();
	String value() default "object";
}
