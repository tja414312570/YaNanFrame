package com.YaNan.frame.servlets.session;

import java.util.HashMap;
import java.util.Map;

/**
 * 令牌池，令牌池化管理
 * @author yanan
 *
 */
public class TokenPool {
	public static Map<String, Token> tokens = new HashMap<String, Token>();
	private static InheritableThreadLocal<Token> tokenLocal = new InheritableThreadLocal<Token>();
	public static Map<String, Token> getTokenMap() {
		return tokens;
	}
	public static Token getToken(){
		return tokenLocal.get();
	}
	public static Token getToken(String tokenId) {
		Token token = tokens.get(tokenId);
		tokenLocal.set(token);
		return token;
	}
	public static void setToken(String tokenId, Token token) {
		tokenLocal.set(token);
		TokenPool.tokens.replace(tokenId, token);
	}
	public static void addToken(Token token) {
		tokenLocal.set(token);
		TokenPool.tokens.put(token.getTokenId(), token);
	}
	public static void deleteToken(Token token) {
		tokenLocal.remove();
		if(token!=null)
			TokenPool.tokens.remove(token.getTokenId());
	}
	public static void deleteToken() {
		Token token = Token.getToken();
		TokenPool.deleteToken(token);
	}
	public void destory() {
		tokens.clear();
		tokens=null;
	}
	public static boolean hasToken(Token token) {
		return tokens.containsKey(token.getTokenId())&&tokenLocal.get()!=null;
	}
	public static boolean hasToken(String tokenId) {
		return tokens.containsKey(tokenId)&&tokenLocal.get()!=null;
	}

	public static void removeToken(String tokenId) {
		tokenLocal.remove();
		tokens.remove(tokenId);
	}

}
