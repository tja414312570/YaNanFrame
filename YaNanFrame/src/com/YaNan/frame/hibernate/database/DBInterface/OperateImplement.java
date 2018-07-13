package com.YaNan.frame.hibernate.database.DBInterface;

import java.util.LinkedList;
import java.util.List;

import com.YaNan.frame.hibernate.database.DBTab;

public abstract class OperateImplement {
	protected DBTab dataTables;
	protected List<Object> parameters = new LinkedList<Object>();
	public abstract String create();
	public DBTab getDbTab() {
		return dataTables;
	}
	public void setDbTab(DBTab dataTables) {
		this.dataTables = dataTables;
	}
	public String hashString(){
		return this.hashCode()+"";
	}
	public List<Object> getParameters() {
		return parameters;
	}
	public void addParameters(Object parameter){
		parameters.add(parameter);
	}
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
}
