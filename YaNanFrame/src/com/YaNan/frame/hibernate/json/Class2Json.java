package com.YaNan.frame.hibernate.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.support.ignore;

public class Class2Json {
	private Map<String, String> map = new LinkedHashMap<String, String>();

	public Class2Json(Object object) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(ignore.class) != null)
				continue;
			field.setAccessible(true);
			this.map.put(field.getName(), "'" + field.get(object).toString()
					+ "'");
		}
	}

	public Class2Json() {
	}

	public void addObject(Object object) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(ignore.class) != null)
				continue;
			field.setAccessible(true);
			this.map.put(field.getName(), "'"
					+ (field.get(object) == null ? "null" : field.get(object)
							.toString().replace("\\", "/").replace("\n", "/n")
							.replace("\r", "/r")) + "'");
		}
	}

	public String toJson() throws IllegalArgumentException,
			IllegalAccessException {
		return mapToJson(this.map);
	}

	public static String toJson(Object object) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = object.getClass().getDeclaredFields();
		Map<String, String> map = new HashMap<String, String>();
		for (Field field : fields) {
			if (field.getAnnotation(ignore.class) != null)
				continue;
			field.setAccessible(true);
			map.put(field.getName(), "'"
					+ (field.get(object) == null ? "null" : field.get(object)
							.toString().replace("\\", "/").replace("\n", "/n")
							.replace("\r", "/r")) + "'");
		}
		return mapToJson(map);
	}

	public static String mapToJson(Map<String, String> map) {
		return "{" + makeJson(map) + "}";
	}

	private static String makeJson(Map<String, String> map) {
		Iterator<String> iterator = map.keySet().iterator();
		String buffer = "";
		while (iterator.hasNext()) {
			String key = iterator.next();
			buffer += key + ":" + map.get(key)
					+ (iterator.hasNext() ? "," : "");
		}
		return buffer;
	}

	public void put(String name, String value) {
		this.map.put(name, "'" + value + "'");
	}

	public void remove(String name) {
		if (this.map.containsKey(name))
			this.map.remove(name);
	}

	public String canEvalJson() {
		return "(" + mapToJson(this.map) + ")";
	}

	public static String canEvalJson(Object object)
			throws IllegalArgumentException, IllegalAccessException {
		return "(" + toJson(object) + ")";
	}

	public void remove(List<String> args) {
		for (String l : args) {
			if (this.map.containsKey(l))
				this.map.remove(l);
		}
	}

	public void replace(String name, String value) {
		this.map.replace(name, "'" + value + "'");
	}

	public Map<String, String> getMap() {
		return this.map;
	}

	@Override
	public String toString() {
		return makeJson(this.map);
	}
}
