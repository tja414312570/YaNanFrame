package com.YaNan.frame.reflect.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * Field的缓存，提供Field的注解等获取
 * @author yanan
 *
 */
public class FieldHelper {
	private Field field;
	private int modifiers;
	private Map<Class<? extends Annotation>,Annotation> declaredAnnotationMap = new HashMap<Class<? extends Annotation>,Annotation>(4);
	private Map<Class<? extends Annotation>,Annotation> annotationMap = new HashMap<Class<? extends Annotation>,Annotation>(4);
	private Class<?> declaringClass;
	
	FieldHelper(Field field){
		this.field = field;
		this.modifiers = field.getModifiers();
		this.declaringClass = field.getDeclaringClass();
		this.initHelper();
	}
	private void initHelper(){
		Annotation[] annotations = field.getAnnotations();
		for(Annotation annotation : annotations){
			annotationMap.put(annotation.annotationType(), annotation);
		}
		annotations = field.getDeclaredAnnotations();
		for(Annotation annotation : annotations){
			declaredAnnotationMap.put(annotation.annotationType(), annotation);
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annoType){
		return (T) this.annotationMap.get(annoType);
	}
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annoType){
		return (T) this.declaredAnnotationMap.get(annoType);
	}
	public Field getField() {
		return field;
	}
	public int getModifiers() {
		return modifiers;
	}
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
