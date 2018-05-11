package com.YaNan.frame.servlets;

import java.lang.annotation.Annotation;

public interface ServletDispatcher {
	 <T extends Annotation> Class<T>[] getDispatcherAnnotation();

	ServletMappingBuilder getBuilder();
}
