package com.YaNan.frame.plugin.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
	/**
	 * 秘钥服务接口
	 * @return
	 */
	Class<?> interfacer();
	/**
	 * 参数
	 * @return
	 */
	String[] parameters() default {};
}
