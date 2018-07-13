package com.YaNan.frame.hibernate.database;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Register
public class HibernateContextInit implements ServletContextListener {
	private Log log  = PlugsFactory.getPlugsInstance(Log.class, HibernateContextInit.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.debug("Destory the hibernate services");
		//关闭数据驱动连接线程
		 Enumeration<Driver> drivers = DriverManager.getDrivers();
	        Driver d = null;
	        while (drivers.hasMoreElements()) {
	            try {
	                d = drivers.nextElement();
	                DriverManager.deregisterDriver(d);
	            } catch (SQLException ex) {
	            	log.error(ex);
	            }
	        }
	        try {
	            AbandonedConnectionCleanupThread.shutdown();
	        } catch (InterruptedException e) {
	           log.error(e);;
	        }
	       //销毁数据库服务
	       DBFactory.getDBFactory().destory();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			//初始化数据库工厂
			DBFactory.getDBFactory().init();
		} catch (Exception e) {
			log.error(e);
		}
		//初始化数据表
		DBFactory.getDBFactory().initTabs();
	}

}
