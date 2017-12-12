package com.YaNan.frame.hibernate.database.DBInterface;

import com.YaNan.frame.hibernate.database.DBTab;

public abstract class OperateImplement {
	protected DBTab dbTab;
	public abstract String create();
	public DBTab getDbTab() {
		return dbTab;
	}
	public void setDbTab(DBTab dbTab) {
		this.dbTab = dbTab;
	}
	
}
