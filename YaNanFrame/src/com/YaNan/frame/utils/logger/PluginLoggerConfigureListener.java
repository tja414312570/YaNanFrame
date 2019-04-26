package com.YaNan.frame.utils.logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class PluginLoggerConfigureListener implements ServletContextListener{
	private static String CONFIG_LOCATION="loggerConfigLocation";
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		String location = arg0.getServletContext().getInitParameter(CONFIG_LOCATION);
//		LoggerContext loggerContext = LoggerFactory.getILoggerFactory();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
	}

}
