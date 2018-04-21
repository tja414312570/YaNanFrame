package com.YaNan.frame.RPC.TokenSupport;

import com.YaNan.frame.servlets.session.Token;

public class RPCTokenDispatcher {
	public Object request(String tokenId,String cls) throws ClassNotFoundException{
		Class<?> beanClass = Class.forName(cls);
		Token token =Token.getToken(tokenId);
		if(token==null)
			return null;
		return token.get(beanClass);
	}
	
}
