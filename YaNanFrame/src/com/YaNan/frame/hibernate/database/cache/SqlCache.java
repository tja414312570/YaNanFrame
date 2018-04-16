package com.YaNan.frame.hibernate.database.cache;

import java.util.HashMap;
import java.util.Map;

public class SqlCache {
	private static SqlCache sqlCache;
	//hashcode sql;
	private Map<Integer,String> cache = new HashMap<Integer,String>();
	//hashcode Objcet;
	private Map<Integer,Object> attributes = new HashMap<Integer,Object>();
	public static SqlCache getCache(){
		if(sqlCache==null)
			synchronized (SqlCache.class) {
				if(sqlCache==null)
					sqlCache=new SqlCache();
			}
		return sqlCache;
	}
	public String getSql(int ident){
		return this.cache.get(ident);
	}
	public void addSql(int ident,String sql){
		this.cache.put(ident,sql);
	}
	public <T> void addAttribute(int hash,T attribute){
		this.attributes.put(hash,attribute);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(int hash){
		return (T) this.attributes.get(hash);
	}
	
}
