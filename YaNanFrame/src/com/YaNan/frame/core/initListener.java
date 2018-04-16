package com.YaNan.frame.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.YaNan.frame.support.ReserveManager;
import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;
@WebListener
public class initListener implements ServletContextListener {
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,initListener.class);
	AppContext context;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		context.destory();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		 context= AppContext.getContext(servletContextEvent);
		try {
			ClassLoader loader = new ClassLoader(context);
			loader.invokeMethod("init");
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			ReserveManager.getReserve().enable(true);
		}

	}

}
