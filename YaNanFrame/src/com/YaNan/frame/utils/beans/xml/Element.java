package com.YaNan.frame.utils.beans.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 节点元素，用于List等聚合Field，或非基本类型的pojo类型，
 * 作用于更节点的java映射的类声明处时，name为根路径
 * 作用于Field时为节点名称
 * @author Administrator
 *
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Element {
	/**
	 * 默认值
	 * @return
	 */
	String value() default "";
	/**
	 * 映射名
	 * @return
	 */
	String name() default "";
	
}
