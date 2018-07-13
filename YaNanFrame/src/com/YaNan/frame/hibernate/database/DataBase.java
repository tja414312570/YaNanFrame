package com.YaNan.frame.hibernate.database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.beanSupport.DecodeBean;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * 数据库对象，提供数据的创建，数据库连接池的管理，在当前数据库执行sql的功能
 * 
 * @author Administrator
 *
 */
public class DataBase {
	@DecodeBean(key="id",value="object")
	private Map<String,String> map = new HashMap<String,String>();
	private Map<String, DBTab> tabMapping = new HashMap<String, DBTab>();
	private DataBaseConfigure dbConfigure;
	private boolean available=false;
	private final String SUFFIX = "autoReconnect=true&failOverReadOnly=false";
	private ConnectionPools connectionPools;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,DataBase.class);
	private String connetionURL;
	private String connectionTestURL;

	public DataBase(DataBaseConfigure dbConf) {
		this.dbConfigure = dbConf;
		this.connetionURL = dbConfigure.getScheme() + "://"
		+ dbConfigure.getHost() + ":" + dbConfigure.getPort() + "/" + dbConfigure.getName()
		+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX;
		this.connectionTestURL = dbConfigure.getScheme() + "://" + dbConfigure.getHost() + ":" + dbConfigure.getPort() + "/"
				+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX+"&zeroDateTimeBehavior=convertToNull";
	}
	public void init(){
		if(this.available)
			this.connectionPools =ConnectionPools.getConnectionPools(this);
	}
	public Map<String, DBTab> getTabMapping() {
		return tabMapping;
	}

	public boolean hasTab(String tabName) {
		return this.tabMapping.containsKey(tabName);
	}

	public void addTab(DBTab tab) {
		this.tabMapping.put(tab.getName(), tab);
	}

	public List<String> showDataBase() {
		List<String> dbs = new ArrayList<String>();
		try {
			Connection connect = getConnection();
			String sql = "SHOW DATABASES";
			PreparedStatement ps = (PreparedStatement) connect
					.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			this.releaseConnection(connect);
			while (rs.next()) {
				dbs.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbs;
	}

	public DataBaseConfigure getDataBaseConfigure() {
		return dbConfigure;
	}

	public void setDbConfigure(DataBaseConfigure dbConf) {
		this.dbConfigure = dbConf;
	}

	public boolean create() {
		return create(dbConfigure.getName());
	}

	public boolean create(String databaseName) {
		Connection connect = null;
		PreparedStatement ps = null;
		try {
			Class.forName(dbConfigure.getDriver());
			connect= (Connection) DriverManager.getConnection(dbConfigure.getScheme() + "://"
					+ dbConfigure.getHost() + ":" + dbConfigure.getPort() 
					+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX,
					dbConfigure.getUsername(),
					dbConfigure.getPassword());
			String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName+" DEFAULT CHARACTER SET "+dbConfigure.getCharset()+" COLLATE "+dbConfigure.getCollate();
			ps = (PreparedStatement) connect
					.prepareStatement(sql);
			 ps.execute();
			 this.available =true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			log.error(e);
		}finally{
			if(connect!=null){
				try {
					if(ps!=null)
						ps.close();
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
		return this.available ;
	}

	public Connection createConnection() throws ClassNotFoundException,
			SQLException {
		Class.forName(dbConfigure.getDriver());
		return (Connection) DriverManager.getConnection(connetionURL, dbConfigure.getUsername(),
				dbConfigure.getPassword());
	}

	public boolean delete() {
		try {
			Connection connect = this.getConnection();
			String sql = "drop database if exists " + dbConfigure.getName();
			PreparedStatement ps = (PreparedStatement) connect
					.prepareStatement(sql);
			this.releaseConnection(connect);
			return ps.execute();
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}

	public Connection getConnection(){
		return this.connectionPools.getConnection();
	}

	public boolean canConnect() {
		try {
			Class.forName(dbConfigure.getDriver());
			Connection connection = (Connection) DriverManager.getConnection(
					connectionTestURL,dbConfigure.getUsername(), dbConfigure.getPassword());
			return connection != null;
		} catch (ClassNotFoundException | SQLException e) {
			log.error(e);
		}
		return false;
	}

	public void destory() {
		this.connectionPools.destory();
	}

	public void releaseConnection(Connection connection) {
		this.connectionPools.release(connection);
	}

	public ConnectionPools getConnectionPools() {
		return this.connectionPools;
	}
	public ResultSet executeQuery(String sql,Connection connection)
			throws SQLException {
		PreparedStatement ps = (PreparedStatement) connection
				.prepareStatement(sql);
		log.debug(ps.asSql());
		return ps.executeQuery();
	}
	public int executeUpdate(String sql,Connection connection)
			throws SQLException {
		PreparedStatement ps = (PreparedStatement) connection
				.prepareStatement(sql);
		log.debug(ps.asSql());
		return ps.executeUpdate();
	}
	public PreparedStatement execute(String sql,Connection connection,int... statement)
			throws SQLException {
		PreparedStatement ps;
		if(statement.length==0)
			ps = (PreparedStatement) connection.prepareStatement(sql);
		else
			ps = (PreparedStatement) connection.prepareStatement(sql,statement[0]);
		log.debug(ps.asSql());
		return ps;
	}
	public PreparedStatement executeQuery(String sql){
		try {
			Connection connection = this.getConnection();
			PreparedStatement ps = (PreparedStatement) connection
					.prepareStatement(sql);
			this.releaseConnection(connection);
			return ps;
		} catch (SQLException e) {
			log.error("error sql:"+sql);
			log.error(e);
		}
		return null;
	}
	public int executeUpdate(String sql){
		try{
			Connection connection = this.getConnection();
			PreparedStatement ps = (PreparedStatement) connection
					.prepareStatement(sql);
			this.releaseConnection(connection);
			return ps.executeUpdate();
		} catch (SQLException e) {
			log.error("error sql:"+sql);
			log.error(e);
		}
		return 0;
	}
	public PreparedStatement execute(String sql,int... statement){
		try{
				Connection connection = this.getConnection();
				PreparedStatement ps = statement.length==0?
						(PreparedStatement) connection.prepareStatement(sql):
							(PreparedStatement) connection.prepareStatement(sql,statement[0]);
				this.releaseConnection(connection);
				return ps;
			} catch (SQLException e) {
				log.error("error sql:"+sql);
				log.error(e);
			}
			return null;
	}

	public boolean isAvailable() {
		return available;
	}
	public String getName() {
		return dbConfigure.getName();
	}
}
