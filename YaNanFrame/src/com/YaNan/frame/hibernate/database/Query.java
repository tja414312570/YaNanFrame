package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.hibernate.database.cache.SqlCache;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.util.StringUtil;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Query extends OperateImplement{
	protected List<String> key = new ArrayList<String>();
	protected DBTab queryTab;
	protected Query unionQuery=null;
	protected boolean unionAll=false;
	protected Object object;
	protected Class<?> cls;
	protected Map<String, String> map = new HashMap<String, String>();
	protected List<String> condition = new ArrayList<String>();
	protected List<String> order = new ArrayList<String>();
	protected Map<Field, DBColumn> fieldMap = new LinkedHashMap<Field, DBColumn>();
	protected String limit="";
	protected String group=null;
	private Query subQuery=null;
	private JoinObject joinObject;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,Query.class);
	public Class<?> getCls() {
		return cls;
	}
	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	public static interface Order{
		public static final String Desc="desc";
	}
	public List<String> getKey() {
		return key;
	}
	public int ident(){
		
		return this.identNC()+map.hashCode()+condition.hashCode();
	}
	public int identNC(){
		int hashCode = "Query".hashCode();
		hashCode+=key.hashCode();
		hashCode+=queryTab.getName().hashCode();
		if(unionQuery!=null)
			hashCode+=unionQuery.hashCode();
		hashCode+=unionAll==true?0:1;
		if(object!=null)
			hashCode+=object.hashCode();
		if(cls!=null)
			hashCode+=cls.hashCode();
		hashCode+=order.hashCode();
		hashCode+=fieldMap.hashCode();
		hashCode+=limit.hashCode();
		if(group!=null)
			hashCode+=group.hashCode();
		if(subQuery!=null)
			hashCode+=subQuery.hashCode();
		if(joinObject!=null)
			hashCode+=joinObject.hashCode();
		return hashCode;
	}
	public boolean equals(Query query){
		//hash code create ;
		if(!query.key.equals(this.key))
			return false;
		if(!query.queryTab.getName().equals(this.queryTab.getName()))
			return false;
		if(query.unionQuery!=null){
			if(this.unionQuery==null)
				return false;
			else if (!this.unionQuery.equals(this.unionQuery))
				return false;
		}else if(this.unionQuery!=null)
			return false;
		if(query.unionAll!=this.unionAll)
			return false;
		if(!query.map.equals(this.map))
			return false;
		if(!query.condition.equals(this.condition))
			return false;
		if(!query.order.equals(this.order))
			return false;
		if(!query.fieldMap.equals(this.fieldMap))
			return false;
		if(!query.limit.equals(this.limit))
			return false;
		if(query.joinObject!=null){
			if(this.joinObject==null)
				return false;
			else if (!this.joinObject.equals(this.joinObject))
				return false;
		}else if(this.joinObject!=null)
			return false;
		if(query.group!=null){
			if(this.group==null)
				return false;
			else if (!this.group.equals(this.group))
				return false;
		}else if(this.group!=null)
			return false;
		if(query.subQuery!=null){
			if(this.subQuery==null)
				return false;
			else if (!this.subQuery.equals(this.subQuery))
				return false;
		}else if(this.subQuery!=null)
			return false;
		if(query.joinObject!=null){
			if(this.joinObject==null)
				return false;
			else if (!this.joinObject.equals(this.joinObject))
				return false;
		}else if(this.joinObject!=null)
			return false;
		if(query.object!=null){
			if(this.object==null)
				return false;
			else if (!this.object.equals(this.object))
				return false;
		}else if(this.object!=null)
			return false;
		return true;
	}
	public boolean containsField(String str){
		if(this.key.size()==0)return true;
		return this.key.contains(str);
	}

	public Query(DBTab dbTab) {
		this.dbTab = dbTab;
	}

	public Query(Object obj, String... strings) {
		this.dbTab = new DBTab(obj);
		object = obj;
		this.queryTab = this.dbTab;
		if (strings.length != 0) {
			for (String str : strings){
				this.key.add(str);
				try {
				if(str.contains(" AS ")){
					String fieldName = str.split(" AS ")[1];
					Field field = this.queryTab.getCls().getDeclaredField(fieldName);
						this.fieldMap.put(field,null);
					
					}else if(!str.trim().equals("*")){
						Field field = this.queryTab.getCls().getDeclaredField(str);
						this.fieldMap.put(field,null);
					}else{
						this.fieldMap.putAll(this.queryTab.getFieldMap());
					}
				} catch (NoSuchFieldException | SecurityException e) {
					log.error(e);
				}
			}
		}else{
			this.fieldMap.putAll(this.queryTab.getFieldMap());
		}
	}
	public Query(Object obj, boolean starReplace) {
		this.dbTab = new DBTab(obj);
		this.queryTab = this.dbTab;
		object = obj;
		this.fieldMap.putAll(dbTab.getFieldMap());
		if(starReplace){
			Map<Field, DBColumn> map = dbTab.getFieldMap();
			Iterator<DBColumn> iterator= map.values().iterator();
			while(iterator.hasNext()){
				this.key.add(iterator.next().getName());
			}
		}
	}
	public Query(Class<?> cls,String... strings) {
		int clsHash = cls.hashCode();
		this.dbTab = SqlCache.getCache().getAttribute(clsHash);
		if(this.dbTab==null){
			this.dbTab = new DBTab(cls);
			SqlCache.getCache().addAttribute(clsHash, this.dbTab);
		}
		this.queryTab = this.dbTab;
		if (strings.length != 0) {
			StringBuilder sb = new StringBuilder();
			for(String str : strings)
				sb.append(str);
			int strHash = cls.hashCode()+sb.toString().hashCode();
			this.fieldMap = SqlCache.getCache().getAttribute(strHash);
			if(this.fieldMap==null){
				this.fieldMap =  new LinkedHashMap<Field, DBColumn>();
				for (String str : strings){
					this.key.add(str);
					try {
					if(str.contains(" AS ")){
						String fieldName = str.split(" AS ")[1];
						Field field = this.queryTab.getCls().getDeclaredField(fieldName);
							this.fieldMap.put(field,null);
						}else if(!str.trim().equals("*")){
							Field field = this.queryTab.getCls().getDeclaredField(str);
							this.fieldMap.put(field,null);
						}else{
							this.fieldMap.putAll(this.queryTab.getFieldMap());
						}
					} catch (NoSuchFieldException | SecurityException e) {
						log.error(e);
					}
				}
			SqlCache.getCache().addAttribute(strHash, this.fieldMap);
			}
		}else{
			int strHash = cls.hashCode() +42804280;
			this.fieldMap = SqlCache.getCache().getAttribute(strHash);
			if(this.fieldMap==null){
				this.fieldMap =  new LinkedHashMap<Field, DBColumn>();
				this.fieldMap.putAll(this.queryTab.getFieldMap());
				SqlCache.getCache().addAttribute(strHash, this.fieldMap);
			}
		}
	}
	public Query(Class<?> cls,boolean trans) {
		this.dbTab = new DBTab(cls);
		this.queryTab = this.dbTab;
		this.fieldMap.putAll(dbTab.getFieldMap());
		if(trans){
			Map<Field, DBColumn> map = dbTab.getFieldMap();
			Iterator<DBColumn> iterator= map.values().iterator();
			while(iterator.hasNext()){
				this.key.add(iterator.next().getName());
			}
		}
	}
	/**
	 * 将查询表的数据库放到新的数据库，用于实现某些高级功能
	 * @param queryCls
	 * @param saveCls
	 */
	public Query(Class<?> queryCls, Class<?> saveCls) {
		this.dbTab = new DBTab(queryCls);
		this.queryTab = new DBTab(saveCls);
		this.fieldMap.putAll(queryTab.getFieldMap());
	}
	public Query(Class<?> queryCls, Class<?> saveCls,String...strings) {
		this.dbTab = new DBTab(queryCls);
		this.queryTab = new DBTab(saveCls);
		if (strings.length != 0) {
			for (String str : strings){
				this.key.add(str);
				try {
				if(str.contains(" AS ")){
					String fieldName = str.split(" AS ")[1];
					Field field = this.queryTab.getCls().getDeclaredField(fieldName);
						this.fieldMap.put(field,null);
					
					}else if(!str.trim().equals("*")){
						Field field = this.queryTab.getCls().getDeclaredField(str);
						this.fieldMap.put(field,null);
					}else{
						this.fieldMap.putAll(this.queryTab.getFieldMap());
					}
				} catch (NoSuchFieldException | SecurityException e) {
					log.error(e);
				}
			}
		}else{
			this.fieldMap.putAll(this.queryTab.getFieldMap());
		}
	}
	public Query(Class<?> queryCls, Class<?> saveCls, boolean trans, String...strings) {
		this.dbTab = new DBTab(queryCls);
		this.queryTab = new DBTab(saveCls);
		this.fieldMap.putAll(queryTab.getFieldMap());
		if (strings.length != 0) {
			for (String str : strings){
				if(str.equals("*")&&trans){
					Map<Field, DBColumn> map = queryTab.getFieldMap();
					Iterator<DBColumn> iterator= map.values().iterator();
					while(iterator.hasNext()){
						this.key.add(iterator.next().getName());
					}
				}
				this.key.add(str);
				try {
					String fieldName=str;
					if(str.contains(" AS "))fieldName = str.split(" AS ")[1];
					Field field = this.queryTab.getCls().getDeclaredField(fieldName);
					this.fieldMap.put(field,null);
				} catch (NoSuchFieldException | SecurityException e) {
					log.error(e);
				}
			}
		}
	}
	public void setFields(List<String> key) {
		this.key = key;
	}

	public void setFields(Object obj) {
		this.setFields(obj.getClass());
	}

	public void setFields(Class<?> cls) {
		Iterator<Field> i = dbTab.iterator();
		while (i.hasNext()) {
			this.key.add(i.next().getName());
		}
	}
	public void addOrder(String...strings ){
		for(String str : strings)
			this.order.add(str+" ASC");
	}
	public void addOrderByDesc(String...strings ){
		for(String str : strings)
			this.order.add(str+" DESC");
	}
	public void setlimit(int num){
		this.limit = limit+"";
	}
	public void setLimit(int pos,int num){
		this.limit=pos+","+num;
	}
	public void addField(String... strings) {
		for (String s : strings)
			this.key.add(s);
	}

	public void addField(Field... strings) {
		for (Field f : strings)
			this.key.add(f.getName());
	}

	public void addCondition(Field field, String condition) {
		this.map.put(dbTab.getName()+"."+dbTab.getDBColumn(field).getName(), condition.toString());
	}

	public void addCondition(String field, Object condition) {
		try {
			map.put(dbTab.getName()+"."+dbTab.getDBColumn(field).getName(), condition.toString());
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(e);
		}
	}
	public void addColumnCondition(String field, Object condition) {
			map.put(field, condition.toString());
	}
	public void addConditionField(String... field) {
		for(String str : field){
			try {
				Field f = object.getClass().getDeclaredField(str);
				f.setAccessible(true);
				map.put(dbTab.getName()+"."+dbTab.getDBColumn(str).getName(), f.get(object).toString().replace("'", "\\'"));
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				log.error(e);
			}
		}
	}

	public void addConditionCommand(String... conditions) {
		for(String condition : conditions)
			this.condition.add(condition);
	}

	@Override
	public String create() {
		String sql = SqlCache.getCache().getSql(this.ident());
		if(sql!=null)
			return sql;
		StringBuilder sb = new StringBuilder("SELECT ");
		if (this.key.size() == 0)
			sb.append("* ");
		else {
			Iterator<String>  s = this.key.iterator();
			while(s.hasNext())
				sb.append(s.next()).append(s.hasNext()?",":"");
		}
		sb.append(" FROM ").append(this.subQuery==null?dbTab.getName():"("+this.subQuery.create()+") AS T"+((int)Math.random()*100));
		if(this.joinObject!=null){
			sb.append(this.joinObject.isInnerJoin()?" INNER OUTER ":" LEFT "+"JOIN "+this.joinObject.getRight()+" ON ");
			for(int i =0;i<this.joinObject.getConditions().length;i++)
				sb.append(this.joinObject.getConditions()[i]).append(i<this.joinObject.getConditions().length-1?" AND ":" ");
		}
		if (map.size() != 0) {
			sb.append(" WHERE ");
			Iterator<String> i = map.keySet().iterator();
			while (i.hasNext()) {
				String s = i.next();
				sb.append(s).append ("='").append( map.get(s)).append( "'").append(i.hasNext() ? " AND " : "");
			}
		}
		if (this.condition.size() != 0) {
			sb.append(this.map.size()==0?" WHERE ":" AND ");
			Iterator<String> i = this.condition.iterator();
			while (i.hasNext()) 
				sb.append(i.next()).append(i.hasNext() ? " AND " : "");
		}
		if(this.group!=null)
			sb.append(" GROUP BY ").append(this.group);
		if(this.unionQuery!=null)
			sb.append(this.unionAll?" ":" ALL "+this.unionQuery.create());
		if(this.order.size()!=0){
			Iterator<?> oI = this.order.iterator();
			sb.append(" ORDER BY ");
			while(oI.hasNext())
				sb.append(oI.next()).append(oI.hasNext() ? "," : " ");
		}
		sb.append(" ").append(this.limit.equals("")?"":"LIMIT "+this.limit);
		SqlCache.getCache().addSql(this.ident(),sb.toString());
		return sb.toString();
	}
	public <T> List<T> query() {
			return this.query(true);
	}
	public <T> T queryOne() {
		List<T> resultSet = this.query(true);
		return resultSet.size()>0?resultSet.get(0):null;
	}
	public <T> List<T> query(boolean mapping){
	this.queryTab.setDataBase(this.dbTab.getDataBase());
	return this.queryTab.query(this,mapping);
}
	public Map<Field, DBColumn> getFieldMap(){
		return this.fieldMap;
	}
	public void removeField(String... string) {
		for(String str :string){
			Field f;
			try {
				f = this.object.getClass().getDeclaredField(str);
				this.fieldMap.remove(f);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public void setSubQuery(Query subQuery){
		this.subQuery = subQuery;
	}
	public void setUnionQuery(Query unionQurery){
		this.unionQuery = unionQurery;
	}
	public void setUnionAllQuery(Query unionQurery){
		this.unionQuery = unionQurery;
		this.unionAll=true;
	}
	public Query getSubQuery(){
		return this.subQuery;
	}
	public String getTabName() {
		return this.dbTab.getName();
	}
	public void setJoinLeft(Class<?> cls, String... conditions) {
		this.setJoinLeft(cls, false, conditions);
	}
	public void setJoinLeft(Class<?> cls,boolean trans, String... conditions) {
		if(conditions.length==0)return;
		DBTab rTab = new DBTab(cls);
		this.joinObject = new JoinObject(this.dbTab.getName(),rTab.getName());
		this.joinObject.setConditions(conditions);
		if(trans)
			for(String condition : this.joinObject.getConditions())
				this.condition.add(condition);
	}
	public void setInnnerJoin(Class<?> cls, String... conditions) {
		this.setInnnerJoin(cls,false, conditions);
	}
	public void setInnnerJoin(Class<?> cls,boolean trans, String... conditions) {
		if(conditions.length==0)return;
		DBTab rTab = new DBTab(cls);
		this.joinObject = new JoinObject(this.dbTab.getName(),rTab.getName());
		this.joinObject.setConditions(conditions);
		this.joinObject.setInnerJoin(true);
		if(trans)
			for(String condition : this.joinObject.getConditions())
				this.condition.add(condition);
	}
	/**
	 * @author tja41
	 *
	 */
	public static class JoinObject{
		private String left;
		private String right;
		private String[] conditions;
		private boolean innerJoin=false;
		public JoinObject(String left, String right) {
			this.left = left;
			this.right = right;
		}
		public String getLeft() {
			return left;
		}
		public void setLeft(String left) {
			this.left = left;
		}
		public String getRight() {
			return right;
		}
		public void setRight(String right) {
			this.right = right;
		}
		public String[] getConditions() {
			return conditions;
		}
		public void setConditions(String... conditions) {
			this.conditions = new String[conditions.length];
			for(int i = 0;i<conditions.length;i++){
				this.conditions[i]=StringUtil.decodeVar(conditions[i], this);
			}
		}
		public boolean isInnerJoin() {
			return innerJoin;
		}
		public void setInnerJoin(boolean innerJoin) {
			this.innerJoin = innerJoin;
		}
		
	}

	
}
