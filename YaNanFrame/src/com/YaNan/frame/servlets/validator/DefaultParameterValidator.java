package com.YaNan.frame.servlets.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Future;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.parameter.ParameterAnnotationType;
import com.YaNan.frame.servlets.validator.annotations.Length;


@Register
public class DefaultParameterValidator implements ParameterValidator,ParameterAnnotationType{

	@Override
	public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		return null;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
		return null;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value,
			Class<?>... groups) {
		return null;
	}

	@Override
	public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> type) {
		return null;
	}

	@Override
	public ExecutableValidator forExecutables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterValition<Field> validate(Field field, Object value,Class<?>... groups) {
		if(field==null) return null;//如果field为空则直接匹配成功
		Annotation[] anns = field.getAnnotations();
		for(Annotation anno : anns){
			if(!validate(anno,value,groups)){
				return new ParameterValition<Field>(field,anno, value,true);
			}
		}
		return null;
	}

	@Override
	public ParameterValition<Parameter> validate(Parameter parameter, Object value,Class<?>... groups) {
		if(parameter==null) return null;//如果field为空则直接匹配成功
		Annotation[] anns = parameter.getAnnotations();
		for(Annotation anno : anns){
			if(!validate(anno,value,groups)){
				return new ParameterValition<Parameter>(parameter,anno, value,true);
			}
		}
		return null;
	}
	@Override
	public ConstraintViolation<?> validate(Object parameter, Annotation annotation, Object value, Class<?>... groups) {
		if(parameter.getClass().equals(Field.class)){
			if(!validate(annotation,value,groups))
				return new ParameterValition<Field>((Field)parameter,annotation, value,true);
		}else{
			if(!validate(annotation,value,groups))
				return new ParameterValition<Parameter>((Parameter)parameter,annotation, value,true);
		}
		return null;
	}
	/**
	 * JSR 303验证
	 * @param anno
	 * @param Value
	 * @return
	 */
	public boolean validate(Annotation anno,Object value,Class<?>... groups){
		if(anno.annotationType().equals(Null.class)){
			return this.assertNull((Null) anno,value,groups);
		}else if(anno.annotationType().equals(NotNull.class)){
			return this.assertNotNull((NotNull) anno,value,groups);
		}else if(anno.annotationType().equals(AssertTrue.class)){
			return this.assertTrue((AssertTrue) anno,value,groups);
		}else if(anno.annotationType().equals(AssertFalse.class)){
			return this.assertFalse((AssertFalse) anno,value,groups);
		}else if(anno.annotationType().equals(Min.class)){
			return this.Min((Min) anno,value,groups);
		}else if(anno.annotationType().equals(Max.class)){
			return this.Max((Max) anno,value,groups);
		}else if(anno.annotationType().equals(DecimalMin.class)){
			return this.DecimalMin((DecimalMin) anno,value,groups);
		}else if(anno.annotationType().equals(DecimalMax.class)){
			return this.DecimalMax((DecimalMax) anno,value,groups);
		}else if(anno.annotationType().equals(Size.class)){
			return this.size((Size) anno,value,groups);
		}else if(anno.annotationType().equals(Digits.class)){
			return this.digits((Digits) anno,value,groups);
		}else if(anno.annotationType().equals(Past.class)){
			return this.past((Past) anno,value,groups);
		}else if(anno.annotationType().equals(Future.class)){
			return this.future((Future) anno,value,groups);
		}else if(anno.annotationType().equals(Pattern.class)){
			return this.pattern((Pattern) anno, value,groups);
		}else if (anno.annotationType().equals(Length.class)){
			return this.length((Length) anno,value,groups);
		}
		return false;
	}
	private boolean length(Length anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		if(value==null)
			return false;
		int len = value.toString().length();
		if(anno.value()>=0)
			return len== anno.value();
		return len >=anno.min()&&len<=anno.max();
	}

	private boolean DecimalMax(javax.validation.constraints.DecimalMax anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return new BigDecimal(value.toString()).compareTo(new BigDecimal(anno.value()))<=0;
	}

	private boolean DecimalMin(DecimalMin anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return new BigDecimal(value.toString()).compareTo(new BigDecimal(anno.value()))>=0;
	}

	private boolean Max(Max anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return Integer.valueOf(value.toString())<=anno.value();
	}

	private boolean Min(Min anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return Integer.valueOf(value.toString())>=anno.value();
	}

	private boolean assertTrue(AssertTrue anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return Boolean.parseBoolean(value.toString());
	}
	private boolean assertFalse(AssertFalse anno, Object value, Class<?>[] groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return !Boolean.parseBoolean(value.toString());
	}
	private boolean checkeGroup(Class<?>[] groups,Class<?>[] vgroups){
		if(vgroups==null||vgroups.length==0){
			if(groups==null||groups.length==0)
				return true;
			else
				return false;
		}
		if(groups==null||groups.length==0)
			return true;
		for(Class<?> group : groups)
			for(Class<?> vgroup:vgroups)
				if(group.equals(vgroup))
					return true;
		return false;
	}
	private boolean assertNull(Null anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return value==null;
	}
	private boolean assertNotNull(NotNull anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		return value!=null;
	}
	private boolean pattern(Pattern anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		if(value==null)return false;
		return value.toString().matches(anno.regexp());
	}
	private boolean past(Past anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		if(value==null)
			return false;
		return ((Date)value).before(new Date());
	}
	private boolean future(Future anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		if(value==null)
			return false;
		return ((Date)value).after(new Date());
	}
	private boolean digits(Digits anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		if(value==null)
			return false;
		String val = value.toString();
		int i = val.indexOf(".");
		if(i>=0)
			return val.substring(0,i).length()<=anno.integer()&&val.substring(i+1).length()<=anno.fraction();
		return val.length()<=anno.integer();
	}

	/**
	 * 
	 * @param anno
	 * @param value
	 * @return
	 */
	private boolean size(Size anno, Object value,Class<?>... groups) {
		if(!this.checkeGroup(anno.groups(), groups))return true;
		//如果是字符
		if(value==null)
			return false;
		//如果是数组
		if(value.getClass().isArray())
			return anno.max()>=Array.getLength(value)&&anno.min()<=Array.getLength(value);
		//list,set==> Collection
		if(value instanceof Collection)
			return anno.max()>=((Collection<?>)value).size()&&anno.min()<=((Collection<?>)value).size();
		//map
		if(value instanceof Map)
			return anno.max()>=((Map<?,?>)value).size()&&anno.min()<=((Map<?,?>)value).size();
		return anno.min()<=value.toString().length()&&value.toString().length()<=anno.max();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annotations = {Constraint.class};
		return (Class<Annotation>[]) annotations;
	}

	
}
