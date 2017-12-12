package com.YaNan.frame.hibernate.database;

import java.util.HashMap;
import java.util.Map;

public class Class2TabMappingCache {
	private static Class2TabMappingCache dbManager;
	private static Map<Class<?>, DBTab> map = new HashMap<Class<?>, DBTab>();

	public Class2TabMappingCache getManager() {
		if (dbManager == null)
			dbManager = new Class2TabMappingCache();
		return dbManager;
	}

	public static DBTab getDBTab(Class<?> cls) {
		if (map.containsKey(cls))
			return map.get(cls);
		DBTab tab = new DBTab(cls);
		map.put(tab.getCls(), tab);
		return tab;
	}

	public static DBTab getDBTab(Object obj) {
		if (map.containsKey(obj.getClass()))
			return map.get(obj.getClass());
		DBTab tab = new DBTab(obj);
		map.put(tab.getCls(), tab);
		return tab;
	}

	public static boolean hasTab(Class<?> cls) {
		return map.containsKey(cls);

	}

	public static boolean hasTab(Object obj) {
		return map.containsKey(obj.getClass());
	}

	public static void addTab(DBTab tab) {
		map.put(tab.getCls(), tab);
	}
}
