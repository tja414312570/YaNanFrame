package com.YaNan.frame.bean;

import java.util.Map;

public class xmlBean {
	public String context;
	public Map<String, String> xml;

	public void add(String key, String value) {
		xml.put(key, value);
	}

	public void setContext(String value) {
		xml.put("xmlTextContent", value);
	}

	public String get(String key) {
		return xml.get(key);
	}

	public String getContext() {
		return xml.get("xmlTextContent");
	}

	public Map<String, String> getObject() {
		return xml;
	}

	public int size() {
		return xml.size();
	}

	public void clear() {
		xml.clear();
	}
}
