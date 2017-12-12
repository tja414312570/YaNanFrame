package com.YaNan.frame.hibernate.database.entity;

import java.util.ArrayList;
import java.util.List;

;


public class Tabs {
	private String db;
	private List<Tab> tab = new ArrayList<Tab>();
	public String getDb() {
		return db;
	}
	public void setDb(String dbName) {
		this.db = dbName;
	}
	@Override
	public String toString() {
		return "Tabs [db Name :" + db + ", tabs=" + tab + "]";
	}
	public void addTab(Tab tab) {
		this.tab.add(tab);
	}
	
}
