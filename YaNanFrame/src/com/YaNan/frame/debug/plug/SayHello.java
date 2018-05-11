package com.YaNan.frame.debug.plug;

import com.YaNan.frame.debug.plugs.service.Say;
import com.YaNan.frame.plugin.annotations.Register;

@Register
public class SayHello implements Say{

	@Override
	public String say(String name) throws Exception {
		if(name.equals("error"))
			throw new Exception("参数不能为error");
		return "hello "+name;
	}

}
