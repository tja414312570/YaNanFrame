package com.YaNan.frame.hibernate.database;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Connection;

public class ConnectionPools {
	 private DataBase dataBase;
     private Vector<Connection> all = null; // 存放连接池中数据库连接的向量 , 初始时为 null
     private Vector<Connection> free = null; //空闲的连接
     private Log log = PlugsFactory.getPlugsInstance(Log.class,ConnectionPools.class);
     private ConnectionPools(DataBase db) {
    	 this.dataBase = db;
    	 this.all = new Vector<Connection>();
    	 this.free = new Vector<Connection>();
    	 this.initial();
	}
     /**
      * 获取连接池
      * @param db
      * @return
      */
	public synchronized static ConnectionPools getConnectionPools(DataBase db){
    	 if(db==null)
    		 return null;
    	 return new ConnectionPools(db);
     }
	/*
	 * 初始化连接池
	 * 从DataBase中创建一个Connection并保存在all与free中
	 */
	private synchronized void initial(){
		log.debug("****初始化数据库连接池["+this.dataBase.getDbConfigure().getName()+"]****");
		log.debug("连接池配置:"+this.dataBase.getDbConfigure().toString());
		this.create(this.dataBase.getInitialConnections());
	}
	private synchronized void create(int num){
		for(int i=0;i< num;i++){
			try {
				Connection connection = this.dataBase.createConnection();
				this.all.add(connection);
				this.free.add(connection);
			} catch (ClassNotFoundException | SQLException e) {
				log.error(e);
			}
		}
	}
	/*
	 * 自动增加连接
	 */
	private synchronized void increase(){
		if(this.all.size()<this.dataBase.getMaxConnections()){
			this.create(this.dataBase.getIncrementalConnections());
		}
	}
	/*
	 * 关闭连接
	 * 如果空闲连接数量超过增加值得一倍，则自动关闭一半连接
	 */
	private synchronized void refresh(){
		if(this.all.size()>this.dataBase.getInitialConnections())
		if(this.free.size()>this.dataBase.getIncrementalConnections()*2){
			for(int i = 0;i<this.dataBase.getIncrementalConnections();i++){
				Connection connection = this.free.get(i);
				for( int j=0;j<this.all.size();j++){
					if(connection==this.all.get(j)){
						try {
							connection.close();
						} catch (SQLException e) {
							log.error(e);
						}
						this.all.remove(j);
						this.free.remove(i);
					}
				}
			}
		}
	}
	/**
	 * 获取可用的连接
	 * @return
	 */
	public synchronized Connection getConnection(){
		Connection connection;
		if(this.all.size()==0)//没有初始化时，首先初始化
			this.initial();
		if(this.free.size()==0){//如果没有空闲连接，首先判断当前所有的连接数量
			if(this.all.size()>=this.dataBase.getMaxConnections()){//如果所有连接数达到最大值，则发起wait
				try {
					this.wait();
					return this.getConnection();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}else{//如果最大连接数没有满，则调用自动增加
				this.increase();
				connection = this.free.get(0);
			}
		}else//存在空闲连接
			connection = this.free.get(0);
		this.free.remove(connection);
		return connection;
	}
	/**
	 * 归还连接
	 */
	public synchronized void release(Connection connection){
		if(connection!=null){
			this.free.add(connection);
			this.refresh();
			this.notify();
		}
	}
	/**
	 * 关闭连接池
	 * @return
	 */
	public synchronized void destory(){
		//清空free
		this.free.clear();
		//关闭所有连接
		Enumeration<Connection> elements = this.all.elements();
		while(elements.hasMoreElements()){
			try {
				elements.nextElement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.all.clear();
	}
	public int getFreeNum() {
		return this.free.size();
	}
	public int getAllNum(){
		return this.all.size();
	}
}
