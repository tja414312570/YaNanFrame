package com.YaNan.frame.plugin.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 
 * 
 * @author Administrator
 *
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
@Target(ElementType.TYPE )
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

	boolean multi() default false;

	String[] join();//切点

	boolean interrupted() default false;//中断
	
	
}
