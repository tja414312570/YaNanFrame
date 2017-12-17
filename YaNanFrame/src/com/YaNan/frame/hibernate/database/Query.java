package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.service.Log;
import com.YaNan.frame.stringSupport.StringSupport;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Query extends OperateImplement{
	protected List<String> key = new ArrayList<String>();
	protected DBTab queryTab;
	protected Object object;
	protected Class<?> cls;
	protected Map<String, Object> map = new HashMap<String, Object>();
	protected List<String> condition = new ArrayList<String>();
	protected List<String> order = new ArrayList<String>();
	protected Map<Field, DBColumn> fieldMap = new LinkedHashMap<Field, DBColumn>();
	protected String orderType="";
	protected String limit="";
	protected String group=null;
	private Query subQuery=null;
	private JoinObject joinObject;
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
		this.fieldMap.putAll(dbTab.getFieldMap());
		if (strings.length != 0) {
			for (String str : strings)
				this.key.add(str);
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
		this.dbTab = new DBTab(cls);
		this.queryTab = this.dbTab;
		this.fieldMap.putAll(dbTab.getFieldMap());
		if (strings.length != 0) {
			for (String str : strings){
				this.key.add(str);
				if(str.contains(" AS ")){
					String fieldName = str.split(" AS ")[1];
					Field field;
					try {
						field = this.queryTab.getCls().getDeclaredField(fieldName);
						this.fieldMap.put(field,null);
					} catch (NoSuchFieldException | SecurityException e) {
						Log.getSystemLog().exception(e);
					}
					}
			}
		}else{
			Iterator<Field> iterator = this.fieldMap.keySet().iterator();
			while(iterator.hasNext())
				this.key.add(iterator.next().getName());
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
		this.fieldMap.putAll(queryTab.getFieldMap());
		if (strings.length != 0) {
			for (String str : strings){
				if(str.equals("*")){
					Map<Field, DBColumn> map = queryTab.getFieldMap();
					Iterator<DBColumn> iterator= map.values().iterator();
					while(iterator.hasNext()){
						this.key.add(iterator.next().getName());
					}
				}
				if(str.contains(" AS ")){
					this.key.add(str);
					String fieldName = str.split(" AS ")[1];
					Field field;
					try {
						field = this.queryTab.getCls().getDeclaredField(fieldName);
						this.fieldMap.put(field,null);
					} catch (NoSuchFieldException | SecurityException e) {
						Log.getSystemLog().exception(e);
					}
					}
			}
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
					Log.getSystemLog().exception(e);
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
		if(strings.length!=0){
			for(String str : strings)
				this.order.add(str);
		}
	}
	public void addOrderByDesc(String...strings ){
		if(strings.length!=0&&orderType!=null){
			for(String str : strings)
				this.order.add(str);
			this.orderType="DESC";
		}
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
		this.map.put(dbTab.getName()+"."+dbTab.getDBColumn(field).getName(), condition);
	}

	public void addCondition(String field, Object condition) {
		try {
			map.put(dbTab.getName()+"."+dbTab.getDBColumn(field).getName(), condition);
		} catch (NoSuchFieldException | SecurityException e) {
			Log.getSystemLog().exception(e);
		}
	}

	public void addConditionField(String... field) {
		for(String str : field){
			try {
				Field f = object.getClass().getDeclaredField(str);
				f.setAccessible(true);
				map.put(dbTab.getName()+"."+dbTab.getDBColumn(str).getName(), f.get(object));
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				Log.getSystemLog().exception(e);
			}
		}
	}

	public void addConditionCommand(String condition) {
		this.condition.add(condition);
	}

	@Override
	public String create() {
		String sql = "SELECT ";
		if (this.key.size() == 0) {
			sql += "* ";
		} else {
			Iterator<String>  s = this.key.iterator();
			while(s.hasNext()) {
				sql += s.next() + (s.hasNext()?",":"");
			}
		}
		sql += " FROM " 
				+ (this.subQuery==null?dbTab.getName():"("+this.subQuery.create()+") AS T"+((int)Math.random()*100));
		if(this.joinObject!=null){
			sql +=" LEFT OUTER JOIN "+this.joinObject.getRight()+" ON ";
			for(int i =0;i<this.joinObject.getConditions().length;i++){
				sql+=this.joinObject.getConditions()[i]+
						(i<this.joinObject.getConditions().length-1?" AND ":" ");
			}
		}
		if(this.group!=null)
			sql+= " GROUP BY "+this.group;
		if (map.size() != 0) {
			sql += " WHERE ";
			Iterator<String> i = map.keySet().iterator();
			while (i.hasNext()) {
				String s = i.next();
				sql += s + "='" + map.get(s) + "'"
						+ (i.hasNext() ? " AND " : "");
			}
		}
		if (this.condition.size() != 0) {
			if(this.map.size()==0){
				sql += " WHERE ";
			}else{
				sql += " AND ";
			}
			Iterator<String> i = this.condition.iterator();
			while (i.hasNext()) {
				sql += i.next() + (i.hasNext() ? " AND " : "");
			}
		}
		if(this.order.size()!=0){
			Iterator<?> oI = this.order.iterator();
			sql +=" ORDER BY ";
			while(oI.hasNext()){
				sql += oI.next() + (oI.hasNext() ? "," : " ");
			}
		}
		sql +=this.orderType+" "+(this.limit.equals("")?"":"LIMIT "+this.limit);
		return sql;
	}
	public List<?> query() {
			return this.query(true);
	}
	public List<?> query(boolean mapping){
	this.queryTab.setDataBase(this.dbTab.getDataBase());
	return this.queryTab.query(this,create(),mapping);
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
	public Query getSubQuery(){
		return this.subQuery;
	}
	public String getTabName() {
		return this.dbTab.getName();
	}
	public void setJoinLeft(Class<?> cls, String... conditions) {
		if(conditions.length==0)return;
		DBTab rTab = new DBTab(cls);
		this.joinObject = new JoinObject(this.dbTab.getName(),rTab.getName());
		this.joinObject.setConditions(conditions);
		for(String condition : this.joinObject.getConditions())
			this.condition.add(condition);
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
	/**
	 * @author tja41
	 *
	 */
	public static class JoinObject{
		public JoinObject(String left, String right) {
			this.left = left;
			this.right = right;
		}
		private String left;
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
				this.conditions[i]=StringSupport.decodeVar(conditions[i], this);
			}
		}
		private String right;
		private String[] conditions;
	}
	
}
