package com.a.encrypt.entity;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;

public class HibernateContextInit implements ServletContextListener {
	private Log log  = PlugsFactory.getPlugsInstance(Log.class, HibernateContextInit.class);
	private File location;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SqlSessionFactory factory  = PlugsFactory.getPlugsInstance(SqlSessionFactory.class);
		factory.builder();
	}

	@Override
	public String toString() {
		return "HibernateContextInit [log=" + log + ", location=" + location + "]";
	}
}
