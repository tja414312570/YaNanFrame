package com.YaNan.frame.debug.plug;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugs.debug.service.Say;

@Register
public class SayHello implements Say{

	@Override
	public String say(String name) throws Exception {
		if(name.equals("error"))
			throw new Exception("参数不能为error");
		return "hello "+name;
	}

}
