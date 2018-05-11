package com.YaNan.frame.hibernate.database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;

@Register
public class HibernateContextInit implements ServletContextListener {
	private Log log  = PlugsFactory.getPlugsInstance(Log.class, HibernateContextInit.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			DBFactory.getDBFactory().init();
		} catch (Exception e) {
			log.error(e);
		}
		DBFactory.getDBFactory().initTabs();
	}

}
