package com.YaNan.frame.utils.beans.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扫描类Field时的方式</br>
 * 不引用此注解时，默认使用FieldTypes.DECLARED{@link com.YaNan.frame.utils.beans.xml.FieldTypes}</br>
 * 引用此注解时，默认使用FieldTypes.DEFAULTED</br>
 * @author Administrator
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldType {
	/**
	 * 默认使用FieldTypes.DEFAULTED
	 * @return
	 */
	FieldTypes value() default FieldTypes.DEFAULTED;
}
