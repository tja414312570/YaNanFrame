package com.YaNan.frame.hibernate.database;

public class DataBaseConfigure {
	private String id;//数据库ID
	private String name;//数据库名
	private String driver = "com.mysql.jdbc.Driver";//数据库驱动
	private String type = "mysql";//类型
	private String scheme = "jdbc:mysql";//协议
	private String host = "localhost";//地址
	private String port = "3306";//端口
	private String username = "root";//用户名
	private String password = "";//密码
	private String charset = "utf8";//字符集
	private String collate = "utf8_general_ci";//排序规则
	private String encoding = "utf-8";//编码
	private String defaulted;//是否设置为默认
	private String log="false";//使用日志
	private int minNum = 2;//连接池初始化链接量
	private int maxNum = 8;//连接池最大连接量
	private int addNum = 2;//数据库每次添加的数量
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
