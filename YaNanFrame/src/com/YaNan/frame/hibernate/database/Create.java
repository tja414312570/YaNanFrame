package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.service.Log;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Create {
	private String PrimaryKey;
	private List<String> uniques = new ArrayList<String>();
	private DBTab tab;
	private Map<String, String> columns = new LinkedHashMap<String, String>();
	/**
	 * 数据表的创建对象，传入一个DBTab对象 
	 * @param tab
	 */
	public Create(DBTab tab) {
		this.tab = tab;
		init();
	}
	
	private void init() {
		Iterator<Field> iterator = this.tab.iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			DBColumn db = this.tab.getDBColumn(field);
			this.columns.put(db.getName(), db.Constraints());
			if (db.isPrimary_Key())
				this.PrimaryKey= db.getName();
			if(db.isUnique())
				this.uniques.add(db.getName());
		}
	}
	/**
	 * 数据表的创建对象，传入一个数据表映射类
	 * @param tab
	 */
	public Create(Class<?> cls) {
		this.tab = new DBTab(cls);
		init();
	}
	/**
	 * 数据表的创建对象，传入一个数据表映射类的对象
	 * @param tab
	 */
	public Create(Object object) {
		this.tab = new DBTab(object);
		init();
	}
	
	public void setFields(Object obj) {
		this.setFields(obj.getClass());
	}

	public void addClass(Class<?> cls) {
		DBTab tab = new DBTab(cls);
		Iterator<Field> i = tab.iterator();
		while (i.hasNext()) {
			Field f = i.next();
			DBColumn db = tab.getDBColumn(f);
			this.columns.put(db.getName(), db.Constraints());
		}
	}

	public void addField(Field... strings) {
		for (Field f : strings) {
			DBColumn dbc = tab.getDBColumn(f);
			this.columns.put(dbc.getName(), dbc.Constraints());
		}

	}

	public String create() {
		String sql = "CREATE TABLE " + this.tab.getName();
		Iterator<String> iterator = this.columns.keySet().iterator();
		if (iterator.hasNext())
			sql += " (";
		while (iterator.hasNext()) {
			String field = iterator.next();
			sql += field + " " + this.columns.get(field)
					+ (iterator.hasNext() ? "," : "");
		}
		// 主键设置部分
		if (PrimaryKey==null) {
			Log.getSystemLog().info("一个表应该至少包含一个主键的字段");
		} else {
			sql += ",\r\nPRIMARY KEY("+this.PrimaryKey+")";
		}
		//unique约束
		if (this.uniques.size() == 0) {
			sql += ")";
		} else {
			sql += ",\r\nUNIQUE (";
			Iterator<String> i = this.uniques.iterator();
			while (i.hasNext()) {
				sql += i.next() + (i.hasNext() ? "," : "))");
			}
		}

		if(this.tab.getCharset()!=null||this.tab.getCollate()!=null)
			sql+=" DEFAULT";
		if(this.tab.getCharset()!=null)
			sql += " CHARACTER SET " + this.tab.getCharset();
		if(this.tab.getCollate()!=null)
			sql += " COLLATE "+ this.tab.getCollate();
		return sql;
	}

	public void addField(String column, String type) {
		this.columns.put(column, type);
	}

	public void addField(Map<String, String> columns) {
		this.columns = columns;
	}
}
