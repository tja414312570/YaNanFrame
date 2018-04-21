package com.YaNan.frame.hibernate.database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.YaNan.frame.plugin.annotations.Register;

@Register
public class HibernateContextInit implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		DBFactory.getDBFactory().init();
		DBFactory.getDBFactory().initTabs();
	}

}
