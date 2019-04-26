package com.YaNan.frame.servlets.session;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.ConfigContext;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.session.entity.Result;
import com.YaNan.frame.servlets.session.entity.TokenEntity;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenHibernateInterface;
import com.YaNan.frame.utils.PathMatcher;
import com.YaNan.frame.utils.beans.BeanFactory;
import com.YaNan.frame.utils.beans.XMLBean;
import com.typesafe.config.Config;

public class TokenManager{
	/**
	 * 客户端token标示
	 */
	public static String TokenMark = "TUID"; 
	public static String RoleMark = "ROLES";
	public static int Timeout=3000;//默认超时
	public static String path;//cookie有效域
	public static boolean secure = false;//启用secure
	public static boolean HttpOnly = true;//启用HttpOnly
	private final Logger log = LoggerFactory.getLogger( TokenManager.class);
	/**
	 * token 数据持久层接口
	 */
	private TokenHibernateInterface hibernateInterface = PlugsFactory.getPlugsInstanceAllowNull(TokenHibernateInterface.class);
	private final Thread tokenDeamon;
	private final TokenLifeDeamon tokenLifeTask;
	private static TokenManager manager;
	private Map<String, TokenEntity> tokenMap = new LinkedHashMap<String, TokenEntity>();
	public Map<String, TokenEntity> getTokenMap() {
		return tokenMap;
	}
	private File file;
	private TokenManager() {
		tokenLifeTask = new TokenLifeDeamon();
		tokenDeamon = new Thread(tokenLifeTask);
		tokenDeamon.setDaemon(true);
		tokenDeamon.start();
	};
	/**
	 * shutdown the token life cycle manager thread
	 */
	public void destory(){
		tokenLifeTask.shutdown();
		if(tokenDeamon.isAlive())
			tokenDeamon.interrupt();
	}
	public static String getTokenMark() {
		return TokenMark;
	}
	public static void setTokenMark(String tokenMark) {
		TokenMark = tokenMark;
	}
	public static TokenHibernateInterface getHibernateInterface() {
		if(manager!=null)
			return manager.hibernateInterface;
		return null;
	}
	public void setHibernateInterface(TokenHibernateInterface hibernateInterface) {
		this.hibernateInterface = hibernateInterface;
	}
	public void addToken(String namespace, TokenEntity bean) {
		this.tokenMap.put(namespace, bean);
	}
	public void setInstance(TokenManager tokenManager){
		manager= tokenManager;
	}
	
	public static TokenManager getInstance() {
		if (manager == null)
			synchronized (manager) {
				if(manager==null)
					manager = new TokenManager();
			}
		return manager;
	}
	public static boolean isInstance(){
		return manager !=null;
	}
	public static void init() {
		if (manager == null)
			manager = new TokenManager();
		manager.file = new File(TokenManager.class.getClassLoader().getResource("").getPath().replace("%20"," ")
				,"Token.xml");
		manager.index();
	}
	public static void init(TokenManager tokenManager) {
		manager = tokenManager;
		manager.file = new File(TokenManager.class.getClassLoader().getResource("").getPath().replace("%20"," ")
				,"Token.xml");
		manager.index();
	}
	// index token XML file;
	private void index() {
		if(!this.file.exists()){
			log.error("token xml file is not exists path:"+this.file.getAbsoluteFile());
			return;
		}
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(this.file);
		xmlBean.addElementPath("/Token");
		xmlBean.setNodeName("token");
		xmlBean.addNameMaping("class", "CLASS");
		xmlBean.addNameMaping("default", "DEFAULT");
		xmlBean.addMapMapping("result",Result.class);
		xmlBean.setBeanClass(TokenEntity.class);
		Iterator<Object> i = xmlBean.execute().iterator();
		while (i.hasNext()) {
			TokenEntity tokenBean = (TokenEntity) i.next();
			manager.addToken(tokenBean.getNamespace(), tokenBean);
		}
		Config conf = ConfigContext.getConfig("Token");
		if(conf!=null){
			conf.allowKeyNull();
			Timeout = conf.getInt("Timeout",3000);
			path = conf.getString("Path",null);
			secure = conf.getBoolean("Secure",false);
			HttpOnly =  conf.getBoolean("HttpOnly",true);
		}
	}

	public static class TokenBean {
		private String namespace;

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public Failed getFailed() {
			return failed;
		}

		public void setFailed(Failed failed) {
			this.failed = failed;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		private String role;
		private Failed failed;

		@Override
		public String toString() {
			return "TokenBean [namespace=" + namespace + ", role=" + role
					+ ", failed=" + failed + ", value=" + value + ", chain="
					+ chain + "]";
		}

		private String value;
		private String chain;

		public String getChain() {
			return chain;
		}

		public void setChain(String chain) {
			this.chain = chain;
		}

		public static class Failed {
			private String command;

			@Override
			public String toString() {
				return "Failed [command=" + command + ", value=" + value + "]";
			}

			public String getCommand() {
				return command;
			}

			public void setCommand(String command) {
				this.command = command;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

			private String value;
		}
	}

	public static boolean match(String namespace) {
		if(manager!=null){
			Iterator<String> iterator = manager.tokenMap.keySet().iterator();
			while(iterator.hasNext()){
				if(PathMatcher.match(iterator.next(), namespace).isMatch())
					return true;
			}
		}
		return false;
	}

	public static List<TokenEntity> getTokenEntitys(String url) {
		List<TokenEntity> tl= new ArrayList<TokenEntity>();
		Iterator<String> iterator = manager.tokenMap.keySet().iterator();
		while(iterator.hasNext()){
			String nameReg =  iterator.next();
			if(PathMatcher.match(nameReg, url).isMatch())
				tl.add(manager.tokenMap.get(nameReg));
		}
		return tl;
	}
	public static void removeToken(String tokenId) {
		TokenPool.removeToken(tokenId);
	}
	public static void addToken(Token token) {
		TokenPool.addToken(token);
	}

}
