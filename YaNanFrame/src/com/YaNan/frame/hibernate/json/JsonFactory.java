package com.YaNan.frame.hibernate.json;

public class JsonFactory {
	public static Class2Json getClass2Json() {
		return new Class2Json();
	}

	public static Class2Json getClass2Json(Object object)
			throws IllegalArgumentException, IllegalAccessException {
		return new Class2Json(object);
	}
}
