package com.YaNan.frame.hibernate.database.cache;

import java.util.HashMap;
import java.util.Map;

public class GeneralCache {
	private static SqlCache sqlCache;
	private Map<Integer,String> cache = new HashMap<Integer,String>();
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
	public String addSql(int ident,String sql){
		return this.cache.put(ident,sql);
	}
}
