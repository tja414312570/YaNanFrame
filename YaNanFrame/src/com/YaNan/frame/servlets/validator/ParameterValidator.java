package com.YaNan.frame.servlets.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public interface ParameterValidator extends Validator {
	
	ParameterValition<Field> validate(Field field,Object value,Class<?>... groups);
	
	ParameterValition<Parameter> validate(Parameter parameter,Object value,Class<?>... groups);

	ConstraintViolation<?> validate(Object parameter, Annotation annotation, Object value, Class<?>... groups);
}
