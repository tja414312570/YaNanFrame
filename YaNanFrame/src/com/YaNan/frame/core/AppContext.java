package com.YaNan.frame.core;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.dom4j.Element;

import com.YaNan.frame.core.init.InitXML;
import com.YaNan.frame.core.servlet.ServletBuilder;
import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.DefaultServletMapping;
import com.YaNan.frame.core.session.TokenManager;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

/**
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class AppContext {
	private static AppContext appcontext;
	private ServletContextEvent servletContextEvent;
	private boolean InitCompleted=false;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, AppContext.class);
	/*
	 * single context
	 */
	private AppContext() {

	}

	private AppContext(ServletContextEvent servletContextEvent) {
		this.servletContextEvent = servletContextEvent;
	}

	/**
	 * get the AppContext
	 */
	public static AppContext getContext(ServletContextEvent servletContextEvent) {
		appcontext = (appcontext == null ? new AppContext(servletContextEvent)
				: appcontext);
		return appcontext;
	}

	public static AppContext getContext() {
		return appcontext;
	}

	public void init() throws Exception {
		try{
			log.debug("init start");
			log.debug("AppContext init:please waiting...");
			log.debug("get webPath object");
			WebPath webPath = WebPath.getWebPath();
			log.debug("get webPath object done");
			log.debug("request get app root realPath:"+File.separator);
			ServletContext servletContext = servletContextEvent.getServletContext();
			String rootPath = servletContext.getRealPath("/");
			log.debug("get servletRootPath :" + rootPath);
			log.debug("init rootPath...");
			webPath.setRoot(rootPath);
			log.debug("decode init.xml");
			log.debug("done...");
			log.debug("traverse Path");
			InitXML.init();
			List<?> list = InitXML.getPath();
			log.debug("get path size:" + list.size());
			if (list.size() > 0)
				for (int i = 0; i < list.size(); i++) {
					String id = ((Element)list.get(i)).attributeValue("id");
					String path = InitXML.getPathById(id);
					log.debug("init webPath " + id + ",addr=" + path);
					webPath.set(id, path);
	
				}
			log.debug("Init servletXml");
			ServletBuilder.getInstance();
			log.debug("Retrieve the servlet configuration");
			log.debug("get servlet Mapping Object");
			DefaultServletMapping servletManager = DefaultServletMapping
					.getInstance();
			log.debug("get Servlet Mapping iterator");
			Iterator<String> iterator = servletManager.getActionKSIterator();
			log.debug("get servlet Mapping");
			Map<String, ServletBean> servletMapping = servletManager
					.getServletMapping();
			log.debug("Traverse the servlet collection");
			while (iterator.hasNext()) {
				String key = iterator.next();
				ServletBean bean = servletMapping.get(key);
				log.debug("---------------------------------------------------------");
				log.debug("url mapping:" + key + ",servlet method:" + bean.getMethod()
						+ ",servlet type:" +bean.getType());
				log.debug("---------------------------------------------------------");
	
			}
			DBFactory.getDBFactory().init();
			DBFactory.getDBFactory().initTabs();
			TokenManager.init();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			this.InitCompleted=true;
		}
		log.debug("application context inited!");
	}
	
	public boolean isInitCompleted() {
		return this.InitCompleted;
	}

	public void end() {

	}

	public ServletContextEvent getServletContextEvent() {
		return servletContextEvent;
	}

	public void setServletContextEvent(ServletContextEvent servletContextEvent) {
		this.servletContextEvent = servletContextEvent;
	}

	public void destory() {
		DBFactory.getDBFactory().destory();
	}
}
