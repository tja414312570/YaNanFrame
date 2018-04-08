package com.YaNan.frame.core;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.dom4j.Element;

import com.YaNan.frame.core.init.InitXML;
import com.YaNan.frame.core.servlet.InitServlet;
import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.DefaultServletMapping;
import com.YaNan.frame.core.session.TokenManager;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.security.DH;
import com.YaNan.frame.service.ClassInfo;
import com.YaNan.frame.service.Log;

/**
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
@ClassInfo(version = 0, author = "YaNan")
public class AppContext {
	private static AppContext appcontext;
	private ServletContextEvent servletContextEvent;
	private boolean InitCompleted=false;

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
			Log.getSystemLog().write("init start");
			Log.getSystemLog().write("AppContext init:please waiting...");
			Log.getSystemLog().write("get webPath object");
			WebPath webPath = WebPath.getWebPath();
			Log.getSystemLog().write("get webPath object done");
			Log.getSystemLog().write("request get app root realPath:"+File.separator);
			ServletContext servletContext = servletContextEvent.getServletContext();
			String rootPath = servletContext.getRealPath("/");
			Log.getSystemLog().write("get servletRootPath :" + rootPath);
			Log.getSystemLog().write("init rootPath...");
			webPath.setRoot(rootPath);
			Log.getSystemLog().write("decode init.xml");
			Log.getSystemLog().write("done...");
			Log.getSystemLog().write("traverse Path");
			InitXML.init();
			List<?> list = InitXML.getPath();
			Log.getSystemLog().write("get path size:" + list.size());
			if (list.size() > 0)
				for (int i = 0; i < list.size(); i++) {
					String id = ((Element)list.get(i)).attributeValue("id");
					String path = InitXML.getPathById(id);
					Log.getSystemLog().write("init webPath " + id + ",addr=" + path);
					webPath.set(id, path);
	
				}
			Log.getSystemLog().write("Init servletXml");
			InitServlet.getInstance();
			Log.getSystemLog().write("Retrieve the servlet configuration");
			Log.getSystemLog().write("get servlet Mapping Object");
			DefaultServletMapping servletManager = DefaultServletMapping
					.getInstance();
			Log.getSystemLog().write("get Servlet Mapping iterator");
			Iterator<String> iterator = servletManager.getActionKSIterator();
			Log.getSystemLog().write("get servlet Mapping");
			Map<String, Map<String, ServletBean>> servletMapping = servletManager
					.getServletMapping();
			Log.getSystemLog().write("Traverse the servlet collection");
			while (iterator.hasNext()) {
				String key = iterator.next();
				Map<String, ServletBean> namespaceMap = servletMapping.get(key);
				Iterator<String> namespaceIterator = namespaceMap.keySet()
						.iterator();
				while (namespaceIterator.hasNext()) {
					String namespace = namespaceIterator.next();
					Log.getSystemLog().write("---------------------------------------------------------");
					Log.getSystemLog().write("namespace:" + key + ",servlet:" + namespace
							+ ",servletbean:" +  namespaceMap.get(namespace).getClassName());
					Log.getSystemLog().write("---------------------------------------------------------");
				}
	
			}
			DBFactory.getDBFactory().init();
			DBFactory.getDBFactory().initTabs();
			TokenManager.init();
			DH.init();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			this.InitCompleted=true;
		}
		Log.getSystemLog().write("application context inited!");
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
