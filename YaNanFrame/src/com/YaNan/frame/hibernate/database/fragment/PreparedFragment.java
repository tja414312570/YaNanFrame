package com.YaNan.frame.hibernate.database.fragment;

import java.util.ArrayList;
import java.util.List;

public class PreparedFragment {
	private String sql = "";
	private List<String> arguments = new ArrayList<String>();
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
}
