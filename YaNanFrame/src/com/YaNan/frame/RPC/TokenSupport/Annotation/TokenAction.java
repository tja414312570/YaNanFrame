package com.YaNan.frame.RPC.TokenSupport.Annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAction {

	String value();
	
}
