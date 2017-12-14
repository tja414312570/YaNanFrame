package com.YaNan.Demo;

import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.NameSpaces;

@NameSpaces("/user/ddd/ssss")
public class NameSpace {
	@Action(namespace="/test/")
	public void name(){
		
	}
}
