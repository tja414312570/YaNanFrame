package com.YaNan.frame.servlets.parameter;

import java.lang.annotation.Annotation;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface ParameterAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}

