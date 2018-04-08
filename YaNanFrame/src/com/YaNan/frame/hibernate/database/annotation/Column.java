package com.YaNan.frame.hibernate.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 姝ゆ敞瑙ｅ彧鑳界敤浜巉ield 姝ゆ敞瑙ｇ敤浜庝繚瀛樼被鐨勪俊鎭�(浠ヤ笅鎵�鏈夌殑鈥滅┖鈥濆潎涓� 绌哄瓧绗︿覆锛岄潪null) value 璇ュ�煎彲浠ュ～鎵�鏈夌殑绫诲瀷 Type 鏁版嵁绫诲瀷
 * 
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	boolean ignore() default false;

	String name() default "";

	String value() default "";

	String type() default "";

	int length() default -1;

	int size() default -1;

	String format() default "";

	boolean Not_Null() default false;

	boolean point() default false;

	boolean Auto_Increment() default false;

	boolean Primary_Key() default false;

	boolean unique() default false;

	boolean Not_Sign() default true;

	boolean Auto_Fill() default false;

	String Annotations() default "";

	String Default() default "";

	boolean encrypt() default false;
    
	String collate() default "";
	
	String charset() default "";
	
}
