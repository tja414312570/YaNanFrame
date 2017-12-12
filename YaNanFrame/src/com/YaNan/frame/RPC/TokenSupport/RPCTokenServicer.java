package com.YaNan.frame.RPC.TokenSupport;

import java.util.Hashtable;
import java.util.Map;

import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.TokenPool;
/**
 * 
 * @author Administrator
 *
 */
public class RPCTokenServicer {
	private static Map<String, Hashtable<String, String>> tokenMap = new Hashtable<String,Hashtable<String,String>>();
	//保存token
	public static void bind(TokenAction tokenAction) {
		bind(tokenAction.getKey(),tokenAction.getValue(),tokenAction.getTokenContext().getTokenId());
	}
	//获取token
	public static Token getToken(String key,String value){
		if(!tokenMap.containsKey(key)) return null;
		Hashtable<String,String> vTokenMap=tokenMap.get(key);
		if(!vTokenMap.containsKey(value))return null;
		String tokenId = vTokenMap.get(value);
		return TokenPool.getToken(tokenId);
	}
	//移除token
	public static void removeToken(TokenAction tokenAction){
		if(tokenMap.containsKey(tokenAction.getKey()))
			if(tokenMap.get(tokenAction.getKey()).contains(tokenAction.getValue()))
				tokenMap.get(tokenAction.getKey()).remove(tokenAction.getValue());
	}
	public static void bind(String key, String value, String id) {
		System.out.println("key:"+key);
		System.out.println("value:"+value);
		System.out.print("id:"+id);
		Hashtable<String,String> tTokenMap = new Hashtable<String,String>();
		tTokenMap.put(value,id);
		if(tokenMap.containsKey(key))
			tokenMap.replace(key, tTokenMap);
		else
		tokenMap.put(key, tTokenMap);
	}
	
}
