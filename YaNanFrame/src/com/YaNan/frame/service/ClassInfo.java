package com.YaNan.frame.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 此注解用于保存类的信息(以下所有的“空”均为 空字符串，非null) author 作者 默认为空 version 版本 description 描述
 * 默认为空 to 被依赖 默认为空 rely 依赖于 默认为空
 * 
 * @author Administrator
 *
 */
@ClassInfo(version = 0)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassInfo {
	int version();

	String author() default "";

	String description() default "";

	String[] to() default "";

	String[] rely() default "";

	String jdk() default "1.6";
}
