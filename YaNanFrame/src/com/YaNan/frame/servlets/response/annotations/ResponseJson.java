package com.YaNan.frame.servlets.response.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应JSON数据
 * @author yanan
 *
 */
@ResponseType
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseJson {
	/**
	 * JSON数据前缀
	 * @return
	 */
	String prefix() default "";
	/**
	 * JSON数据后缀
	 * @return
	 */
	String suffix() default "";
}
