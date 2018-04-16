package com.YaNan.frame.core.session;

import java.text.SimpleDateFormat;
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

import com.YaNan.frame.core.session.entity.TokenCell;
/**
 * 
 * @author Administrator
 * @version 101
 */
public class Token {
	private String tokenId;
	public final static int MaxTimeout = Integer.MAX_VALUE;
	private static int timeOut=864000;
	private Map<String, Object> arguments = new HashMap<String, Object>();
	private Map<String,String> strArguments = new HashMap<String,String>();
	private Map<String,Integer> intArguments = new HashMap<String,Integer>();
	private Map<String,ArrayList<Object>> listArguments = new HashMap<String,ArrayList<Object>>();
	private List<String> roles = new ArrayList<String>();
	private Map<String,Object> hibernateDatas = new HashMap<String,Object>();
	private Map<Class<?>, Object> objs = new HashMap<Class<?>, Object>();
	private boolean valid;
	private Date createDate;
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
		return this.objs.containsKey(cls);
	}
	public boolean container(String key){
		return this.arguments.containsKey(key);
	}
	/**
	 * Delete all data, including persistent data
	 */
	public void clearAll(){
		this.arguments.clear();
		this.hibernateDatas.clear();
		this.intArguments.clear();
		this.listArguments.clear();
		this.strArguments.clear();
	}
	public void clear(){
		this.arguments.clear();
	}
	public void set(String key, Object value) {
		this.arguments.put(key, value);
	}
	public Object get(String key) {
		return arguments.get(key);
	}
	public void remove(String key){
		this.arguments.remove(key);
	}
	public String getString(String key){
		return this.strArguments.get(key);
	}
	public void setString(String key,String value){
		this.strArguments.put(key, value);
	}
	public void clearString(){
		this.strArguments.clear();
	}
	public int getInt(String key){
		return this.intArguments.get(key);
	}
	public void setInt(String key,int value){
		this.intArguments.put(key, value);
	}
	public void clearInt(){
		this.intArguments.clear();
	}
	public void set(Object obj) {
		if (objs.containsKey(obj.getClass())) {
			objs.replace(obj.getClass(), obj);
		} else {
			objs.put(obj.getClass(), obj);
		}
	}
	public void clearClass(){
		this.objs.clear();
	}
	public void set(Class<?> cls, Object value) {
		this.objs.put(cls, value);
	}
	public Object get(Class<?> cls) {
		return objs.get(cls);
	}
	public void remove(Class<?> cls) {
		this.objs.remove(cls);
	}
	/***********************************存储结束*****************/
	/*******************************持久化数据*******************/
	public void setHibernate(String key,Object value){
		this.hibernateDatas.put(key, value);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().set(tokenId,key,value);
	}
	public Object getHibernate(String key){
		if(this.hibernateDatas.containsKey(key))
			return this.hibernateDatas.get(key);
		if(TokenManager.getHibernateInterface()!=null)
			return TokenManager.getHibernateInterface().get(tokenId,key);
		return null;
	}
	public void removeHibernate(String key){
		this.hibernateDatas.remove(key);
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().remove(tokenId, key);
	}
	public void clearHibernate(){
		this.hibernateDatas.clear();
		if(TokenManager.getHibernateInterface()!=null)
			TokenManager.getHibernateInterface().clear(tokenId);
	}
	/********************************持久化数据完成********************/
	
	
	/**
	 * 默认构造方法
	 * @param tokenId
	 */
	private Token(String tokenId) {
		this.tokenId = tokenId;
		this.createDate=new Date();
	}
	private Token() {
		this.createDate=new Date();
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
		String tokenId = getTokenId(requestContext);
		return getToken(tokenId);
	}
	public static Token getToken(String tokenId){
		if(tokenId==null)return null;
		if(TokenPool.hasToken(tokenId))
			return TokenPool.getToken(tokenId);
		if(TokenManager.getHibernateInterface()!=null)
		if(TokenManager.getHibernateInterface().containerToken(tokenId)){
			TokenCell tokenCell = TokenManager.getHibernateInterface().getToken(tokenId);
			Token token = new Token(tokenId);
			token.setCreateDate(tokenCell.getCreateDate());
			if(token!=null)
				TokenPool.addToken(token);
			return token;
		}
		return null;
	}
	/**
	 * 从request中获取token id
	 * @param requestContext
	 * @return
	 */
	private static String getTokenId(HttpServletRequest requestContext) {
		String cookie = requestContext.getHeader("Cookie");
		String tokenMark = TokenManager.getTokenMark();
		if (cookie == null) {
			cookie = requestContext.getParameter(tokenMark);
		} else {
			if (cookie.contains(tokenMark)) {
				cookie = cookie.substring(cookie.lastIndexOf(tokenMark)).replace("=", "");
				if (cookie.contains(";"))
					cookie = cookie.substring(0, cookie.indexOf(";"));
			}
			cookie = cookie.replace(tokenMark,"");
		}
		return cookie;
	}
	
	private static Token addToken(String tokenId) {
		Token token = new Token(tokenId);
		token.setTimeOut(TokenManager.Timeout);
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
		Cookie cookie = new Cookie(TokenManager.getTokenMark(),tokenId);
		cookie.setPath("/");
		cookie.setMaxAge(timeOut);
		response.addCookie(cookie);
		if(TokenManager.getHibernateInterface()!=null){
			TokenCell tc = new TokenCell();
			tc.setTokenId(tokenId);
			tc.setCreateDate(new Date());
			TokenManager.getHibernateInterface().addToken(tc);
		}
		request.getSession().setAttribute(TokenManager.getTokenMark(), tokenId);
		return addToken(tokenId);
	}
	/**
	 * 产生新的token Id
	 * @return
	 */
	private static String newTokenId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		date=Integer.toHexString(Integer.valueOf(date));
		String uuid = UUID.randomUUID().toString();
		String tokenId=date+uuid.replace("-", "");
		return tokenId;
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

	public boolean isRole(String[] role) {
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
		this.objs.clear();
		TokenManager.removeToken(tokenId);
		tokenId=null;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
