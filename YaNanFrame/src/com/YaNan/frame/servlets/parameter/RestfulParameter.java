package com.YaNan.frame.servlets.parameter;

import java.lang.annotation.Annotation;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.annotations.restful.ParameterType;

@Register
public class RestfulParameter implements ParameterAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos ={ParameterType.class};
		return (Class<Annotation>[]) annos;
	}

}
