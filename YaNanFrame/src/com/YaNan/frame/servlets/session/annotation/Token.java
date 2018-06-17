package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
	String[] roles() default {};//需要满足的角色，包含的角色才能进入

	String[] exroles() default {};//需要排除的角色,包含的角色不能进入

	String[] chain() default {};//需要放行的接口，注解在父类时有效

	String onFailed() default "{status:false,message:\"token auth failed\"}";//当角色认证失败时返回的内容
}