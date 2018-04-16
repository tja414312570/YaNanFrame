package com.YaNan.frame.plugs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 
 * 
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {

	int priority() default 0;

	boolean signlTon() default true;

	String attribute() default "*";
	
	Class<?>[] register() default {};
	
	Class<?> declare() default Object.class;
	
	
}
