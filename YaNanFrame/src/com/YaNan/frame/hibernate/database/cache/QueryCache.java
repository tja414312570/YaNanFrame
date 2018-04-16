package com.YaNan.frame.hibernate.database.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueryCache {
	private static QueryCache queryCache;
	//sql ==> result
	private Map<String,Object> sqlCache = new Hashtable<String,Object>();
	//tableName ==>list <sql>
	private Map<String,List<String>> tabCache = new Hashtable<String,List<String>>();
	public static QueryCache getCache(){
		if(queryCache==null)
			synchronized (QueryCache.class) {
				if(queryCache==null)
					queryCache=new QueryCache();
			}
		return queryCache;
	}
	@SuppressWarnings("unchecked")
	public <T> T getQuery(String sql){
		return (T) this.sqlCache.get(sql);
	}
	public <T> void addQuery(String tableName,String sql,T t){
		this.sqlCache.put(sql, t);
		if(tabCache.get(tableName)!=null)
			this.tabCache.get(tableName).add(sql);
		else{
			List<String> list = new ArrayList<String>();
			list.add(sql);
			this.tabCache.put(tableName, list);
		}
	}
	public void cleanCache(){
		this.sqlCache.clear();
		this.tabCache.clear();
	}
	public void cleanCache(String tableName){
		if(tabCache.get(tableName)!=null){
			Iterator<String> iterator = tabCache.get(tableName).iterator();
			while(iterator.hasNext())
				this.sqlCache.remove(iterator.next());
			tabCache.get(tableName).clear();
			tabCache.remove(tableName);
		}
	}
}
