package com.YaNan.frame.servlets.annotations.restful;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.groups.Default;

/**
 * Restful接口基础参数类
 * @author yanan
 *
 */
@Target(ElementType.ANNOTATION_TYPE )
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterType {
	Class<?> group() default Default.class;
}
