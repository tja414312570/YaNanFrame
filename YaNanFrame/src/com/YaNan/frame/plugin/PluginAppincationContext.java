package com.YaNan.frame.plugin;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.YaNan.frame.logging.Log;

/**
 * Web容器的Plug组件的初始化
 * @author yanan
 *
 */
@WebListener
public class PluginAppincationContext implements ServletContextListener {
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,PluginAppincationContext.class);
	private ServletContextEvent servletContextEvent;
	private List<ServletContextListener> contextListernerList;
	private ServletContext servletContext;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(contextListernerList!=null)
			for(ServletContextListener contenxtInitListener :contextListernerList){
				contenxtInitListener.contextDestroyed(arg0);
			}
		log.debug("Plugin conetxt has destory at "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.gc();//通过gc清除无用的对象
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		long t = System.currentTimeMillis();
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
		try{
			contextListernerList = PlugsFactory.getPlugsInstanceList(ServletContextListener.class);
			log.debug("Context Init Plug number:"+contextListernerList.size());
			for(ServletContextListener contenxtInitListener :contextListernerList){
				log.debug("Plug Instance:"+contenxtInitListener);
				contenxtInitListener.contextInitialized(servletContextEvent);
			}
			log.debug("Plugin conetxt init has completed in "+(System.currentTimeMillis()-t)+"ms");
			System.gc();//通过gc清除启动时创建的对象
		}catch (Throwable e){
			log.error(e);
			System.exit(1);
		}
		
	}

	public ServletContextEvent getServletContextEvent() {
		return servletContextEvent;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

}
