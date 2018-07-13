package com.YaNan.frame.servlets.session;

import java.util.HashMap;
import java.util.Map;

public class TokenPool {
	public static Map<String, Token> tokens = new HashMap<String, Token>();
	public static Map<String, Token> getTokenMap() {
		return tokens;
	}
	public static Token getToken(String tokenId) {
		return tokens.get(tokenId);
	}
	public static void setToken(String tokenId, Token token) {
		TokenPool.tokens.replace(tokenId, token);
	}
	public static void addToken(Token token) {
		TokenPool.tokens.put(token.getTokenId(), token);
	}
	public static void deleteToken(Token token) {
		if(token!=null)
			TokenPool.tokens.remove(token.getTokenId());
	}
	public void destory() {
		tokens.clear();
		tokens=null;
	}
	public static boolean hasToken(Token token) {
		return tokens.containsKey(token.getTokenId());
	}
	public static boolean hasToken(String tokenId) {
		return tokens.containsKey(tokenId);
	}

	public static void removeToken(String tokenId) {
		tokens.remove(tokenId);
	}

}
