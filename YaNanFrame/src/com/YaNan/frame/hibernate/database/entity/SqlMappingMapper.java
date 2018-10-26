package com.YaNan.frame.hibernate.database.entity;

import java.util.List;

import com.YaNan.frame.hibernate.database.fragment.SqlFragment;

/**
 * sql映射mapper
 * @author yanan
 *
 */
public class SqlMappingMapper {
	//数据库
	private String database;
	//命名空间 
	private String namespace;
	//里面包含的语句片段
	private List<SqlFragment> sqlFragmentList;
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public List<SqlFragment> getSqlFragmentList() {
		return sqlFragmentList;
	}
	public void setSqlFragmentList(List<SqlFragment> sqlFragmentList) {
		this.sqlFragmentList = sqlFragmentList;
	}
	public SqlMappingMapper(String database, String namespace) {
		super();
		this.database = database;
		this.namespace = namespace;
	}
	public SqlMappingMapper() {
		super();
	}
	@Override
	public String toString() {
		return "SqlMappingMapper [database=" + database + ", namespace=" + namespace + ", sqlFragmentList="
				+ sqlFragmentList + "]";
	}
	
}
