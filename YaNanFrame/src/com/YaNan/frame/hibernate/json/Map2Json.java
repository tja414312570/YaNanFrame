package com.YaNan.frame.hibernate.json;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Map2Json {
	private Map<String, String> map = new LinkedHashMap<String, String>();

	public void put(String key, Map<String, String> map) {
		this.map.put(key, map.toString());
	}

	public Map2Json() {
	}

	public void put(Map<String, String> map) {
		Iterator<String> iterator = map.keySet().iterator();
		this.map.put(map.get(iterator.next()).toString(), map.toString());
	}

	public void put(Class2Json c2j) {
		Map<String, String> m = c2j.getMap();
		Iterator<String> iterator = m.keySet().iterator();
		this.map.put(m.get(iterator.next()).toString(), mapToJson(m));
	}

	public String toJson() {
		return mapToJson(this.map);
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

	public static String mapToJson(Map<String, String> map) {
		return "{" + makeJson(map) + "}";
	}

	public String canEvalJson() {
		return "(" + mapToJson(this.map) + ")";
	}

	public static String canEvalJson(Map<String, String> map) {
		return "(" + mapToJson(map) + ")";
	}

	public void put(String name, String value) {
		this.map.put(name, "'" + value + "'");
	}

	public void remove(String name) {
		if (this.map.containsKey(name))
			this.map.remove(name);
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

	@Override
	public String toString() {
		return makeJson(this.map);
	}
}
