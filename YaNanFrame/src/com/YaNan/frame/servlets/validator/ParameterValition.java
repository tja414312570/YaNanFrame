package com.YaNan.frame.servlets.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;

import com.YaNan.frame.servlets.validator.annotations.Length;
import com.YaNan.frame.utils.StringUtil;

public class ParameterValition<T> implements ConstraintViolation<T>{
	private String message;
	private String messageTemplate;
	private T rootBean;
	private Object parameter;
	private Object invalidValue;
	public ParameterValition(T parameter, Annotation anno,
			Object invalidValue,boolean isFast) {
		super();
		if(isFast){
			this.messageTemplate = this.getMessageTemplateFast(anno);
			this.message = this.messageTemplate;
		}else{
			this.messageTemplate = this.getMessageTemplate(anno);
			this.message = this.decoderMessage(parameter,messageTemplate,invalidValue);
			
		}
		this.rootBean = parameter;
		this.parameter = parameter;
		this.invalidValue = invalidValue;
	}
	private String decoderMessage(T parameter, String messageTemplate, Object invalidValue) {
		return StringUtil.decodeVar(messageTemplate, invalidValue);
	}
	private String getMessageTemplateFast(Annotation anno) {
		if(anno.annotationType().equals(Null.class)){
			return ((Null) anno).message();
		}else if(anno.annotationType().equals(NotNull.class)){
			return ((NotNull) anno).message();
		}else if(anno.annotationType().equals(AssertTrue.class)){
			return ((AssertTrue) anno).message();
		}else if(anno.annotationType().equals(AssertFalse.class)){
			return ((AssertFalse) anno).message();
		}else if(anno.annotationType().equals(Min.class)){
			return ((Min) anno).message();
		}else if(anno.annotationType().equals(Max.class)){
			return ((Max) anno).message();
		}else if(anno.annotationType().equals(DecimalMin.class)){
			return ((DecimalMin) anno).message();
		}else if(anno.annotationType().equals(DecimalMax.class)){
			return ((DecimalMax) anno).message();
		}else if(anno.annotationType().equals(Size.class)){
			return ((Size) anno).message();
		}else if(anno.annotationType().equals(Digits.class)){
			return ((Digits) anno).message();
		}else if(anno.annotationType().equals(Past.class)){
			return ((Past) anno).message();
		}else if(anno.annotationType().equals(Future.class)){
			return ((Future) anno).message();
		}else if(anno.annotationType().equals(Pattern.class)){
			return ((Pattern) anno).message();
		}else if(anno.annotationType().equals(Length.class)){
			return ((Length) anno).message();
		}
		return "another validate annotation "+anno.annotationType()+" please use @FastValidate(false)";
	}
	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public void setRootBean(T rootBean) {
		this.rootBean = rootBean;
	}

	public void setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public String getMessageTemplate(Annotation anno) {
		try {
			Method method = anno.annotationType().getDeclaredMethod("message");
			return method.invoke(anno).toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T getRootBean() {
		return this.rootBean;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getRootBeanClass() {
		return (Class<T>) rootBean.getClass();
	}

	@Override
	public Object getLeafBean() {
		return null;
	}

	@Override
	public Object[] getExecutableParameters() {
		return new Object[]{parameter};
	}

	@Override
	public Object getExecutableReturnValue() {
		return null;
	}

	@Override
	public Path getPropertyPath() {
		return null;
	}

	@Override
	public Object getInvalidValue() {
		return this.invalidValue;
	}

	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object unwrap(Class type) {
		return null;
	}
	@Override
	public String getMessageTemplate() {
		return this.messageTemplate;
	}

}
