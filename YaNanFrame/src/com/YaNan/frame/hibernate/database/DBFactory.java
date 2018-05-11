package com.YaNan.frame.hibernate.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.beanSupport.BeanFactory;
import com.YaNan.frame.hibernate.beanSupport.XMLBean;
import com.YaNan.frame.hibernate.database.cache.Class2TabMappingCache;
import com.YaNan.frame.hibernate.database.entity.Package;
import com.YaNan.frame.hibernate.database.entity.Tab;
import com.YaNan.frame.hibernate.database.entity.Tabs;
import com.YaNan.frame.hibernate.database.exception.DATABASES_EXCEPTION;
import com.YaNan.frame.hibernate.database.exception.DataBaseException;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.path.PackageScanner;
import com.YaNan.frame.path.PackageScanner.ClassInter;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Driver;
/**
 * v2.0 增加连接池对连接进行管理，重构DBTab和DataBase功能
 *      修复连接慢，内存泄漏问题
 * @author tja41
 *
 */
public class DBFactory {
	private static DBFactory dbFactory;
	private Map<String,DataBase> dbMap = new HashMap<String,DataBase>();
	private DataBase defaultDB = null;
	private File xmlFile;//new File("src/hibernate.xml");//
	private String classPath;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,DBFactory.class);
	public static DBFactory getDBFactory(){
		if (dbFactory ==null)
				dbFactory=new DBFactory();
		return dbFactory;
	}
	public Map<Class<?>, DBTab> getTabMappingCaches(){
		return Class2TabMappingCache.getDBTabelsMap();
	}
	private DBFactory(){};
	public void init() throws Exception{
		if(xmlFile==null)
			xmlFile =new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "),"hibernate.xml");
		if(!xmlFile.exists())throw new Exception("hibernate.xml is not exist!");
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("dataBase");
		xmlBean.addNameMaping("default", "defaulted");
		xmlBean.setBeanClass(DataBaseConfigure.class);
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		while(iterator.hasNext()){
			this.builder((DataBaseConfigure)iterator.next());
		}
	}
	public void init(File xmlFile){
		this.xmlFile = xmlFile;
		try {
			this.init();
		} catch (Exception e) {
			log.error(e);
		}
	}
	private void addDB(String id, DataBase dbi) {
		this.dbMap.put(id,dbi);
	}
	public static DataBase getDataBase(String dbName){
		return dbFactory.getDataBaseByName(dbName);
	}
	private DataBase getDataBaseByName(String Id){
		return this.dbMap.get(Id);
	}
	
	public void initTabs() {
		if(classPath==null)
			classPath = this.getClass().getClassLoader().getResource("").getPath().replace("%20"," ");
		this.initTables(classPath, null);
		this.initTabs(classPath);
	}
	public void initTabs(String classPath) {
		log.debug("Parse database table!");
		//初始化包扫描注解
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("package");
		xmlBean.setBeanClass(Package.class);
		xmlBean.addNameMaping("package","PACKAGE");
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		//初始化配置式
		xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("tabs");
		xmlBean.setBeanClass(Tabs.class);
		xmlBean.removeNode("tab");
		lists = xmlBean.execute();
		iterator = lists.iterator();
		while(iterator.hasNext()){
			Tabs tabs = (Tabs) iterator.next();
			String xmlPath = "//Hibernate"+"//tabs[@db='"+tabs.getDb()+"']";
			initTab(xmlPath,tabs);
		}
	}
	public void initTables(String classPath,String pkg){
		PackageScanner scanner = new PackageScanner();
		if(classPath==null)
			classPath = this.getClass().getClassLoader().getResource("").getPath().replace("%20"," ");
		scanner.setClassPath(classPath);
		if(pkg!=null&&!pkg.equals("*"))
			scanner.setPackageName(pkg);
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				if(cls.getAnnotation(com.YaNan.frame.hibernate.database.annotation.Tab.class)!=null){
					log.debug("scan hibernate class:"+cls.getName());
					new DBTab(cls);
				}
			}
		});
	}
	public void initTab(String xmlPath,Tabs tabs){
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath(xmlPath);
		xmlBean.setNodeName("tab");
		xmlBean.setBeanClass(Tab.class);
		xmlBean.addNameMaping("class","CLASS");
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		while(iterator.hasNext()){
			Tab tab = (Tab) iterator.next();
			tab.setDb(tabs.getDb());
			tabs.addTab(tab);
			try {
				DBTab dbtab = new DBTab(tab);
				if (!dbtab.exists())
					dbtab.create(new Create(dbtab));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.error(e);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}
	}
	public static DataBase getDefaultDB() throws DataBaseException{
		if(dbFactory==null) throw new DataBaseException(DATABASES_EXCEPTION.NOT_INIT);
		return dbFactory.defaultDB;
	}
	public static boolean HasDB(String dbName) throws DataBaseException{
		if(dbFactory==null) throw new DataBaseException(DATABASES_EXCEPTION.NOT_INIT);
		return dbFactory.dbMap.containsKey(dbName);
	}
	public void destory() {
		Iterator<DataBase> iterator = this.dbMap.values().iterator();
		while(iterator.hasNext()){
			iterator.next().destory();
		}
		this.deregistDriver();
	}
	public void deregistDriver(){
		for (Enumeration<java.sql.Driver> e = DriverManager.getDrivers(); e
			     .hasMoreElements();) {
		    Driver driver = (Driver) e.nextElement();
		    if (driver.getClass().getClassLoader() == getClass()
		      .getClassLoader()) {
		     try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e1) {
				e1.printStackTrace();
				log.error(e1);
			}
		    }
		}
	}
	public Map<String, DataBase> getDataBases() {
		return dbMap;
	}
	public DataBase builder(DataBaseConfigure dataSource) {
		DataBase db = new DataBase(dataSource);
		db.create();
		db.init();
		if(defaultDB==null||(dataSource.getDefaulted()!=null&&dataSource.getDefaulted().equals("default")))
			this.defaultDB = db;
		this.addDB(dataSource.getName(),db);
		return db;
	}
}
