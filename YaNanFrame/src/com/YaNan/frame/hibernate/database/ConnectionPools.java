package com.YaNan.frame.hibernate.database;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Connection;
/**
 * 数据库连接池，用于管理数据库连接，提供数据库连接的初始化、获取、释放</br>
 * 注意！！数据库连接获取并使用之后，记得关闭PreparedStatement和ResultSet，否则导致连接池刷新逻辑失效，Connection不需要关闭</br>
 * 注意！！数据库使用完毕后，记得关闭ConnectionPoolRefreshService服务，否则导致ConnectionPoolRefreshService服务延迟关闭</br>
 * 具体关闭时间和设置的连接池空闲时等待时间有关（参数timeout）</br>
 * 通过ConnectionPoolRefreshService.destory()或DBFactory.getDBFactory().destory()可销毁该线程</br>
 * 2018-6-20~2018-6-21 把连接池刷新从释放中独立出来，新添加一个连接池刷新逻辑服务，该服务使得连接刷新</br>
 * 从连接释放中独立出来，提高数据吞吐能力，同时该服务会自动启动和销毁。</br>
 * 2018-6-18 ~ 2018-6-20 优化连接池刷新、释放、获取、添加逻辑，降低各种锁的粒度，提高运行速度，降低运行内存，</br>
 * 2016-?-？~ 2018-6-17 添加数据库连接池用于管理所有DataBase中的数据连接</br>
 * 
 * @author yanan
 *
 */
