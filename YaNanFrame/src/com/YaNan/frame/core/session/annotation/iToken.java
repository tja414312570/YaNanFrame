package com.YaNan.frame.core.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface iToken {
	String[] role() default {};

	String[] ex() default {};

	String[] noToken() default {};

	String[] chain() default {};

	String[] exRole() default {};

	String[] exChain() default {};
	
	String onFailed() default "{status:false,message:\"token auth failed\"}";
}