package com.YaNan.frame.RPC.TokenSupport;

import com.YaNan.frame.core.session.Token;
import com.google.gson.Gson;

public class RPCTokenDispatcher {
	public Object request(String tokenId,String cls) throws ClassNotFoundException{
		Class<?> beanClass = Class.forName(cls);
		Token token =Token.getToken(tokenId);
		if(token==null)
			return null;
		System.out.println(beanClass);
		return new Gson().toJson(token.get(beanClass));
	}
	
}
