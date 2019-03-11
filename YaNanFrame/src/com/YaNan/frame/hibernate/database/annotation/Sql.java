package com.YaNan.frame.hibernate.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 此注解用于标注一个接口或类作为一个SQL语法的映射，</br>
 * 当接口被此注解注释后，可以在Bean中File或参数除通过@Service的方式注入此注解的实例，并通过调用接口就可执行对应的SQL并返回</br>
 * 注意！！！此注解需要同时标注@Service，否则不会被处理
 * @author Administrator
 *
 */
@Target({ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {
}
