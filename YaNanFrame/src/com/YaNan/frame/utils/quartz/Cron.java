package com.YaNan.frame.utils.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
public @interface Cron {
	String value() default "";
	String name() default "";
	String group() default "";
	String descripton() default "";
}
