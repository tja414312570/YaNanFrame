package com.YaNan.frame.core.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.YaNan.frame.service.ClassInfo;

@ClassInfo(version = 0)
@Retention(RetentionPolicy.RUNTIME)
public @interface iToken {
	String[] role() default {};

	String[] ex() default {};

	String[] noToken() default {};

	String[] chain() default {};

	String[] exRole() default {};

	String[] exChain() default {};
	
	String onFailed() default "{status:false,message:\"token认证失败\"}";
}