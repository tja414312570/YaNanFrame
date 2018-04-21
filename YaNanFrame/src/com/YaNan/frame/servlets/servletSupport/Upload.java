package com.YaNan.frame.servlets.servletSupport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Upload {
	
	/**
	 * 允许上传的文件类型，不设置则允许所有文件，设置以后DeniedType将失效
	 * @return
	 */
	String Type() default "";
	
	/**
	 * 禁止上传的文件类型，不设置则不禁止
	 * @return
	 */
	String DeniedType() default "";
	
	/**
	 * 最大总文件大小，默认2MB
	 * @return
	 */
	long MaxSize() default 1024*1024*2;
	
	/**
	 * 最大单文件大小，默认2MB
	 * @return
	 */
	long MaxFileSize() default 1024*1024*2;
	
	/**
	 * 最大文件数量，为-1时则不限文件数量
	 * @return
	 */
	int MaxNum() default -1; 
	
	/**
	 * 文件上传路劲，默认当前Servlet目录
	 * @return
	 */
	String Path() default "/";

	String secretKey() default "";
}
