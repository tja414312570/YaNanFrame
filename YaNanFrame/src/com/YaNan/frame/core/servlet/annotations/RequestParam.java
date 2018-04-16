package com.YaNan.frame.core.servlet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
	/**
	 * 参数名称
	 * @return
	 */
	String value() default "";
	/**
	 * 默认值
	 * @return
	 */
	String defaultValue() default "";
	
	
	}
