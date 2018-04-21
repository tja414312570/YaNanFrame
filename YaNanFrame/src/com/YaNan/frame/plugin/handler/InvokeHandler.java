package com.YaNan.frame.plugin.handler;


public interface InvokeHandler {
	Object before(MethodHandler methodHandler);
	Object after(MethodHandler methodHandler);
	Object error(MethodHandler methodHandler, Exception e);
}
