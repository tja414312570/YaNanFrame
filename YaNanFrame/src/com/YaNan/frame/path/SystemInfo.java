package com.YaNan.frame.path;

public class SystemInfo {
	public static String osName = System.getProperty("os.name");
	public static String osArch = System.getProperty("os.arch");
	public static String osVersion = System.getProperty("os.version");
	public static String fileSeparator = System.getProperty("file.separator");
	public static String pathSeparator = System.getProperty("path.separator");
	public static String lineSeparator = System.getProperty("line.separator");
	public static String userName = System.getProperty("user.name");
	public static String userHome = System.getProperty("user.home");
	public static String userDir = System.getProperty("user.dir");

	public static String getOsName() {
		return osName;
	}

	public static String getOsArch() {
		return osArch;
	}

	public static String getOsVersion() {
		return osVersion;
	}

	public static String getFileSeparator() {
		return fileSeparator;
	}

	public static String getPathSeparator() {
		return pathSeparator;
	}

	public static String getLineSeparator() {
		return lineSeparator;
	}

	public static String getUserName() {
		return userName;
	}

	public static String getUserHome() {
		return userHome;
	}

	public static String getUserDir() {
		return userDir;
	}
}
