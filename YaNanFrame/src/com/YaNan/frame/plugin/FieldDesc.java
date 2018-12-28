package com.YaNan.frame.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.YaNan.frame.plugin.handler.FieldHandler;

public class FieldDesc extends ParamDesc {
	private Field field;
	/**
	 * fieldHandler
	 */
	private FieldHandler fieldHandler;
	/**
	 * 扫描到的注解
	 */
	private Annotation annotation;

	@SuppressWarnings("unchecked")
	public <T> T getAnnotation() {
		return (T) annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public FieldDesc(String type, String value, Field field) {
		super(type,value,field.getName());
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public FieldHandler getFieldHandler() {
		return fieldHandler;
	}

	public void setFieldHandler(FieldHandler fieldHandler) {
		this.fieldHandler = fieldHandler;
	}

	@Override
	public String toString() {
		return "FieldDesc [type=" + type + ", value=" + value + ", field=" + field + ", fieldHandler="
				+ fieldHandler + ", annotation=" + annotation + "]";
	}

}