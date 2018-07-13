package com.YaNan.frame.plugin.autowired.property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
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
