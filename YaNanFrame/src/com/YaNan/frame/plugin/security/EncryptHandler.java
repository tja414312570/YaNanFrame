package com.YaNan.frame.plugin.security;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.plugin.security.Encrypt;
import com.YaNan.frame.plugin.security.EncryptService;

@Support(Encrypt.class)
@Register(signlTon = true)
public class EncryptHandler implements InvokeHandler {
	/**
	 * 加密类实现类可以定义为单例
	 */
	private EncryptService encryptService = PlugsFactory.getPlugsInstance(EncryptService.class);

	@Override
	public Object before(MethodHandler methodHandler) {
		Object[] parameters = methodHandler.getParameters();
		try {
			for (int i = 0; i < parameters.length; i++)
				parameters[i] = encryptService.descrypt(parameters[i]);
			methodHandler.chain();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object after(MethodHandler methodHandler) {
		Object result = methodHandler.getOriginResult();
		try {
			return encryptService.encrypt(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object error(MethodHandler methodHandler, Exception e) {
		methodHandler.chain();
		return null;
	}

}
