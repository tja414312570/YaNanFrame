package com.YaNan.frame.hibernate.database;

import java.util.HashMap;
import java.util.Map;

public class TabCache {
	private static TabCache dbManager;
	private static Map<String, DBTab> map = new HashMap<String, DBTab>();

	public TabCache getManager() {
		if (dbManager == null)
			dbManager = new TabCache();
		return dbManager;
	}

	public static DBTab getDBTab(Class<?> cls) {
		if (map.containsKey(cls))
			return map.get(cls);
		DBTab tab = new DBTab(cls);
		map.put(tab.getName(), tab);
		return tab;
	}

	public static DBTab getDBTab(Object obj) {
		if (map.containsKey(obj.getClass()))
			return map.get(obj.getClass());
		DBTab tab = new DBTab(obj);
		map.put(tab.getName(), tab);
		return tab;
	}

	public static boolean hasTab(Class<?> cls) {
		return map.containsKey(cls);

	}

	public static boolean hasTab(Object obj) {
		return map.containsKey(obj.getClass());
	}

	public static void addTab(DBTab tab) {
		map.put(tab.getName(), tab);
	}
}
