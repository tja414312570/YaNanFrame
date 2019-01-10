package com.YaNan.frame.servlets.session.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口认证标示。
 * 20190105 支持转发，消息，重定向，上下文类型以及状态码
 * @author yanan
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Repeatable(AuthenticationGroups.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {

	/**
	 * 需要满足的角色，包含的角色才能进入
	 * 如果角色为or关系，使用数组方式
	 * 如果角色为and关系，使用，分开
	 * @return
	 */
	String[] roles() default {};
	/**
	 * 需要排除的角色,包含的角色不能进入
	 * @return
	 */
	String[] exroles() default {};
	/**
	 * 需要放行的接口，注解在类名时有效
	 * @return
	 */
	String[] chain() default {};
	/**
	 * 当角色认证失败时返回的内容
	 * @return
	 */
	String message() default "";
	/**
	 * 当认证失败时返回状态码 -1为默认
	 * @return
	 */
	int status() default -1;
	/**
	 * 当认证失败返回为内容时的上下文类型
	 * @return
	 */
	String contextType() default "text/html;charset=utf-8";
	/**
	 * 当角色认证失败时重定向到目标
	 * @return
	 */
	String redirect() default "";
	/**
	 * 当角色认证失败是转发目标
	 * @return
	 */
	String forward() default "";
}
