package com.YaNan.frame.hibernate.database.DBInterface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBTab;

public class SqlExecute {

	public void setQuery(SqlCondition sqlCodition) {

	}

	public void setInsert(SqlCondition sqlCodition, String... args) {

	}

	public void setUpdate(SqlCondition sqlCodition) {

	}

	public void setDelete(SqlCondition sqlCodition) {

	}

	public Object[] query() {
		return null;
	}

	public Map<Object, Object> query(Field field) {
		return null;
	}

	public List<Object> Query() {
		return null;
	}

	public boolean insert() {
		return true;
	}

	public boolean update() {
		return true;
	}

	public boolean delete() {
		return true;
	}

	public void ignore(Field... fields) {

	}

	public boolean createTab(DBTab dbTab) {
		return true;
	}
}

abstract class SqlLimiter {
	private SqlCondition sqlCondition;

	public abstract void setLimiter(SqlCondition sqlCondition);

	public SqlCondition getCondition() {
		return this.sqlCondition;
	}
}

class SqlCondition {
	private Map<String, String> map = new HashMap<String, String>();

	public void setCondition(Field field, String limited, String value) {
		if (this.map.containsKey(field)) {

		} else {

		}
	}

	public void setCondition(Field field, String limited) {
		this.map.put(field.getName(), limited);
	}
}
