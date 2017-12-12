package com.YaNan.frame.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.YaNan.frame.hibernate.WebPath;

public class Resource {
	private static Map<Integer, File> map = new HashMap<Integer, File>();
	private final static URL SYSTEM = Resource.class.getClass().getResource(
			"/system.properties");
	private final static URL PERMISSION = Resource.class.getClass().getClass()
			.getResource("/permisssion.properties");
	private static URL I18N = Resource.class.getClass().getClass()
			.getResource("/i18n");

	public static File getResourceById(int id) {
		return map.get(id);
	}

	public static Properties getResourceAsPropertyById(int id)
			throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(map.get(id)));
		return properties;
	}

	public static void addResource(int id, File file) {
		map.put(id, file);
	}

	public static boolean containsId(int id) {
		return map.containsKey(id);
	}

	public static URL getSystem() {
		return SYSTEM;
	}

	public static URL getPermission() {
		return PERMISSION;
	}

	public static URL getI18N() {
		return I18N;
	}

	public static Properties getSystemProperty() throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(WebPath.getWebPath()
				.getClassPath().realPath, "system.properties")));
		return properties;
	}

	public static OutputStream getSysteamPropertyAsOutputStream()
			throws FileNotFoundException {
		return new FileOutputStream(new File(WebPath.getWebPath()
				.getClassPath().realPath, "system.properties"));
	}

	public static Properties getPermissionProperty()
			throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(PERMISSION.getFile()));
		return properties;
	}

	public static Properties getI18NProperty() throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(I18N.getFile()));
		return properties;
	}

	public static void setI18N(URL url) {
		I18N = url;
	}

	public static void setI18N(String resourcePath) {
		I18N = Resource.class.getClass().getClass().getResource(resourcePath);
	}

	public static URL getResourceAsURL(String resourcePath) {
		return Resource.class.getClass().getResource("/" + resourcePath);
	}

	public static Properties getResourceAsProperty(String resourcePath)
			throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(getResourceAsURL(resourcePath)
				.getFile()));
		return properties;
	}
}
