package com.YaNan.frame.RTDT.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;

/**
 * websocket notify , was used with notify,entity class must implement RTDTNotification;
 * {@link RTDTNotification}
 * @author yanan
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RNotify {
	/**
	 * notify path
	 * @return
	 */
	String value() default "";
	/**
	 * notify marker
	 * @return
	 */
	int mark() default 4280;
	/**
	 * 
	 * @return
	 */
	String token() default "";
}