public class ConnectionPools{
	 private DataBase dataBase;//数据库对象
	 private Vector<Connection> all = null; // 存放连接池中数据库连接的向量 , 初始时为 null 
     private Vector<Connection> free = null; //空闲的连接
     private Log log = PlugsFactory.getPlugsInstance(Log.class,ConnectionPools.class);
     private final static ConnectionPoolRefreshService connectionPoolRefreshService = new ConnectionPoolRefreshService();
     public static ConnectionPoolRefreshService getConnectionpoolRefreshService() {
		return connectionPoolRefreshService;
	}
	private ConnectionPools(DataBase db) {
    	 this.dataBase = db;
    	 this.all = new Vector<Connection>();
    	 this.free = new Vector<Connection>();
    	 this.initial();
    	 connectionPoolRefreshService.addConnectionPool(this);
	}
     public Vector<Connection> getAllConnections(){
    	 return this.all;
     }
     public Vector<Connection> getFreeConnections(){
    	 return this.free;
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
	/**
	 * 初始化连接池
	 * 从DataBase中创建一个Connection并保存在all与free中
	 */
	private synchronized void initial(){
		log.debug("****初始化数据库连接池["+this.dataBase.getName()+"]****");
		log.debug("连接池配置:"+this.dataBase.getDataBaseConfigure().toString());
		this.create(this.dataBase.getDataBaseConfigure().getMinNum());
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
	/**
	 * 自动增加连接
	 */
	private synchronized void increase(){
		if(this.all.size()<this.dataBase.getDataBaseConfigure().getMaxNum()){
			this.create(this.dataBase.getDataBaseConfigure().getAddNum());
			connectionPoolRefreshService.addConnectionPool(this);
		}
	}
	 public DataBase getDataBase() {
			return dataBase;
		}
	/**
	 * 关闭连接
	 * 如果空闲连接数量超过初始化数量，则自动关闭多于的连接
	 * 刷新为归还链接时触发
	 */
	public void refresh(){
		if(this.free.size()>this.dataBase.getDataBaseConfigure().getMinNum()){
			synchronized (free) {
				if(this.free.size()>this.dataBase.getDataBaseConfigure().getMinNum()){
					Connection connection;
					Iterator<Connection> iterator = this.free.iterator();
					while(iterator.hasNext()){
							try {
								connection = iterator.next();
								if(connection.getActiveStatementCount()==0){//找到没有使用的连接关闭
									connection.close();
									this.all.remove(connection);
									iterator.remove();
								}
								if(this.free.size()<=this.dataBase.getDataBaseConfigure().getMinNum())//当空闲链接为最低链接数时，不在关闭链接
									break;
							} catch (SQLException e) {
								log.error(e);
						}
					}
				}
			}
		}
	}
	/**
	 * 获取可用的连接
	 * @return
	 */
	public Connection getConnection(){
		Connection connection;
		synchronized (free) {
			if(this.free.size()==0){
					if(this.all.size()>=this.dataBase.getDataBaseConfigure().getMaxNum()){//如果没有空闲连接，首先判断当前所有的连接数量
						try {
							free.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return this.getConnection();
					}else{
						this.increase();
					}
			}//存在空闲连接
			connection = this.free.get(0);
			this.free.remove(0);
		}
		return connection;
	}
	/**
	 * 归还连接
	 */
	public void release(Connection connection){
		if(connection!=null){
			synchronized (free) {
				this.free.add(connection);
				free.notify();
			}
		}
	}
	/**
	 * 关闭连接池
	 * @return
	 */
	public void destory(){
		//清空free
		if(this.free!=null){
			this.free.clear();
			this.free = null;
		}
		//关闭所有连接
		if(this.all!=null){
			Enumeration<Connection> elements = this.all.elements();
			while(elements.hasMoreElements()){
				try {
					elements.nextElement().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			this.all.clear();
			this.all = null;
			connectionPoolRefreshService.removeConnectionPool(this);
		}
		if(this.dataBase!=null)
			this.dataBase = null;
	}
	public int getFreeNum() {
		return this.free.size();
	}
	public int getAllNum(){
		return this.all.size();
	}
}
/**
 * 连接池刷新服务类,该类用于提供连接池的刷洗服务
 * @author yanan
 *
 */
class ConnectionPoolRefreshService implements Runnable{
	 private Thread connectionPoolRefreshThread;//用于提供服务的线程
	 transient boolean keepAlive = false;
	 private int delay = 1000;//每次数据连接池刷新时间  
	 private int timeout = 1000*60;//当所有连接为空时刷新服务等待时间 默认1 min  //用于提高效率，降低新建对象的性能开销
	 private int sleepType = 0;//睡眠类型   0 ==> 间隔睡眠   1 ==> 守护睡眠
	 private List<ConnectionPools> connectionPoolsQueue = new LinkedList<ConnectionPools>();
	 ConnectionPoolRefreshService(){};
	 public void addConnectionPool(ConnectionPools connectionPool){
		if(!this.connectionPoolsQueue.contains(connectionPool)){
			synchronized (connectionPoolsQueue) {
				if(!this.connectionPoolsQueue.contains(connectionPool)){
					connectionPoolsQueue.add(connectionPool);
				}
			}
		}
		if(connectionPoolRefreshThread==null){//保证单线程
			synchronized (this) {
				if(connectionPoolRefreshThread==null){
					keepAlive=true;
					connectionPoolRefreshThread = new Thread(this);
					connectionPoolRefreshThread.setPriority(1);
					connectionPoolRefreshThread.start();
				}
			}
		}else{
			if(sleepType==1){
				keepAlive=true;
				connectionPoolRefreshThread.interrupt();
			}
		}
	 }
	 /**
	  * 关闭刷新监控线程
	  */
	 public void shutdown(){
		 if(keepAlive)
			 keepAlive=false;
		 if(connectionPoolRefreshThread!=null&&connectionPoolRefreshThread.isAlive())
			 connectionPoolRefreshThread.interrupt();
		 if(connectionPoolRefreshThread!=null)
			 connectionPoolRefreshThread=null;
	 }
	 /**
	  * 销毁刷新监控线程
	  */
	 public void destory(){
		 if(connectionPoolsQueue!=null){
			 connectionPoolsQueue.clear();
			 connectionPoolsQueue=null;
		 }
		 this.shutdown();
	 }
	 /**
	  * 移除连接池
	  */
	 public void removeConnectionPool(ConnectionPools connectionPool){
		 if(connectionPoolsQueue!=null)
			connectionPoolsQueue.remove(connectionPool);
	}
	 @Override
	public void run() {
		while(keepAlive){
			try {
				Iterator<ConnectionPools> iterator = connectionPoolsQueue.iterator();
				ConnectionPools connectionPool;
				while(iterator.hasNext()){
					connectionPool = iterator.next();
					if(connectionPool==null||
							connectionPool.getAllConnections()==null||
							connectionPool.getAllNum()<=connectionPool.getDataBase().getDataBaseConfigure().getMinNum())
						iterator.remove();
					else
						connectionPool.refresh();
				}
				if(connectionPoolsQueue.size()==0){
					keepAlive = false;
					sleepType = 1;
					Thread.sleep(timeout);
					break;
				}
				sleepType = 0;
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
		connectionPoolRefreshThread=null;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}