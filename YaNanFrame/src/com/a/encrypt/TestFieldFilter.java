package com.a.encrypt;

import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.RegisterDescription.FieldDesc;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.FieldHandler;

/**
 * 
 * @author yanan
 *
 */
@Register
public class TestFieldFilter implements FieldHandler{
	@Override
	public void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args) {
		System.out.println("field:"+desc.getField());
		System.out.println("value:"+desc.getValue());
	}

}
