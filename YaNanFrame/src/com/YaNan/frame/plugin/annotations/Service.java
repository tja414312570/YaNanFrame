package com.YaNan.frame.plugin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * multi 是否每次调用都残生新的实例
 * 
 * @author Administrator
 *
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.PARAMETER} )
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
	boolean multi() default false;
	String attribute() default "*";
}
