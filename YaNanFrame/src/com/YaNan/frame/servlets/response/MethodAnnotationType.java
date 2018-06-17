package com.YaNan.frame.servlets.response;

import java.lang.annotation.Annotation;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface MethodAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}

