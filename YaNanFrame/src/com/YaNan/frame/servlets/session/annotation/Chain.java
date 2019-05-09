package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 放行地址，通过此配置的地址不会经过验证
 * @author yanan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Chain {
	/**
	 * 需要放行的地址
	 * @return
	 */
	String[] chain();
}
