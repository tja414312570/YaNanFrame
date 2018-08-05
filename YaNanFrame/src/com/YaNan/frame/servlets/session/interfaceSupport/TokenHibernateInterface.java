package com.YaNan.frame.servlets.session.interfaceSupport;


import com.YaNan.frame.servlets.session.entity.TokenCell;

public interface TokenHibernateInterface {
/************************token 基础部分***********************/
	boolean containerToken(String tokenId);

	void addToken(TokenCell tokenCell);

	TokenCell getToken(String tokenId);
	
	void destory(String tokenId);
/****************************数据持久化部分*******************/
/*****map**********/
	Object get(String tokenId, String key);

	void set(String tokenId, String key, Object value);
	
	void clear(String tokenId);
	
	void remove(String tokenId,String key);
/****************************角色部分*************************/

	void addRole(String tokenId, String... role);

	void clearRole(String tokenId);

	void removeRole(String tokenId, String role);



}
