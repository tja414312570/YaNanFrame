package com.YaNan.frame.plugin;

import java.lang.reflect.Constructor;
import com.YaNan.frame.plugin.FieldDesc;
import com.YaNan.frame.plugin.InvokeStack;
import com.YaNan.frame.plugin.PluginRuntimeException;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;

@Register(attribute="*",description="调用栈的记录")
public class InvokeStackHandler implements InvokeHandler,FieldHandler,InstanceHandler{

	@Override
	public void before(MethodHandler methodHandler) {
		InvokeStack.addStack(methodHandler.getMethod());
	}

	@Override
	public void after(MethodHandler methodHandler) {
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

	@Override
	public void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args) {
		InvokeStack.addStack(desc.getField());
	}

	@Override
	public void before(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object... args) {
		InvokeStack.addStack(constructor);
	}

	@Override
	public void after(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... arguments) {
	}

	@Override
	public void exception(RegisterDescription registerDescription, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		
	}
}
