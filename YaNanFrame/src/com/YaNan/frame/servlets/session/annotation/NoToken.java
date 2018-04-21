package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NoToken {
	String[] chain();
}
