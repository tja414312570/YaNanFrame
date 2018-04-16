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
import com.YaNan.frame.plugs.PlugsFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * 鏁版嵁搴撶被锛岀敤浜庡瓨鍌ㄦ暟鎹簱鐨勬暟鎹� 浠ュ強绠＄悊鏁版嵁搴撻摼鎺�
 * 
 * @author Administrator
 *
 */
public class DataBase {
	@DecodeBean(key="id",value="object")
	private Map<String,String> map = new HashMap<String,String>();
	private Map<String, DBTab> tabMapping = new HashMap<String, DBTab>();
	private DataBaseConfigure dbConfigure;
	private int initialConnections = 2; 
	private int incrementalConnections = 2;
	private int maxConnections = 6; 
	private final String SUFFIX = "autoReconnect=true&failOverReadOnly=false";
	private ConnectionPools connectionPools;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,DataBase.class);
	public int getInitialConnections() {
		return initialConnections;
	}

	public void setInitialConnections(int initialConnections) {
		this.initialConnections = initialConnections;
	}

	public int getIncrementalConnections() {
		return incrementalConnections;
	}

	public void setIncrementalConnections(int incrementalConnections) {
		this.incrementalConnections = incrementalConnections;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	public DataBase(DataBaseConfigure dbConf) {
		this.dbConfigure = dbConf;
		this.incrementalConnections = dbConf.getAddNum();
		this.initialConnections = dbConf.getMinNum();
		this.maxConnections = dbConf.getMaxNum();
	}
	public void init(){
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
			// 鑾峰緱ResultSet
			ResultSet rs = ps.executeQuery();
			this.releaseConnection(connect);
			// 瀵圭粨鏋滆繘琛屽垎鏋愬苟鎻愬彇鏁版嵁
			while (rs.next()) {
				dbs.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbs;
	}

	public DataBaseConfigure getDbConfigure() {
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
		try {
			Class.forName(dbConfigure.getDriver());
			connect= (Connection) DriverManager.getConnection(dbConfigure.getScheme() + "://"
					+ dbConfigure.getHost() + ":" + dbConfigure.getPort()
					+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX, dbConfigure.getUsername(),
					dbConfigure.getPassword());
			String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName+" DEFAULT CHARACTER SET "+dbConfigure.getCharset()+" COLLATE "+dbConfigure.getCollate();
			PreparedStatement ps = (PreparedStatement) connect
					.prepareStatement(sql);
			return ps.execute();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			log.error(e);
			return false;
		}finally{
			if(connect!=null)
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
					log.error(e);
				}
		}
	}

	public Connection createConnection() throws ClassNotFoundException,
			SQLException {
		Class.forName(dbConfigure.getDriver());
		return (Connection) DriverManager.getConnection(dbConfigure.getScheme() + "://"
				+ dbConfigure.getHost() + ":" + dbConfigure.getPort() + "/" + dbConfigure.getName()
				+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX, dbConfigure.getUsername(),
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
					dbConfigure.getScheme() + "://" + dbConfigure.getHost() + ":" + dbConfigure.getPort() + "/"
							+ "?characterEncoding=" + dbConfigure.getEncoding()+"&"+SUFFIX+"&zeroDateTimeBehavior=convertToNull",
					dbConfigure.getUsername(), dbConfigure.getPassword());
			return connection == null ? false : true;
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
		ps.execute();
		return ps;
	}
	public ResultSet executeQuery(String sql){
		try {
			Connection connection = this.getConnection();
			PreparedStatement ps = (PreparedStatement) connection
					.prepareStatement(sql);
			this.releaseConnection(connection);
			return ps.executeQuery();
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
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
			log.error(e);
		}
		return 0;
	}
	public PreparedStatement execute(String sql,int... statement){
		try{
				Connection connection = this.getConnection();
				PreparedStatement ps;
				if(statement.length==0)
					ps = (PreparedStatement) connection.prepareStatement(sql);
				else
					ps = (PreparedStatement) connection.prepareStatement(sql,statement[0]);
				this.releaseConnection(connection);
				ps.execute();
				return ps;
			} catch (SQLException e) {
				log.error(e);
				e.printStackTrace();
			}
			return null;
	}
}
