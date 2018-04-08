package com.YaNan.frame.hibernate.database;

public class DataBaseConfigure {
	private String id;
	private String name;
	private String driver = "com.mysql.jdbc.Driver";
	private String type = "mysql";
	private String scheme = "jdbc:mysql";
	private String host = "localhost";
	private String port = "3306";
	private String username = "root";
	private String password = "";
	private String charset = "utf8";
	private String collate = "utf8_general_ci";
	private String encoding = "utf-8";
	private String defaulted;
	private String log="false";
	private int minNum = 2;
	private int maxNum = 6;
	private int addNum = 2;
	public int getMinNum() {
		return minNum;
	}
	public void setMinNum(int minNum) {
		this.minNum = minNum;
	}
	public int getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}
	public int getAddNum() {
		return addNum;
	}
	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String cahrset) {
		this.charset = cahrset;
	}
	public String getCollate() {
		return collate;
	}
	public void setCollate(String collate) {
		this.collate = collate;
	}
	public String getDefaulted() {
		return defaulted;
	}
	public void setDefaulted(String defaulted) {
		this.defaulted = defaulted;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	@Override
	public String toString() {
		return "DataBaseConfigure [id=" + id + ", name=" + name + ", driver=" + driver + ", type=" + type + ", scheme="
				+ scheme + ", host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
				+ ", charset=" + charset + ", collate=" + collate + ", encoding=" + encoding + ", defaulted="
				+ defaulted + ", log=" + log + ", minNum=" + minNum + ", maxNum=" + maxNum + ", addNum=" + addNum + "]";
	}
}
