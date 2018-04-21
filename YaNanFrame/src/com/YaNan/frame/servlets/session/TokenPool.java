package com.YaNan.frame.servlets.session;

import java.util.HashMap;
import java.util.Map;

public class TokenPool {
	public static interface Response {
		public void onExist();

		public void onDestory();

		public void onNotExists();
	}

	public static Map<String, Token> tokens = new HashMap<String, Token>();

	public static Token getToken(String tokenId) {
		if (tokens.containsKey(tokenId)) {
			return tokens.get(tokenId);
		}
		return null;
	}

	public static void setToken(String tokenId, Token token) {
		TokenPool.tokens.replace(tokenId, token);
	}

	public static void addToken(Token token) {
		if (TokenPool.tokens.containsKey(token.getTokenId())) {
			tokens.replace(token.getTokenId(), token);
		} else {
			TokenPool.tokens.put(token.getTokenId(), token);
		}
	}

	public static void deleteToken(Token token) {
		if(token!=null){
			String tokenId = token.getTokenId();
			if(tokens.containsKey(tokenId))
				TokenPool.tokens.remove(tokenId);
		}
			
	}

	public void destory() {

	}

	public static boolean hasToken(Token token) {
		if (tokens.containsKey(token.getTokenId()))
			return true;
		return false;
	}

	public static boolean hasToken(String tokenId) {

		return tokens.containsKey(tokenId);
	}

	public static void removeToken(String tokenId) {
		if(tokens.containsKey(tokenId))
			tokens.remove(tokenId);
	}

}
