package com.YaNan.frame.RTDT.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface RAction {
	String value() default "";
}
