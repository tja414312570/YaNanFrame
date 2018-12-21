package com.YaNan.frame.plugin.autowired.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 错误处理注解，提供bean执行过程的异常处理
 * @author yanan
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Error {
	/**
	 * 参数名称
	 * @return
	 */
	Class<?> exception() default Throwable.class;
	/**
	 * 默认值
	 * @return
	 */
	String value() default "";
	
	boolean recorder() default true;
}
