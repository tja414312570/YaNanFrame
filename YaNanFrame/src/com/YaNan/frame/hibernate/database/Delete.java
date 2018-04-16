package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

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
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,Delete.class);

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
			log.error(e);
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
			log.error(e);
		}

	}

	@Override
	public String create() {
		StringBuilder sb = new StringBuilder( "DELETE FROM ").append(this.dbTab.getName());
		if (this.map.size() != 0) {
			sb.append(" WHERE ");
			Iterator<String> i = this.map.keySet().iterator();
			while (i.hasNext()) {
				String s = i.next();
				sb.append( s ).append("='").append(this.map.get(s)).append(i.hasNext() ? "' AND " : "'");
			}
		} 
		if (this.condition.size() != 0) {
			sb.append(this.map.size()==0?" WHERE ":" AND ");
			Iterator<String> i = this.condition.iterator();
			while (i.hasNext())
				sb.append( i.next()).append(i.hasNext() ? " AND " : "");
		}
		return sb.toString();
	}

	public int delete() {
		return this.dbTab.delete(this);
	}
}
