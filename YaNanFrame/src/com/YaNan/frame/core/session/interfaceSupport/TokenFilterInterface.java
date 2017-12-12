package com.YaNan.frame.core.session.interfaceSupport;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.YaNan.frame.core.session.Token;

public interface TokenFilterInterface {
	public String excute(ServletRequest request, ServletResponse response, Token token);
}
