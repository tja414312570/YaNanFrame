package com.YaNan.frame.plugin;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.YaNan.frame.logging.Log;
@WebListener
public class PluginAppincationContextInit implements ServletContextListener {
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,PluginAppincationContextInit.class);
	private List<ServletContextListener> contextListernerList;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(contextListernerList!=null)
			for(ServletContextListener contenxtInitListener :contextListernerList){
				contenxtInitListener.contextDestroyed(arg0);
			}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		log.debug("       PLUGINPLUGIN     PLUG         PLUG       PLUG      PLUGINPLUGIN    PLUGINPLUGIN   PLUG         PLUG");
		log.debug("      PLUG      PLUG   PLUG         PLUG       PLUG    PLUG                  PLUG       PLUGPLUG     PLUG");
		log.debug("     PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG PLUG    PLUG");
		log.debug("    PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG  PLUG   PLUG");
		log.debug("   PLUGPLUGPLUGI    PLUG         PLUG       PLUG   PLUG      PLUGINP      PLUG       PLUG   PLUG  PLUG");
		log.debug("  PLUG             PLUG         PLUG       PLUG   PLUG         PLUG      PLUG       PLUG    PLUG PLUG");
		log.debug(" PLUG             PLUG         PLUG       PLUG     PLUG       PLUG      PLUG       PLUG     PLUGINPL");
		log.debug("PLUG             PLUGINPLUGI   PLUGINPLUGINP        PLUGINPLUGIN    PLUGINPLUGIN  PLUG         PLUG");
		log.debug("");
		List<ServletContextListener> contextListernerList = PlugsFactory.getPlugsInstanceList(ServletContextListener.class);
		log.debug("Context Init Plug number:"+contextListernerList.size());
		for(ServletContextListener contenxtInitListener :contextListernerList){
			log.debug("Plug Instance:"+contenxtInitListener);
			contenxtInitListener.contextInitialized(servletContextEvent);
		}
		log.debug("get Context Listener ");
	}

}
