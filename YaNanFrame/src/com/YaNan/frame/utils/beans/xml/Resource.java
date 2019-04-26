package com.YaNan.frame.utils.beans.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于提供xml寻址路径
 * @author Administrator
 *
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
	/**
	 * 路径表达式
	 * @return
	 */
	String value();
}
