package com.YaNan.frame.plugin.handler;

import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.RegisterDescription.FieldDesc;
/**
 * 字段拦截器
 * 当实例初始化之后立刻调用
 * @author yanan
 *
 */
public interface FieldHandler {
	/**
	 * 当要准备字段时掉此方法
	 * @param registerDescription
	 * @param proxy
	 * @param target
	 * @param desc
	 * @param args
	 */
	void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args);

}
