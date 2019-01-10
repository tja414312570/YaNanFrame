package com.YaNan.frame.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @author yanan
 *
 */

import com.YaNan.frame.plugin.ProxyModel;
@Target({ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
	/**
	 * 组件优先级，数值越低，优先级越高
	 * @return
	 */
	int priority() default 0;
	/**
	 * 是否采用单例模式
	 * @return
	 */
	boolean signlTon() default true;
	/**
	 * 组件的属性，匹配模式 *匹配任意字符，？匹配任意单字符
	 * @return
	 */
	String[] attribute() default "*";
	/**
	 * 用于描述组件所实现的接口
	 * @return
	 */
	Class<?>[] register() default {};
	/**
	 * 组件描述
	 * @return
	 */
	String description() default "";
	/**
	 * 描述组件实现类所在父类，当接口不在此类时可以使用此属性指向接口所在位置
	 * @return
	 */
	Class<?> declare() default Object.class;
	/**
	 * 代理模式
	 * @return
	 */
	ProxyModel model() default ProxyModel.DEFAULT;
	/**
	 * 组件实例化并在Field赋值完成后之后执行的方法
	 * @return
	 */
	String[] method() default {};
	
}
