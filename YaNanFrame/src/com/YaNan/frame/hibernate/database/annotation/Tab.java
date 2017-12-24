package com.YaNan.frame.hibernate.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 此注解只能用于Tab 此注解用于保存表的注解(以下所有的“空”均为 空字符串，非null) value 该值可以填所有的类型 Type 数据类型
 * 
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tab {
	String name() default "";//数据表名

	boolean isMust() default true;//是否必要，默认true，true时会在容器启动时创建对应的数据表

	String include() default "";//包含其他表

	String value() default "";//

	String DB() default "";//数据库名称 与 hibernate.xml中的name对应

	boolean autoUpdate() default false;//自动更新表结构，此版本不提供

	boolean encrypt() default false;//启用加密,此版本不提供
	
	String collate() default "";
	
	String charset() default "utf8";
}
