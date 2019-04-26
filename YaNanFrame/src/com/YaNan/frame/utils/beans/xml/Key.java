package com.YaNan.frame.utils.beans.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当类型为Map时的Key值取值
 * @author Administrator
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
	/**
	 * 默认值
	 * @return
	 */
	String value();
}
