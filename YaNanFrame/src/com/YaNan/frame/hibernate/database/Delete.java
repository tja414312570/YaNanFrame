package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.service.Log;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Delete extends OperateImplement{
	private List<String> key = new ArrayList<String>();
	private DBTab dbTab;
	private Object object;
	private Map<String, String> map = new HashMap<String, String>();
	private List<String> condition = new ArrayList<String>();

	public List<String> getKey() {
		return key;
	}

	public Delete(DBTab dbTab) {
		this.dbTab = dbTab;
	}

	public Delete(Object object) {
		this.dbTab = new DBTab(object);
		this.object = object;
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

	public void addField(String... strings) {
		for (String s : strings)
			this.key.add(s);
	}

	public void addField(Field... strings) {
		for (Field f : strings)
			this.key.add(f.getName());
	}

	public void addCondition(Field field, String condition) {
		this.map.put(dbTab.getDBColumn(field).getName(), condition);
	}

	public void addCondition(String field, String condition) {
		try {
			this.map.put(dbTab.getDBColumn(field).getName(), condition);
		} catch (NoSuchFieldException | SecurityException e) {
			Log.getSystemLog().exception(e);
		}
	}

	public void addCondition(String field) {
		try {
			Field f = object.getClass().getDeclaredField(field);
			f.setAccessible(true);
			this.map.put(dbTab.getDBColumn(field).getName(),
					(String) f.get(object));
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			Log.getSystemLog().exception(e);
		}

	}

	@Override
	public String create() {
		String sql = "DELETE FROM " + this.dbTab.getName();
		if (this.map.size() != 0) {
			sql += " WHERE ";
			Iterator<String> i = this.map.keySet().iterator();
			while (i.hasNext()) {
				String s = i.next();
				sql += s + "=" + this.map.get(s);
			}
		} else {
			if (this.condition.size() != 0) {
				sql += " WHERE ";
				Iterator<String> i = this.condition.iterator();
				while (i.hasNext()) {
					sql += i.next();
				}
			} else {
				//do some thing
			}
		}
		return sql;
	}

	public int delete() {
		return this.dbTab.delete(this);
	}
}
