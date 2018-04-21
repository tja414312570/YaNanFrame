package com.YaNan.frame.servlets;

import java.lang.annotation.Annotation;

public interface ServletDispatcher {
	public <T extends Annotation> Class<T>[] getDispatcherAnnotation();

	public ServletMappingBuilder getBuilder();
}
