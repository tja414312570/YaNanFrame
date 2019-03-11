package com.YaNan.frame.servlets.session.interfaceSupport;

import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.servlets.session.Token;

/**
 * Token监听
 * @author yanan
 *
 */
@Service
public interface TokenListener {
	
	void init(Token token);
	
	void destory(Token token);
	
}
