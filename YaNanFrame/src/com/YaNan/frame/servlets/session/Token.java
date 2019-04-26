package com.YaNan.frame.servlets.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.servlets.session.entity.TokenCell;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenListener;
/**
 * 
 * @author Administrator
 * @version 101
 */
public class Token {
	private String tokenId;
	private int timeOut=-1;//超时
	private static List<TokenListener> listenerList;//监听列表
	private Map<String, Object> arguments = new HashMap<String, Object>();
	private Map<String,ArrayList<Object>> listArguments = new HashMap<String,ArrayList<Object>>();
	private volatile Set<String> roles = new HashSet<String>();
	private boolean valid;
	private long lastuse;
	private Logger log = LoggerFactory.getLogger( Token.class);
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
	public Set<Entry<String, Object>> attributeEntry() {
		return this.arguments.entrySet();
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
		this.timeOut = TokenManager.Timeout;
		this.initListener();
	}
	private void initListener() {
		if(listenerList ==null){
			listenerList = new ArrayList<TokenListener>();
			List<RegisterDescription> registerList = PlugsFactory.getRegisterList(TokenListener.class);
			for(RegisterDescription reg : registerList){
				try {
					listenerList.add(reg.getRegisterInstance(TokenListener.class));
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		for(TokenListener listener : listenerList){
			listener.init(this);
		}
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
	public static Token getToken(){
		Token token = TokenPool.getToken();
			if(token==null)
				token = Token.addToken(Token.newTokenId());
		return token;
	}
	public static void delete(){
		TokenPool.deleteToken();
	}
	public static Token getToken(String tokenId){
		if(tokenId==null)return null;//如果tokenId为null 则返回null
		Token token = TokenPool.getToken(tokenId);//从缓存中获取Token
		if(token==null&&TokenManager.getHibernateInterface()!=null){//如果缓存中没有，且有持久化接口时，常识从持久层接口中获取
			TokenCell tokenCell = TokenManager.getHibernateInterface().getToken(tokenId);
			if(tokenCell !=null){
				token = new Token(tokenId);
				if(token.getTimeOut()<0)
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
		writeCookie(request, response);
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
	private static void writeCookie(HttpServletRequest request,HttpServletResponse response) {
		if(request==null||response==null)
			throw new RuntimeException("No servlet context!");
		Token token = Token.getToken();
		Cookie cookie = new Cookie(TokenManager.TokenMark,token.getTokenId());
		String path = TokenManager.path;
		if(path==null)
			path = request.getContextPath().length()==0?"/":request.getContextPath();
		cookie.setPath(path);
		cookie.setMaxAge(token.getTimeOut());
		cookie.setHttpOnly(TokenManager.HttpOnly);
		cookie.setSecure(TokenManager.secure);//启用时 非 https 不能获取到ID
		response.addCookie(cookie);
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
	public Set<String> getRoles() {
		return roles;
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
	/**
	 * 是否包含角色中某个角色
	 * @param roles
	 * @return
	 */
	public boolean containerRole(String[] roles) {
		for (String role : roles) {
			if(isRole(role)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 是否是某角色
	 * @param role
	 * @return
	 */
	public boolean isRole(Class<?> role){
		return isRole(role.getSimpleName());
	}
	/**
	 * 是否是某角色
	 * @param role
	 * @return
	 */
	public boolean isRole(String role) {
		return roles.contains(role);
	}
	/**
	 * 是否包含角色
	 * @param role
	 * @return
	 */
	public String[] containsRole(String[] role) {
		List<String> ls = new ArrayList<String>();
		for (String key : role) {
			if (roles.contains(key)) {
				ls.add(key);
			}
		}
		return ls.toArray(new String[] {});
	}
	/**
	 * 删除角色
	 * @param roles
	 */
	public void delRoles(Class<?>... roles){
		for(Class<?> role : roles){
			this.delRole(role);
		}
	}
	/**
	 * 删除角色
	 * @param role
	 */
	public void delRole(Class<?> role){
		this.delRole(role.getSimpleName());
	}
	/**
	 * 删除角色
	 * @param roles
	 */
	public void delRole(String... roles){
		for(String role : roles){
			this.delRole(role);
		}
	}
	/**
	 * 删除角色
	 * @param role
	 */
	public synchronized void delRole(String role) {
		roles.remove(role);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().removeRole(tokenId,role);
	}
	/**
	 * 添加角色
	 * @param roles
	 */
	public void addRoles(Class<?>... roles){
		for(Class<?> role : roles){
			this.addRole(role);
		}
	}
	/**
	 * 添加角色
	 * @param role
	 */
	public void addRole(Class<?> role){
		this.addRole(role.getSimpleName());
	}
	/**
	 * 添加角色
	 * @param roles
	 */
	public void addRoles(String... roles){
		for(String role : roles){
			this.addRole(role);
		}
	}
	/**
	 * 添加角色
	 * @param role
	 */
	public synchronized void addRole(String role) {
		this.roles.add(role);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().addRole(tokenId,role);
	}
	/**
	 * 重新设置角色
	 * @param roles
	 */
	public void setRoles(Class<?>... roles){
		this.delAllRole();
		for(Class<?> role:roles){
			this.addRole(role);
		}
	}
	/**
	 * 重新设置角色
	 * @param roles
	 */
	public void setRoles(String... roles) {
		this.delAllRole();
		for(String role:roles){
			this.addRole(role);
		}
	}
	/**
	 * 删除所有角色
	 */
	public synchronized void delAllRole(){
		this.roles.clear();
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().clearRole(tokenId);
	}

	// -------------------------------------role ------ end

	public static Map<String, Token> getTokens() {
		return TokenPool.tokens;
	}
	
	/**
	 * 销毁token
	 */
	public void destory(){
		TokenManager.removeToken(tokenId);
		for(TokenListener listener : listenerList)
			listener.destory(this);
		this.arguments.clear();
		this.roles.clear();
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
