package com.YaNan.frame.utils.beans.xml;

/**
 * 扫描类Field时使用的方式</br>
 * DEFAULTED:使用ClassHelper.getFields(){@link com.YaNan.frame.reflect.cache.ClassHelper}</br>
 * DECLARED:使用ClassHelper.getDeclaredFields()</br>
 * ALL:使用ClassHelper.getAllFields()</br>
 * @author yanan
 */
public enum FieldTypes {
	DEFAULTED,DECLARED,ALL
}
