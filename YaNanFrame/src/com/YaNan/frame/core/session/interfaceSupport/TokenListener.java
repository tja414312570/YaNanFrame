package com.YaNan.frame.core.session.interfaceSupport;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.annotation.iToken;

public interface TokenListener {

	void onSuccess(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException;
	void onFailed(Token token, ServletRequest request, ServletResponse response, iToken itoken) throws Exception;
}
