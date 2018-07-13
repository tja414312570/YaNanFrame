package com.YaNan.frame.servlets.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.servlets.session.entity.TokenCell;
/**
 * 
 * @author Administrator
 * @version 101
 */
public class Token {
	private String tokenId;
	public final static int MaxTimeout = Integer.MAX_VALUE;
	private int timeOut=864000;//超时
	private Map<String, Object> arguments = new HashMap<String, Object>();
	private Map<String,ArrayList<Object>> listArguments = new HashMap<String,ArrayList<Object>>();
	private List<String> roles = new ArrayList<String>();
	private boolean valid;
	private long lastuse;
	/************************超时，有效***************/
	public int getTimeOut() {
		return timeOut;
	}
	/**
	 * 设置token超时，以分钟计算
	 * @param maxTimeOut
	 */
	public void setTimeOut(int maxTimeOut) {
		timeOut = maxTimeOut;
	}
	public boolean isValid(){
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/*******************************存储*************************/
	public boolean container(Class<?> cls) {
		return this.arguments.containsKey(cls.getName());
	}
	public boolean container(String key){
		return this.arguments.containsKey(key);
	}
	/**
	 * Delete all data, including persistent data
	 */
	public void clearAll(){
		this.arguments.clear();
		this.listArguments.clear();
	}
	public void clear(){
		this.arguments.clear();
	}
	public void set(String key, Object value) {
		this.arguments.put(key, value);
	}
	public void set(Class<?> cls, Object value) {
		this.arguments.put(cls.getName(), value);
	}
	public Object get(String key) {
		return arguments.get(key);
	}
	public void remove(String key){
		this.arguments.remove(key);
	}
	public void set(Object obj) {
		this.arguments.put(obj.getClass().getName(), obj);
	}
	public Object get(Class<?> cls) {
		return this.arguments.get(cls.getName());
	}
	public void remove(Class<?> cls) {
		this.arguments.remove(cls.getName());
	}
	/***********************************存储结束*****************/
	
	/**
	 * 默认构造方法
	 * @param tokenId
	 */
	private Token(String tokenId) {
		this.tokenId = tokenId;
	}
	private Token() {
	}
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	/**
	 * 系统中是否包含token id
	 * @param request
	 * @return
	 */
	public static boolean cotainerToken(HttpServletRequest request){
		String tokenId = getTokenId(request);
		if(tokenId==null)return false;
		if(TokenPool.hasToken(tokenId))return true;
		if(TokenManager.getHibernateInterface().containerToken(tokenId))return true;
		return false;
	}
	/**
	 * 获取token
	 * @param requestContext
	 * @return
	 */
	public static Token getToken(HttpServletRequest requestContext){
		Token token = getToken(getTokenId(requestContext));
			if(token==null)
				token = getToken((String)requestContext.getAttribute(TokenManager.TokenMark));
		return token;
	}
	public static Token getToken(String tokenId){
		if(tokenId==null)return null;//如果tokenId为null 则返回null
		Token token = TokenPool.getToken(tokenId);//从缓存中获取Token
		if(token==null&&TokenManager.getHibernateInterface()!=null){//如果缓存中没有，且有持久化接口时，常识从持久层接口中获取
			TokenCell tokenCell = TokenManager.getHibernateInterface().getToken(tokenId);
			if(tokenCell !=null){
				token = new Token(tokenId);
				token.setTimeOut(tokenCell.getTimeOut());
				TokenPool.addToken(token);
			}
		}
		if(token!=null)
			token.setLastuse(System.currentTimeMillis());
		return token;
	}
	/**
	 * 从request中获取token id
	 * @param requestContext
	 * @return
	 */
	private static String getTokenId(HttpServletRequest requestContext) {
		Cookie[] cookies = requestContext.getCookies();
		if (cookies == null||cookies.length==0) {
			return requestContext.getParameter(TokenManager.TokenMark);
		} else {
			for(int i = 0;i<cookies.length;i++){
				if(cookies[i].getName().equals(TokenManager.TokenMark))
					return cookies[i].getValue();
			}
		}
		return null;
	}
	private static Token addToken(String tokenId) {
		Token token = new Token(tokenId);
		token.setTimeOut(TokenManager.Timeout);
		token.setLastuse(System.currentTimeMillis());
		TokenPool.addToken(token);
		return token;
	}
	/**
	 * 新增token
	 * @param request
	 * @param response
	 */
	public static Token addToken(HttpServletRequest request,HttpServletResponse response){
		String tokenId=newTokenId();
		Token token = addToken(tokenId);
		Cookie cookie = new Cookie(TokenManager.TokenMark,tokenId);
		cookie.setPath("/");
		cookie.setMaxAge(token.getTimeOut());
		response.addCookie(cookie);
		request.setAttribute(TokenManager.TokenMark, tokenId);
		if(TokenManager.getHibernateInterface()!=null){
			TokenCell tc = new TokenCell();
			tc.setTokenId(tokenId);
			tc.setCreateDate(new Date());
			tc.setTimeOut(token.getTimeOut());
			TokenManager.getHibernateInterface().addToken(tc);
		}
		request.setAttribute(TokenManager.TokenMark, tokenId);
		return token;
	}
	/**
	 * 产生新的token Id
	 * @return
	 */
	private static String newTokenId() {
		UUID uuid = UUID.randomUUID();
		return new StringBuilder(32).append(digits(uuid.getMostSignificantBits() >> 32, 8))
				.append(digits(uuid.getMostSignificantBits() >> 16, 4))
				.append(digits(uuid.getMostSignificantBits(), 4))
				.append(digits(uuid.getLeastSignificantBits() >> 48, 4))
				.append(digits(uuid.getLeastSignificantBits(), 12)).toString();
	}
	public boolean exists() {
		if (TokenPool.hasToken(this))
			return true;
		return false;
	}
	// --------------------------role -----start
	public Object[] getRoles() {
		return roles.toArray();
	}

	public Iterator<String> getRoleIterator() {
		return this.roles.iterator();
	}
	public List<String> matchRoles(String[] role) {
		List<String> roles = new ArrayList<String>();
		for (String key : role) {
			Iterator<String> rrole = roles.iterator();
			while (rrole.hasNext()) {
				String rrolei = rrole.next();
				if (key.equals(rrolei))
					roles.add(rrolei);
			}
		}
		return roles;
	}

	public boolean containerRole(String[] role) {
		for (String key : role) {
			Iterator<String> rrole = roles.iterator();
			while (rrole.hasNext()) {
				if (key.equals(rrole.next()))
					return true;
			}
		}
		return false;
	}

	public boolean isRole(String role) {
		Iterator<String> rrole = roles.iterator();
		while (rrole.hasNext()) {
			String r = rrole.next();
			if (role.equals(r)){
				return true;
			}
				
		}
		return false;
	}

	public String[] containsRole(String[] role) {
		List<String> ls = new ArrayList<String>();
		for (String key : role) {
			if (roles.contains(key)) {
				ls.add(key);
			}
		}
		return ls.toArray(new String[] {});
	}

	public void delRole(String role) {
		roles.remove(role);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().removeRole(tokenId,role);
	}

	public void addRole(String role) {
		this.roles.add(role);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().addRole(tokenId,role);
	}

	// -------------------------------------role ------ end

	public static Map<String, Token> getTokens() {
		return TokenPool.tokens;
	}
	
	public void delAllRole(){
		this.roles.clear();
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().clearRole(tokenId);
	}
	
	public void setRoles(String roles) {
		this.roles.clear();
		if(TokenManager.getHibernateInterface()!=null){
			TokenManager.getHibernateInterface().clearRole(tokenId);
			String[] role = roles.split(",");
			for(String str:role){
				TokenManager.getHibernateInterface().addRole(tokenId, str);
			}
		}
		String[] role = roles.split(",");
		for(String str:role){
			this.roles.add(str);
		}
			
	}
	/**
	 * 销毁token
	 */
	public void destory(){
		this.arguments.clear();
		this.roles.clear();
		TokenManager.removeToken(tokenId);
		tokenId=null;
	}
	public static String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return Long.toHexString(hi | (val & (hi - 1))).substring(1);
	}
	public long getLastuse() {
		return lastuse;
	}
	public void setLastuse(long lastuse) {
		this.lastuse = lastuse;
	}
}
