package com.YaNan.frame.hibernate.database;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class HibernateContextInit implements ServletContextListener {
	private Logger log  = LoggerFactory.getLogger(HibernateContextInit.class);
	private File location;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.debug("Destory the hibernate services");
		 Enumeration<Driver> drivers = DriverManager.getDrivers();
	        Driver d = null;
	        while (drivers.hasMoreElements()) {
	            try {
	                d = drivers.nextElement();
	                DriverManager.deregisterDriver(d);
	            } catch (SQLException ex) {
	            	log.error(ex.getMessage(),ex);
	            }
	        }
	        try {
	            AbandonedConnectionCleanupThread.shutdown();
	        } catch (InterruptedException e) {
	        	log.error(e.getMessage(),e);
	        }
	       //销毁数据库服务
	       DBFactory.getDBFactory().destory();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//初始化数据库工厂
		DBFactory.getDBFactory().init(this.location);
		//初始化数据表
		DBFactory.getDBFactory().initTabs();
	}

}
