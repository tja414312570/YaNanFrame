package com.YaNan.frame.hibernate.database;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.Create;
import com.YaNan.frame.hibernate.database.DBTab;
import com.YaNan.frame.hibernate.database.DataBase;
import com.YaNan.frame.hibernate.database.DataBaseConfigure;
import com.YaNan.frame.hibernate.database.cache.Class2TabMappingCache;
import com.YaNan.frame.hibernate.database.entity.BaseMapping;
import com.YaNan.frame.hibernate.database.entity.Package;
import com.YaNan.frame.hibernate.database.entity.SqlFragmentManger;
import com.YaNan.frame.hibernate.database.entity.Tab;
import com.YaNan.frame.hibernate.database.entity.Tabs;
import com.YaNan.frame.hibernate.database.entity.WrapperConfgureMapping;
import com.YaNan.frame.hibernate.database.entity.WrapperMapping;
import com.YaNan.frame.hibernate.database.exception.DATABASES_EXCEPTION;
import com.YaNan.frame.hibernate.database.exception.DataBaseException;
import com.YaNan.frame.hibernate.database.exception.HibernateInitException;
import com.YaNan.frame.hibernate.database.fragment.FragmentBuilder;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.path.PackageScanner;
import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.path.PackageScanner.ClassInter;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.util.beans.BeanFactory;
import com.YaNan.frame.util.beans.XMLBean;
import com.YaNan.frame.util.beans.xml.XMLHelper;
import com.mysql.jdbc.Driver;
import com.YaNan.frame.reflect.cache.ClassHelper;

/**
 * v2.0 增加连接池对连接进行管理，重构DBTab和DataBase功能 修复连接慢，内存泄漏问题
 * 
 * @author tja41
 *
 */
