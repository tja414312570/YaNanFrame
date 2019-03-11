package com.YaNan.frame.servlets.response.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 请求重定向注解
 * @author yanan
 *
 */
@ResponseType
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Redirect {
	/**
	 * 前缀
	 * @return
	 */
	String suffix() default "";
	/**
	 * 后缀
	 * @return
	 */
	String prefix() default "";
	
	}
