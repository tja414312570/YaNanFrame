package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.servlet.DefaultDispatcher;
import com.YaNan.frame.hibernate.database.DBInterface.mySqlInterface;
import com.YaNan.frame.hibernate.database.annotation.Column;
import com.YaNan.frame.hibernate.database.annotation.Tab;
import com.YaNan.frame.service.Log;
import com.YaNan.frame.stringSupport.StringSupport;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class DBTab implements mySqlInterface {
	private Object object;
	private String name;
	private boolean isMust;
	private String include;
	private String value;
	private String DBName;
	private boolean autoUpdate;
	private Class<?> cls;
	private ClassLoader loader;
	private Map<Field, DBColumn> map = new LinkedHashMap<Field, DBColumn>();
	private Map<String, String> columns = new LinkedHashMap<String, String>();
	private Map<String, ResultSet> session = new HashMap<String, ResultSet>();
	private Field AIField;
	private Field Primary_key;
	private DataBase dataBase;
	public ClassLoader getLoader() {
		return loader;
	}
	private void Clone(DBTab tab) {
		this.dataBase = tab.dataBase;
		this.object = tab.object;
		this.name = tab.name;
		this.isMust = tab.isMust;
		this.include = tab.include;
		this.value = tab.value;
		this.DBName = tab.DBName;
		this.cls = tab.cls;
		this.map = tab.map;
		this.session = tab.session;
		this.AIField = tab.AIField;
		this.Primary_key = tab.Primary_key;
	}

	private void Clone(DBTab tab, Object obj) {
		this.dataBase = tab.dataBase;
		this.object = obj;
		this.name = tab.name;
		this.isMust = tab.isMust;
		this.include = tab.include;
		this.value = tab.value;
		this.DBName = tab.DBName;
		this.cls = tab.cls;
		this.map = tab.map;
		this.session = tab.session;
		this.AIField = tab.AIField;
		this.Primary_key = tab.Primary_key;

	}
	public DBTab(com.YaNan.frame.hibernate.database.entity.Tab tabEntity) throws ClassNotFoundException{
		this.cls = Class.forName(tabEntity.getCLASS());
		this.loader = new ClassLoader(this.cls);
		Tab tab = this.loader.getLoadedClass().getAnnotation(Tab.class);
		if(tab==null){
			Class<?> sCls=this.cls.getSuperclass();
			tab = sCls.getAnnotation(Tab.class); 
			if(tab!=null){
				this.cls = sCls;
				this.loader = new ClassLoader(this.cls);
			}
		}
		if (Class2TabMappingCache.hasTab(this.cls)) {
			this.Clone(Class2TabMappingCache.getDBTab(this.cls));
		} else {
			Log.getSystemLog()
			.info("================================================================================================================");
			Log.getSystemLog().info("the current class：" + this.cls.getSimpleName());
			if (tab != null) {
				this.setDBName(tab.DB().equals("")?tabEntity.getDb():tab.DB());
				this.setInclude(tab.include());
				this.setMust(tab.isMust());
				this.setName(tab.name().equals("") ? this.DBName+"."+this.cls.getSimpleName() : this.DBName+"."+tab
						.name());
				this.setValue(tab.value());
				this.setAutoUpdate(tab.autoUpdate());
			} else {
				Log.getSystemLog().info("not annotion configure,try to set default");
				this.setDBName(tabEntity.getDb()==null?"":tabEntity.getDb());
				this.setInclude("");
				this.setMust(false);
				this.setName(this.cls.getSimpleName());
				this.setValue("");
				this.setAutoUpdate(false);
			}
			this.setMap(this.cls);
			try {
				if(this.DBName!=null&&!this.DBName.equals(""))
				this.dataBase = DBFactory.HasDB(this.DBName)?DBFactory.getDataBase(this.DBName):DBFactory.getDefaultDB();
				Class2TabMappingCache.addTab(this);
				if(this.isMust&&!this.exists()){
					this.create();
				}
			} catch (Exception e) {
				Log.getSystemLog().error("the databaes ["+this.DBName+"] is't init ,please try to init database! at class ["+this.getCls().getName()+"]");
				e.printStackTrace();
			} 
		}
	}

	/**
	 * 默认构造器，需要传入一个Class《？》的class 构造器会默认从DataBase中获得connection
	 * 同时该构造器会对cls进行处理，进行class与Field的映射
	 * 
	 * @param cls
	 */
	public DBTab(Class<?> cls) {
		this.cls = cls;
		this.loader = new ClassLoader(this.cls);
		Tab tab = this.loader.getLoadedClass().getAnnotation(Tab.class);
		if(tab==null){
			Class<?> sCls=this.cls.getSuperclass();
			tab = sCls.getAnnotation(Tab.class); 
			if(tab!=null){
				this.cls = sCls;
				this.loader = new ClassLoader(this.cls);
			}
		}
		if (Class2TabMappingCache.hasTab(this.cls)) {
			this.Clone(Class2TabMappingCache.getDBTab(this.cls));
		} else {
			Log.getSystemLog()
					.info("================================================================================================================");
			Log.getSystemLog().info("the current class：" + cls.getSimpleName());
			if (tab != null) {
				this.setDBName(tab.DB());
				this.setInclude(tab.include());
				this.setMust(tab.isMust());
				this.setName(tab.name().equals("") ? this.DBName+"."+cls.getSimpleName() : this.DBName+"."+tab
						.name());
				this.setValue(tab.value());
				this.setAutoUpdate(tab.autoUpdate());
			} else {
				Log.getSystemLog().info("not annotion configure,try to set default");
				this.setDBName("");
				this.setInclude("");
				this.setMust(false);
				this.setName(cls.getSimpleName());
				this.setValue("");
				this.setAutoUpdate(false);
			}
			this.setMap(cls);
			try {
				if(this.DBName!=null&&!this.DBName.equals(""))
				this.dataBase = DBFactory.HasDB(this.DBName)?DBFactory.getDataBase(this.DBName):DBFactory.getDefaultDB();
				Class2TabMappingCache.addTab(this);
				if(this.isMust&&!this.exists()){
					this.create();
				}
			} catch (Exception e) {
				Log.getSystemLog().error("the databaes ["+this.DBName+"] is't init ,please try to init database! at class ["+this.getCls().getName()+"]");
				e.printStackTrace();
			} 
		}
	}
	public DBTab(Object obj) {
			this.cls = obj.getClass();
			this.object=obj;
			this.loader = new ClassLoader(obj);
			Tab tab = this.loader.getLoadedClass().getAnnotation(Tab.class);
			if(tab==null){
				Class<?> sCls=this.cls.getSuperclass();
				tab = sCls.getAnnotation(Tab.class); 
				if(tab!=null){
					this.cls = sCls;
				}
			}
		//如果表缓存中有当前类得表
		if (Class2TabMappingCache.hasTab(this.cls)) {
			this.Clone(Class2TabMappingCache.getDBTab(this.cls), obj);
			if (tab != null) {
				this.setName(tab.name().equals("") ? this.DBName+"."+this.cls.getSimpleName() : this.DBName+"."+tab
						.name());
				try {
					if(this.isMust&&!this.exists()){
						this.create();
					}
				} catch (Exception e) {
					Log.getSystemLog().error("the databaes ["+this.DBName+"] is't init ,please try to init database! at class ["+this.getCls().getName()+"]");
					e.printStackTrace();
				}
			}
			//如果表缓存中不存在当前表
		} else {
			//重新解析数据表
			Log.getSystemLog()
					.info("================================================================================================================");
			Log.getSystemLog().info("the current class：" + cls.getSimpleName());
			//如果当前类有Tab注解
			if (tab != null) {
				this.setDBName(tab.DB());
				this.setInclude(tab.include());
				this.setMust(tab.isMust());
				this.setName(tab.name().equals("") ? this.DBName+"."+cls.getSimpleName() : this.DBName+"."+tab
						.name());
				this.setValue(tab.value());
				this.setAutoUpdate(tab.autoUpdate());
			} else {
					Log.getSystemLog().info("not annotion configure,try to set default");
					this.setDBName("");
					this.setInclude("");
					this.setMust(false);
					this.setName(cls.getSimpleName());
					this.setValue("");
					this.setAutoUpdate(false);
				}
			this.setMap(cls);
			try {
				if(this.DBName!=null&&!this.DBName.equals(""))
				this.dataBase = DBFactory.HasDB(this.DBName)?DBFactory.getDataBase(this.DBName):DBFactory.getDefaultDB();
				Class2TabMappingCache.addTab(this);
				if(this.isMust&&!this.exists()){
					this.create();
				}
			} catch (Exception e) {
				Log.getSystemLog().error("the databaes ["+this.DBName+"] is't init ,please try to init database! at class ["+this.getCls().getName()+"]");
				e.printStackTrace();
			} 
		}
	}
	public List<Object> query(Connection connection, String sql)
			throws SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException, SecurityException,
			IllegalArgumentException {
		ResultSet rs = this.dataBase.executeQuery(sql);
		List<Object> objects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(cls, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					if (loader.hasMethod(
							ClassLoader.createFieldSetMethod(field),
							field.getType()))
						if(rs.getObject(this.map.get(field).getName())!=null)
						loader.set(field.getName(), field.getType(),
								DefaultDispatcher.castType(rs
										.getObject(this.map.get(field)
												.getName()), field.getType()));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			objects.add(loader.getLoadedObject());
		}
		return objects;
	}

	
	public List<Object> query(Query query,String sql,boolean mapping) {
		List<Object> objects = new ArrayList<Object>();
		try {
			ResultSet rs =this.dataBase.executeQuery(sql);
			while (rs.next()) {
				loader = new ClassLoader(this.cls);
				Iterator<Field> iterator = query.getFieldMap().keySet().iterator();
				while (iterator.hasNext()) {
					Field field = iterator.next();
					try {
						if (loader.hasMethod(
								ClassLoader.createFieldSetMethod(field),
								field.getType())){
							String columnName= this.map.get(field)==null?field.getName():this.map.get(field).getName();
							if(columnName.contains(".")&&mapping)columnName=columnName.substring(columnName.lastIndexOf(".")+1);
							if(rs.getObject(columnName)!=null)
							loader.set(field.getName(), field.getType(),
									DefaultDispatcher.castType(rs
											.getObject(columnName), field.getType()));
						}
					} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | SecurityException | SQLException e) {
						Log.getSystemLog().error(sql);
						e.printStackTrace();
						continue;
					}
				}
				objects.add(loader.getLoadedObject());
			}
		} catch (SQLException e) {
			Log.getSystemLog().error(sql);
			e.printStackTrace();
		}
		return objects;
	}

	public List<Object> query(Query query) throws SQLException,
			InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, IllegalArgumentException {
		ResultSet rs = this.dataBase.executeQuery(query.create());
		List<Object> objects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(cls, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					if (loader.hasMethod(
							ClassLoader.createFieldSetMethod(field),
							field.getType()))
						if(rs.getObject(this.map.get(field).getName())!=null)
						loader.set(field.getName(), field.getType(),
								DefaultDispatcher.castType(rs
										.getObject(this.map.get(field)
												.getName()), field.getType()));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			objects.add(loader.getLoadedObject());
		}
		return objects;
	}
	public List<Object> query(Query query,Connection connection) throws SQLException,
		InstantiationException, IllegalAccessException,
		NoSuchFieldException, SecurityException, IllegalArgumentException {
	ResultSet rs = this.dataBase.executeQuery(query.create(),connection);
	List<Object> objects = new ArrayList<Object>();
	while (rs.next()) {
		loader = new ClassLoader(cls, true);
		Iterator<Field> iterator = this.map.keySet().iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			try {
				if (loader.hasMethod(
						ClassLoader.createFieldSetMethod(field),
						field.getType()))
					if(rs.getObject(this.map.get(field).getName())!=null)
					loader.set(field.getName(), field.getType(),
							DefaultDispatcher.castType(rs
									.getObject(this.map.get(field)
											.getName()), field.getType()));
			} catch (InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		objects.add(loader.getLoadedObject());
	}
	return objects;
	}
	public <T> T query(Query query, Class<T> obj) throws SQLException,
			InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, IllegalArgumentException {
		ResultSet rs = this.dataBase.executeQuery(query.create());
		
		Object object = null;
		while (rs.next()) {
			object = obj.newInstance();
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				field.setAccessible(true);
				field.set(object, rs.getObject(this.map.get(field).getName()));
			}
		}
		return obj.cast(object);
	}

	public boolean exists() throws Exception
	  {
	    String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + this.DBName + "' and TABLE_NAME='" + this.name.substring(!this.name.contains(".") ? 0 : this.name.lastIndexOf(".") + 1, this.name.length()) + "'";
	    	if(this.dataBase==null)
	    		throw new Exception("unknow database exception");
		    ResultSet rs = this.dataBase.executeQuery(sql);
		      return rs.next();
	  }

	public Iterator<Field> iterator() {
		return this.map.keySet().iterator();
	}

	public Map<Field, DBColumn> getDBColumns() {
		return this.map;
	}

	/**
	 * 
	 * @param insert
	 * @param obj
	 * @return
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object insert(Insert insert, Object obj) throws SQLException,
			IllegalArgumentException, IllegalAccessException {
		PreparedStatement ps = this.dataBase.execute(insert.create());
		ResultSet rs = ps.getGeneratedKeys();
		ClassLoader loader = new ClassLoader(obj);
			try {
				if (this.AIField!=null&&loader.hasMethod(ClassLoader.createFieldSetMethod(this.AIField),
						this.AIField.getType()))
					loader.set(
							this.AIField.getName(),
							this.AIField.getType(),
							DefaultDispatcher.castType(rs.getInt(1),
									this.AIField.getType()));
			} catch (InvocationTargetException | NoSuchMethodException e) {
				Log.getSystemLog().exception(e);
			}
		return obj;
	}

	public Object insertOrUpdate(Insert insert) throws Exception {
		if (this.autoUpdate) {
			if (this.AIField == null) {
				throw new Exception(
						"When a primary key is empty does not automatically update table");
			}
			Query query = new Query(insert.getObj());
			query.addField(this.AIField);
			if (query.query().size() == 1) {
				Update update = new Update(insert.getObj());
					update.addCondition(this.AIField.getName(),
							this.AIField.get(insert.getObj()));
				update.update();
				query = new Query(insert.getObj());
				return query.query().get(0);
			} else {
				return insert(insert);
			}
		}
		return insert(insert);
	}

	public Object insert(Insert insert) throws
			IllegalArgumentException, IllegalAccessException {
			try {
				PreparedStatement ps = this.dataBase.execute(insert.create(),Statement.RETURN_GENERATED_KEYS);
				if(ps!=null){
					ResultSet rs = ps.getGeneratedKeys();
					while(rs.next()){
						this.AIField.setAccessible(true);
						if (loader.hasMethod(ClassLoader.createFieldSetMethod(this.AIField),
								this.AIField.getType()))
							loader.set(
									this.AIField.getName(),
									this.AIField.getType(),
								DefaultDispatcher.castType(rs.getInt(1),
										this.AIField.getType()));
					}
				}else{
					return null;
				}
			} catch (InvocationTargetException | NoSuchMethodException | SQLException | SecurityException e) {
				Log.getSystemLog().error(insert.create());;
				Log.getSystemLog().exception(e);
			}
		return this.loader.getLoadedObject();
	}
	public Object insert(Insert insert,Connection connection) throws
		IllegalArgumentException, IllegalAccessException, SecurityException, SQLException {
	try {
		PreparedStatement ps = this.dataBase.execute(insert.create(),connection,Statement.RETURN_GENERATED_KEYS);
		if(this.AIField!=null){
			ResultSet rs = ps.getGeneratedKeys();
			while(rs.next()){
				this.AIField.setAccessible(true);
				if (loader.hasMethod(ClassLoader.createFieldSetMethod(this.AIField),
						this.AIField.getType()))
					loader.set(
							this.AIField.getName(),
							this.AIField.getType(),
						DefaultDispatcher.castType(rs.getInt(1),
								this.AIField.getType()));
			}
		}
	} catch (InvocationTargetException | NoSuchMethodException  e) {
		Log.getSystemLog().error(insert.create());;
		Log.getSystemLog().exception(e);
	}
	return this.loader.getLoadedObject();
	}
	public int update(Update update) {
		String sql = update.create();
		sql = FilterSql(sql);
		int start = 7;
		int end = sql.indexOf(" ", 7);
		String sub = sql.substring(start, end);
		sql = sql.replaceFirst(sub, this.name);
		return this.dataBase.executeUpdate(sql);
	}
	
	public int update(Update update,Connection connection) throws SQLException  {
		String sql = update.create();
		sql = FilterSql(sql);
		int start = 7;
		int end = sql.indexOf(" ", 7);
		String sub = sql.substring(start, end);
		sql = sql.replaceFirst(sub, this.name);
		return this.dataBase.executeUpdate(sql,connection);
	}
	
	private String FilterSql(String sql) {

		return sql.replace("\\", "/");
	}

	public int create(Create create) {
		return this.dataBase.executeUpdate(create.create());
	
	}

	public boolean create(){
		return create(new Create(this))>0;
	}

	public int delete(Delete delete) {
		return this.dataBase.executeUpdate(delete.create());
	
	}
	public boolean delete(Delete delete,Connection connection) throws SQLException {
			this.dataBase.executeUpdate(delete.create(),connection);
			return true;
	}

	public boolean delete() {
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (this.object != null) {
			name = StringSupport.decodeVar(name, this.object);
		}
		Log.getSystemLog().info("build the DataTab Mapping:" + name);
		this.name = name;
	}

	public boolean isMust() {
		return isMust;
	}

	public void setMust(boolean isMust) {
		Log.getSystemLog().info("create the tab when the tab is not exists:" + isMust);
		this.isMust = isMust;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		Log.getSystemLog().info("contain orthers xml file" + include);
		this.include = include;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (this.object != null) {
			value = StringSupport.decodeVar(value, this.object);
		}
		Log.getSystemLog().info("set value：" + value);
		this.value = value;
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		if (this.object != null) {
			dBName = StringSupport.decodeVar(dBName, this.object);
		}
		Log.getSystemLog().info("set the data base name:" + dBName);
		DBName = dBName;
	}

	public Map<Field, DBColumn> getFieldMap() {
		return map;
	}

	/**
	 * getDBColumn
	 * 
	 * @param field
	 * @return
	 */
	public DBColumn getDBColumn(Field field) {
		if (this.map.containsKey(field))
			return this.map.get(field);
		Log.getSystemLog()
				.info("--------------------------------------------------------------------------------------------------");
		Log.getSystemLog().info("current field:" + field.getName());
		Column column = field.getAnnotation(Column.class);
		if (column != null && column.ignore() == true) {
			Log.getSystemLog().info(field.getName() + " ignore,jump decode the field!");
			return null;
		}
		if (column != null){
			if(column.Auto_Increment()){
				this.AIField=field;
				this.Primary_key = field;
			}else if(column.Primary_Key())
				this.Primary_key=field;
			DBColumn db = new DBColumn(field, column);
			this.map.put(field, db);
			return db;
		}
		DBColumn db = new DBColumn(field);
		this.map.put(field, db);
		return db;
	}

	public DBColumn getDBColumn(String field) throws NoSuchFieldException,
			SecurityException {
		Field f = this.cls.getDeclaredField(field);
		return getDBColumn(f);
	}

	private void setMap(Class<?> cls) {
		DBColumn dbColumn;
		for (Field field : cls.getDeclaredFields()) {
			dbColumn = getDBColumn(field);
			if (dbColumn != null) {
				this.map.put(field, dbColumn);
			}
		}
	}
	
	public void setMap(Map<Field, DBColumn> map) {
		this.map = map;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void addColumn(String column, String type) {
		this.columns.put(column, type);
	}

	public void addColumn(Map<String, String> columns) {
		Iterator<String> i = columns.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			this.columns.put(key, columns.get(key));
		}

	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	@Override
	public String toString() {
		return "DBTab [object=" + object
				+ ", name=" + name + ", isMust=" + isMust + ", include="
				+ include + ", value=" + value + ", DBName=" + DBName
				+ ", autoUpdate=" + autoUpdate + ", cls=" + cls + ", loader="
				+ loader + ", map=" + map + ", columns=" + columns
				+ ", session=" + session + ", AIField=" + AIField + "]";
	}

	public List<Object> showTab() {
		String sql = "SELECT * FROM " + this.name;
		List<Object> objs = new ArrayList<Object>();
		try {
			ResultSet rs =this.dataBase.executeQuery(sql);
			while (rs.next()) {
				Object obj = this.cls.newInstance();
				Iterator<Field> i = this.iterator();
				while (i.hasNext()) {
					Field f = i.next();
					f.setAccessible(true);
					try {
						f.set(obj, rs.getObject(this.map.get(f).getName()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						f.set(obj, Boolean.parseBoolean((String) rs
								.getObject(this.map.get(f).getName())));
					}
				}
				objs.add(obj);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objs;
	}
	public DataBase getDataBase() {
		return dataBase;
	}
	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}
	public Field getAIField() {
		return AIField;
	}
	public void setAIField(Field aIField) {
		AIField = aIField;
	}
	public Field getPrimary_key() {
		return Primary_key;
	}
	public void setPrimary_key(Field primary_key) {
		Primary_key = primary_key;
	}

}
