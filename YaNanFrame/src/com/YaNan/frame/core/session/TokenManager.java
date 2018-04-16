package com.YaNan.frame.core.session;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.session.entity.Result;
import com.YaNan.frame.core.session.entity.TokenConfigure;
import com.YaNan.frame.core.session.entity.TokenEntity;
import com.YaNan.frame.core.session.interfaceSupport.TokenHibernateInterface;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.beanSupport.BeanFactory;
import com.YaNan.frame.hibernate.beanSupport.XMLBean;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;
import com.YaNan.frame.stringSupport.StringUtil;

public class TokenManager {
	/**
	 * 客户端token标示
	 */
	public static String TokenMark = "TUID"; 
	public static String RoleMark = "ROLES";
	public static int Timeout=3600;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, TokenManager.class);
	/**
	 * token 数据持久层接口
	 */
	public static TokenHibernateInterface hibernateInterface;
	
	private static TokenManager manager;
	private Map<String, TokenEntity> tokenMap = new LinkedHashMap<String, TokenEntity>();
	public Map<String, TokenEntity> getTokenMap() {
		return tokenMap;
	}
	private File file;
	private TokenManager() {
	};
	@Override
	public String toString(){
		return "cs";
	}
	public static String getTokenMark() {
		return TokenMark;
	}
	public static void setTokenMark(String tokenMark) {
		TokenMark = tokenMark;
	}
	public static TokenHibernateInterface getHibernateInterface() {
		return hibernateInterface;
	}
	public static void setHibernateInterface(TokenHibernateInterface hibernateInterface) {
		TokenManager.hibernateInterface = hibernateInterface;
	}
	public void addToken(String namespace, TokenEntity bean) {
		this.tokenMap.put(namespace, bean);
	}
	public void setInstance(TokenManager tokenManager){
		manager= tokenManager;
	}
	
	public static TokenManager getInstance() {
		if (manager == null)
			manager = new TokenManager();
		return manager;
	}

	public static void init() {
		if (manager == null)
			manager = new TokenManager();
		manager.file = new File(WebPath.getWebPath().getClassPath().realPath
				+ "Token.xml");
		manager.index();
	}

	public static void init(TokenManager tokenManager) {
		manager = tokenManager;
		manager.file = new File(WebPath.getWebPath().getClassPath().realPath
				+ "Token.xml");
		manager.index();
	}
	// index token XML file;
	private void index() {
		if(!this.file.exists()){
			log.error("token xml file is not exists path:"+this.file.getAbsoluteFile());
			return;
		}
		XMLBean cmlBean = BeanFactory.getXMLBean();
		cmlBean.addXMLFile(this.file);
		cmlBean.setBeanClass(TokenConfigure.class);
		cmlBean.addElementPath("/Token");
		cmlBean.setNodeName("Config");
		List<?> cmls = cmlBean.execute();
		if(cmls.size()!=0){
			TokenConfigure configure = (TokenConfigure) cmls.get(0);
			if(configure.getTokenMark()!=null&&!configure.getTokenMark().equals(""))
				TokenMark=configure.getTokenMark();
			Timeout=configure.getTimeout();
			if(configure.getHibernateInterface()!=null&&ClassLoader.exists(configure.getHibernateInterface())){
				Class<?> cls;
				try {
					cls = Class.forName(configure.getHibernateInterface());
					if(ClassLoader.implementOf(cls, TokenHibernateInterface.class))
						hibernateInterface=(TokenHibernateInterface) cls.newInstance();
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				log.error(e.getMessage(), e);
				}
				
			}
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
			log.debug(
					"add token namespace :" + tokenBean.getNamespace() + " ; "
							+ tokenBean.toString());
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

	public static boolean hasNameSpace(String namespace) {
		if(manager!=null){
			Iterator<String> iterator = manager.tokenMap.keySet().iterator();
			while(iterator.hasNext()){
				if(StringUtil.match(namespace, iterator.next()))
					return true;
			}
		}
		return false;
	}

	public static List<TokenEntity> getTokenEntitys(String namespace) {
		List<TokenEntity> tl= new ArrayList<TokenEntity>();
		Iterator<String> iterator = manager.tokenMap.keySet().iterator();
		while(iterator.hasNext()){
			String nameReg =  iterator.next();
			if(StringUtil.match(namespace,nameReg))
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
