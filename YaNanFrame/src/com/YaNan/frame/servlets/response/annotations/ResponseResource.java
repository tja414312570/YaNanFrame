package com.YaNan.frame.servlets.response.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文件响应
 * @author yanan
 *
 */
@ResponseType
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseResource {
	/**
	 * 断点续传功能，默认启用
	 * @return
	 */
	boolean enableBCT() default true;
	/**
	 * 响应文件下载名，默认为文件名
	 */
	String fileName() default "";
	/**
	 * 缓存大小
	 * @return
	 */
	int buffer() default 2048;
	/**
	 * 是否使用下载文件，为false则直接输出类容，默认开启
	 */
	boolean attachment() default true;
	
	
	}
