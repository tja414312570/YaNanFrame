package com.YaNan.frame.plugs.debug.handler;


import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;

@Register(attribute="com.YaNan.frame.plugs.debug.service.Say.say")
public class SayHandler implements InvokeHandler{

	@Override
	public Object before(MethodHandler mh) {
		if(!mh.getParameters()[0].equals("参数不满足"))
			mh.chain();
		return "执行方法前被拦截了 原因:"+mh.getParameters()[0];
	}

	@Override
	public Object after(MethodHandler mh) {
		if(!mh.getOriginResult().equals("hello 错误结果"))
			mh.chain();
		return "事物结果不满足 原因:执行结果不允许为：hello 错误结果";
	}

	@Override
	public Object error(MethodHandler methodHandler, Exception e) {
		return "事物过程出错,错误原因:"+e.getCause();
	}




}
