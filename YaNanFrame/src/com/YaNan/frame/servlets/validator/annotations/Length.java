package com.YaNan.frame.servlets.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Constraint(validatedBy = {})
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {
	Class<?>[] groups() default { };
	String message() default "{javax.validation.constraints.Length.message}";
	int min() default 0;
	int max() default 0;
	int value() default  -1;
}
