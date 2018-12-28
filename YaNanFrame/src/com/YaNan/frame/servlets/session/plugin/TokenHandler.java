package com.YaNan.frame.servlets.session.plugin;

import java.lang.reflect.Constructor;

import com.YaNan.frame.plugin.FieldDesc;
import com.YaNan.frame.plugin.PluginRuntimeException;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.plugin.handler.InstanceHandler;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.cache.ClassHelper;
import com.YaNan.frame.reflect.cache.MethodHelper;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.servlets.session.annotation.Authentication;

@Support(Authentication.class)
@Register
public class TokenHandler implements InvokeHandler,FieldHandler,InstanceHandler {
	
	@Override
	public void before(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object... args) {
		Authentication auth = ClassHelper.getClassHelper(plugClass).getConstructorHelper(constructor).getAnnotation(Authentication.class);
		if(auth==null)
			auth = ClassHelper.getClassHelper(plugClass).getAnnotation(Authentication.class);
		Token token = Token.getToken();
		if(token == null)
			throw new RuntimeException("Class "+plugClass +" instance need provide token");
		if(!token.containerRole(auth.roles()))
			throw new RuntimeException("No permission to instantiate the current class");
	}

	@Override
	public void after(RegisterDescription registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exception(RegisterDescription registerDescription, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
	}

	@Override
	public void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args) {
		System.out.println("拦截器:"+Token.getToken());
	}

	@Override
	public void before(MethodHandler methodHandler) {
		Authentication auth =MethodHelper.getMethodHelper(methodHandler.getMethod()).getAnnotation(Authentication.class);
		if(auth==null)
			auth = ClassHelper.getClassHelper(methodHandler.getPlugsProxy().getProxyClass()).getAnnotation(Authentication.class);
		Token token = Token.getToken();
		if(token == null)
			throw new RuntimeException("Method "+methodHandler.getMethod()+" invoke need provide token");
		if(!token.containerRole(auth.roles()))
			throw new RuntimeException("No permission to invoke method:"+methodHandler.getMethod());
	}

	@Override
	public void after(MethodHandler methodHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

}
