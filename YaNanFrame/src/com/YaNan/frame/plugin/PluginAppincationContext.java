package com.YaNan.frame.plugin;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Web容器的Plug组件的初始化
 * @author yanan
 *
 */
public class PluginAppincationContext implements ServletContextListener {
	private static boolean isWebContext;
	private Logger log = LoggerFactory.getLogger(PluginAppincationContext.class);
	private ServletContextEvent servletContextEvent;
	private List<ServletContextListener> contextListernerList;
	private ServletContext servletContext;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(contextListernerList!=null)
			for(ServletContextListener contenxtInitListener :contextListernerList){
				contenxtInitListener.contextDestroyed(arg0);
			}
		if(log!=null)
		log.debug("Plugin conetxt has destory at "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.gc();//通过gc清除无用的对象
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		isWebContext = true;
		try{
			long t = System.currentTimeMillis();
			String locations = servletContextEvent.getServletContext().getInitParameter("contextConfigure");
			if(locations!=null)
				locations = locations.replace("project:", servletContextEvent.getServletContext().getRealPath(""));
			this.servletContextEvent = servletContextEvent;
			this.servletContext = servletContextEvent.getServletContext();
			log.debug("");
			log.debug("       PLUGINPLUGID     PLUE         PLUA       PLUR      PLUGINPLUGIY    PLUGINPLUGIN   PLUG         PLUG");
			log.debug("      PLUG      PLUW   PLUA         PLUN       PLUG    PLUG                  PLUG       PLUGPLUG     PLUG");
			log.debug("     PLUG      PLUH   PLUO         PLUN       PLUG   PLUG                   PLUG       PLUG PLUG    PLUG");
			log.debug("    PLUG      PLUY   PLUA         PLUN       PLUG   PLUG                   PLUG       PLUG  PLUG   PLUG");
			log.debug("   PLUGPLUGPLUGI    PLUG         PLUG       PLUG   PLUG      PLUGINP      PLUG       PLUG   PLUG  PLUG");
			log.debug("  PLUN             PLUE         PLUV       PLUE   PLUR         PLUG      PLUG       PLUG    PLUG PLUG");
			log.debug(" PLUG             PLUI         PLUV       PLUE     PLUU       PLUP      PLUG       PLUG     PLUGINPL");
			log.debug("PLUY             PLUGINPLUGO   PLUGINPLUGINU        PLUGINPLUGIG    PLUGINPLUGIE  PLUG         PLUG");
			log.debug("");
			PlugsFactory.init(locations);
			contextListernerList = PlugsFactory.getPlugsInstanceList(ServletContextListener.class);
			log.debug("Context Init Plug size:"+contextListernerList.size());
			for(ServletContextListener contenxtInitListener :contextListernerList){
				log.debug("Plug Instance:"+contenxtInitListener);
				contenxtInitListener.contextInitialized(servletContextEvent);
			}
			log.debug("Plugin conetxt init has completed in "+(System.currentTimeMillis()-t)+"ms");
			System.gc();//通过gc清除启动时创建的对象
		}catch (Throwable e){
			log.error(e.getMessage(),e);
			System.exit(1);
		}
		
	}

	public static void setWebContext(boolean isWebContext) {
		PluginAppincationContext.isWebContext = isWebContext;
	}

	public ServletContextEvent getServletContextEvent() {
		return servletContextEvent;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public static boolean isWebContext() {
		return isWebContext;
	}


}
