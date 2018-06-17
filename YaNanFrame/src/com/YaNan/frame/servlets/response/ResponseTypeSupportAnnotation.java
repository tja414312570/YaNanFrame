package com.YaNan.frame.servlets.response;

import java.lang.annotation.Annotation;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.response.annotations.ResponseType;

@Register
public class ResponseTypeSupportAnnotation implements MethodAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos = {ResponseType.class};
		return (Class<Annotation>[]) annos;
	}

}
