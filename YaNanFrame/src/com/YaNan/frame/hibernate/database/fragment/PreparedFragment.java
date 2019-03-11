package com.YaNan.frame.hibernate.database.fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 预执行片段，此片段包含生成的SQL语句以及所涉及到的参数和变量信息。
 * @author yanan
 *
 */
public class PreparedFragment {
	/**
	 * 生成的SQL语句
	 */
	private String sql = "";
	/**
	 * SQL语句所涉及到的参数，此参数已排序
	 */
	private List<String> arguments = new ArrayList<String>();
	/**
	 * SQL语句所涉及到的变量，此变量已排序
	 */
	private List<Object> variable = new ArrayList<Object>();
	public List<Object> getVariable() {
		return variable;
	}
	
	public void setVariable(List<Object> variable) {
		this.variable = variable;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
	public void addParameter(@SuppressWarnings("unchecked") List<String> ...lists) {
		for(List<String> list : lists){
			for(String str : list)
				this.arguments.add(str);
		}
	}
	@Override
	public String toString() {
		return "PreparedFragment [sql=" + sql + ", arguments=" + arguments + "]";
	}

	public void addAllVariable(List<Object> param) {
		this.variable.addAll(param);
	}
}
