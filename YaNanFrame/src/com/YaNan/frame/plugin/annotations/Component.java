package com.YaNan.frame.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.plugin.Default;


@Target(ElementType.ANNOTATION_TYPE )
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
	Class<? extends AnnotationHandler>[] value() default {AnnotationHandler.class};
	Class<?> group() default Default.class;
}