public class DBFactory {
	private static DBFactory dbFactory;
	// 数据库存储对象 数据库名 ==》 数据库
	private Map<String, DataBase> dbMap = new HashMap<String, DataBase>();
	private DataBase defaultDB = null;
	private File xmlFile;// new File("src/hibernate.xml");//
	private String classPath;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, DBFactory.class);
	private Map<String, BaseMapping> wrapMap = new HashMap<String, BaseMapping>();

	public static DBFactory getDBFactory() {
		if (dbFactory == null) {
			synchronized (DBFactory.class) {
				if (dbFactory == null) {
					dbFactory = new DBFactory();
				}
			}
		}
		return dbFactory;
	}

	public Map<Class<?>, DBTab> getTabMappingCaches() {
		return Class2TabMappingCache.getDBTabelsMap();
	}

	private DBFactory() {
	};

	/**
	 * 用于初始化hibernate.xml，创建对应的对应DataBase和连接池等
	 * 
	 * @throws Exception
	 */
	public void init(){
		log.debug("init hibernate configure!");
		if (xmlFile == null)
			xmlFile = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20", " "),
					"hibernate.xml");
		if (!xmlFile.exists())
			throw new DataBaseException(DATABASES_EXCEPTION.NOT_CONF,
					"database configure file \"" + xmlFile + "\" is not exist!");
		XMLHelper help = new XMLHelper();
		help.setFile(this.xmlFile);
		help.setMapping(WrapperConfgureMapping.class);
		List<WrapperConfgureMapping> list = help.read();
		if (list.size() == 0)
			throw new DataBaseException(DATABASES_EXCEPTION.NOT_CONF,
					"database configure file \"" + xmlFile + "\" could not container Hibernate tags!");
		WrapperConfgureMapping wrapper = list.get(0);
		// 获取数据库配置
		DataBaseConfigure[] dataBases = wrapper.getDataBases();
		for (DataBaseConfigure dataBaseConf : dataBases) {
			// 构建数据库
			this.builder(dataBaseConf);
		}
		// 获取mapper配置
		String[] wrappers = wrapper.getWrapper();
		if (wrappers == null || wrappers.length == 0)
			return;
		// 获取所有的wrapper xml文件
		List<File> files = ResourceManager.getResource(wrappers[0]);
		log.debug("get wrap file num : " + files.size());
		Iterator<File> fileIterator = files.iterator();
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			log.debug("scan wrap file : " + file.getAbsolutePath());
			XMLHelper helper = new XMLHelper(file, WrapperMapping.class);
			List<WrapperMapping> wrapps = helper.read();
			if (wrapps != null && wrapps.size() != 0) {
				List<BaseMapping> baseMapping = wrapps.get(0).getBaseMappings();
				Iterator<BaseMapping> mappingIterator = baseMapping.iterator();
				String namespace = wrapps.get(0).getNamespace();
				ClassHelper classHelper = null;
				try {
					Class<?> clzz = Class.forName(namespace);
					classHelper = ClassHelper.getClassHelper(clzz);
				} catch (ClassNotFoundException e) {
//						throw new HibernateInitException("wrapper interface class \""+namespace+"\" is not exists ! at file \""+file.getAbsolutePath()+"\"");
				}
				while (mappingIterator.hasNext()) {
					BaseMapping mapping = mappingIterator.next();
					mapping.setWrapperMapping(wrapps.get(0));
					if(classHelper!=null){
						Method[] methods = classHelper.getDeclaredMethods();
						boolean find = false;
						for(Method method : methods){
							if(method.getName().equals(mapping.getId())){
								find = true;
								continue;
							}
						}
						if(!find)
							throw new HibernateInitException("wrapper method \""+mapping.getId()+"\" at interface class \""+namespace+"\" is not exists ! at file \""+file.getAbsolutePath()+"\"");
					}
					String sqlId = namespace + "." + mapping.getId();
					wrapMap.put(sqlId, mapping);
					log.debug("found wrap id " +sqlId+" ; content : "+mapping.getContent().trim());
				}
			}
		}
		Iterator<BaseMapping> iterator = this.wrapMap.values().iterator();
		while (iterator.hasNext())
			this.buildFragment(iterator.next());

		// XMLBean xmlBean = BeanFactory.getXMLBean();
		// xmlBean.addXMLFile(xmlFile);
		// xmlBean.addElementPath("//Hibernate");
		// xmlBean.setNodeName("dataBase");
		// xmlBean.addNameMaping("default", "defaulted");
		// xmlBean.setBeanClass(DataBaseConfigure.class);
		// List<Object> lists = xmlBean.execute();
		// Iterator<Object> iterator = lists.iterator();
		// while(iterator.hasNext()){
		// this.builder((DataBaseConfigure)iterator.next());
		// }
	}

	public SqlFragment buildFragment(BaseMapping mapping) {
		SqlFragment sqlFragment = null;
		try {
			sqlFragment = SqlFragmentManger
					.getSqlFragment(mapping.getWrapperMapping().getNamespace() + "." + mapping.getId());
		} catch (Exception e) {
		}
		if (sqlFragment == null) {
			PlugsHandler handler = PlugsFactory.getPlugsHandler(mapping);
			FragmentBuilder fragmentBuilder = PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
					handler.getProxyClass().getName() + ".root");
			log.debug("build " + mapping.getNode().toUpperCase() + " wrapper fragment , wrapper id : \""
					+ mapping.getWrapperMapping().getNamespace() + "." + mapping.getId() + "\" ;");
			fragmentBuilder.build(mapping);
			sqlFragment = (SqlFragment) fragmentBuilder;
			SqlFragmentManger.addWarp(sqlFragment);
		}
		return sqlFragment;
	}

	public void init(File xmlFile) {
		this.xmlFile = xmlFile;
		this.init();
	}

	private void addDB(String id, DataBase dbi) {
		this.dbMap.put(id, dbi);
	}

	public static DataBase getDataBase(String dbName) {
		if (dbFactory == null)
			throw new RuntimeException("DataBase Is Not Init!");
		return dbFactory.getDataBaseByName(dbName);
	}

	private DataBase getDataBaseByName(String Id) {
		return this.dbMap.get(Id);
	}

	public void initTabs() {
		if (classPath == null)
			classPath = this.getClass().getClassLoader().getResource("").getPath().replace("%20", " ");
		this.initTables(classPath, null);
		this.initTabs(classPath);
	}

	public void initTabs(String classPath) {
		log.debug("Parse database table!");
		// 初始化包扫描注解
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("package");
		xmlBean.setBeanClass(Package.class);
		xmlBean.addNameMaping("package", "PACKAGE");
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		// 初始化配置式
		xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("tabs");
		xmlBean.setBeanClass(Tabs.class);
		xmlBean.removeNode("tab");
		lists = xmlBean.execute();
		iterator = lists.iterator();
		while (iterator.hasNext()) {
			Tabs tabs = (Tabs) iterator.next();
			String xmlPath = "//Hibernate" + "//tabs[@db='" + tabs.getDb() + "']";
			initTab(xmlPath, tabs);
		}
	}

	public void initTables(String classPath, String pkg) {
		PackageScanner scanner = new PackageScanner();
		if (classPath == null)
			classPath = this.getClass().getClassLoader().getResource("").getPath().replace("%20", " ");
		scanner.setClassPath(classPath);
		if (pkg != null && !pkg.equals("*"))
			scanner.setPackageName(pkg);
		scanner.doScanner(new ClassInter() {
			@Override
			public void find(Class<?> cls) {
				if (cls.getAnnotation(com.YaNan.frame.hibernate.database.annotation.Tab.class) != null) {
					log.debug("scan hibernate class:" + cls.getName());
					new DBTab(cls);
				}
			}
		});
	}

	public void initTab(String xmlPath, Tabs tabs) {
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(xmlFile);
		xmlBean.addElementPath(xmlPath);
		xmlBean.setNodeName("tab");
		xmlBean.setBeanClass(Tab.class);
		xmlBean.addNameMaping("class", "CLASS");
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		while (iterator.hasNext()) {
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

	public static DataBase getDefaultDB() {
		if (dbFactory == null)
			throw new DataBaseException(DATABASES_EXCEPTION.NOT_INIT);
		return dbFactory.defaultDB;
	}

	public static boolean HasDB(String dbName) {
		if (dbFactory == null)
			throw new DataBaseException(DATABASES_EXCEPTION.NOT_INIT);
		return dbFactory.dbMap.containsKey(dbName);
	}

	public void destory() {
		// ConnectionPools.getConnectionpoolRefreshService().destory();
		Iterator<DataBase> iterator = this.dbMap.values().iterator();
		while (iterator.hasNext()) {
			iterator.next().destory();
		}
		this.deregistDriver();
	}

	public void deregistDriver() {
		for (Enumeration<java.sql.Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
			Driver driver = (Driver) e.nextElement();
			if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
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
		if (defaultDB == null || (dataSource.getDefaulted() != null && dataSource.getDefaulted().equals("default")))
			this.defaultDB = db;
		this.addDB(dataSource.getName(), db);
		return db;
	}

	public Map<String, BaseMapping> getWrapMap() {
		return wrapMap;
	}

	public BaseMapping getWrapMap(String id) {
		return wrapMap.get(id);
	}

	public void setWrapMap(Map<String, BaseMapping> wrapMap) {
		this.wrapMap = wrapMap;
	}
}
