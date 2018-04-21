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

import com.YaNan.frame.hibernate.database.DBInterface.mySqlInterface;
import com.YaNan.frame.hibernate.database.annotation.Column;
import com.YaNan.frame.hibernate.database.annotation.Tab;
import com.YaNan.frame.hibernate.database.cache.Class2TabMappingCache;
import com.YaNan.frame.hibernate.database.cache.QueryCache;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.util.StringUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class DBTab implements mySqlInterface {
	private Field AIField;
	private boolean autoUpdate;
	private String charset;
	private Class<?> cls;
	private String collate;
	private Map<String, String> columns = new LinkedHashMap<String, String>();
	private DataBase dataBase;
	private String DBName;
	private String include;
	private boolean isMust;
	private ClassLoader loader;
	private Map<Field, DBColumn> map = new LinkedHashMap<Field, DBColumn>();
	private String name;
	private Object object;
	private Field Primary_key;
	private Map<String, ResultSet> session = new HashMap<String, ResultSet>();
	private String value;
	private boolean exist;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class,DBTab.class);

	/**
	 * 默认构造器，需要传入一个Class《？》的class 构造器会默认从DataBase中获得connection
	 * 同时该构造器会对cls进行处理，进行class与Field的映射
	 * 
	 * @param cls
	 */
	public DBTab(Class<?> cls) {
		this(new ClassLoader(cls).getLoadedObject());
	}

	public DBTab(com.YaNan.frame.hibernate.database.entity.Tab tabEntity)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this(new ClassLoader(tabEntity.getCLASS()).getLoadedClass());
		
	}

	public DBTab(Object obj) {
		this.cls = obj.getClass();
		this.object = obj;
		this.loader = new ClassLoader(obj);
		Tab tab = this.loader.getLoadedClass().getAnnotation(Tab.class);
		if (tab == null) {
			Class<?> sCls = this.cls.getSuperclass();
			tab = sCls.getAnnotation(Tab.class);
			if (tab != null) {
				this.cls = sCls;
			}
		}
		// 如果表缓存中有当前类得表
		if (Class2TabMappingCache.hasTab(this.cls)) {
			this.Clone(Class2TabMappingCache.getDBTab(this.cls), obj);
			if (tab != null) {
				this.setName(tab.name().equals("") ? this.DBName + "." + this.cls.getSimpleName()
						: this.DBName + "." + tab.name());
				try {
					if (!this.exist&&!this.DBName.equals("") && this.isMust) {
						if(this.exists())
							this.exist=true;
						else if(this.create())
							this.exist=true;
					}
				} catch (Exception e) {
					log.error("the databaes [" + this.DBName
							+ "] is't init ,please try to init database! at class [" + this.getCls().getName() + "]");
					e.printStackTrace();
				}
			}
			// 如果表缓存中不存在当前表
		} else {
			// 重新解析数据表
			log.debug(
					"================================================================================================================");
			log.debug("the current class：" + cls.getSimpleName());
			// 如果当前类有Tab注解
			if (tab != null) {
				this.setDBName(tab.DB());
				this.setInclude(tab.include());
				this.setMust(tab.isMust());
				this.setName(tab.name().equals("") ? this.DBName + "." + cls.getSimpleName()
						: this.DBName + "." + tab.name());
				this.setValue(tab.value());
				this.setAutoUpdate(tab.autoUpdate());
				if (!tab.charset().equals(""))
					this.setCharset(tab.charset());
				if (!tab.collate().equals(""))
					this.setCollate(tab.collate());
			} else {
				log.debug("not annotion configure,try to set default");
				this.setDBName("");
				this.setInclude("");
				this.setMust(false);
				this.setName(cls.getSimpleName());
				this.setValue("");
				this.setAutoUpdate(false);
			}
			this.setMap(cls);
			try {
				if (this.DBName != null && !this.DBName.equals("")){
					this.dataBase = DBFactory.HasDB(this.DBName) ? DBFactory.getDataBase(this.DBName)
							: DBFactory.getDefaultDB();
					this.dataBase.addTab(this);
				}
				Class2TabMappingCache.addTab(this);
				if (!this.exist&&!this.DBName.equals("") && this.isMust) {
					if(this.exists())
						this.exist=true;
					else if(this.create())
						this.exist=true;
				}
			} catch (Exception e) {
				log.error("the databaes [" + this.DBName
						+ "] is't init ,please try to init database! at class [" + this.getCls().getName() + "]");
				e.printStackTrace();
			}
		}
	}

	public void addColumn(Map<String, String> columns) {
		Iterator<String> i = columns.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			this.columns.put(key, columns.get(key));
		}

	}

	public void addColumn(String column, String type) {
		this.columns.put(column, type);
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
		this.charset = tab.charset;
		this.collate = tab.collate;
		this.exist = tab.exist;

	}

	public boolean create() {
		return create(new Create(this)) > 0;
	}

	public int create(Create create) {
		return this.dataBase.executeUpdate(create.create());

	}

	public int delete() {
		return this.dataBase.executeUpdate("DROP TABLE "+this.getName());
	}

	public int delete(Delete delete) {
		QueryCache.getCache().cleanCache(this.getName());
		return this.dataBase.executeUpdate(delete.create());
	}

	public boolean delete(Delete delete, Connection connection) throws SQLException {
		this.dataBase.executeUpdate(delete.create(), connection);
		QueryCache.getCache().cleanCache(this.getName());
		return true;
	}

	public boolean exists() throws Exception {
		String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + this.DBName
				+ "' AND TABLE_NAME='"
				+ this.name.substring(!this.name.contains(".") ? 0 : this.name.lastIndexOf(".") + 1, this.name.length())
				+ "'";
		if (this.dataBase == null)
			throw new Exception("unknow database exception");
		ResultSet rs = this.dataBase.executeQuery(sql);
		return rs.next();
	}

	private String FilterSql(String sql) {

		return sql.replace("\\", "/");
	}

	public Field getAIField() {
		return AIField;
	}

	public String getCharset() {
		return charset;
	}

	public Class<?> getCls() {
		return cls;
	}

	public String getCollate() {
		return collate;
	}

	public Map<String, String> getColumns() {
		return columns;
	}

	public DataBase getDataBase() {
		return dataBase;
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
		log.debug(
				"--------------------------------------------------------------------------------------------------");
		log.debug("current field:" + field.getName());
		Column column = field.getAnnotation(Column.class);
		if (column != null && column.ignore() == true) {
			log.debug(field.getName() + " ignore,jump decode the field!");
			return null;
		}
		if (column != null) {
			if (column.Auto_Increment()) {
				this.AIField = field;
				this.Primary_key = field;
			} else if (column.Primary_Key())
				this.Primary_key = field;
			DBColumn db = new DBColumn(field, column);
			this.map.put(field, db);
			return db;
		}
		DBColumn db = new DBColumn(field);
		this.map.put(field, db);
		return db;
	}

	public DBColumn getDBColumn(String field) throws NoSuchFieldException, SecurityException {
		Field f = this.cls.getDeclaredField(field);
		return getDBColumn(f);
	}

	public Object insert(Insert insert) throws IllegalArgumentException, IllegalAccessException {
		try {
			PreparedStatement ps = this.dataBase.execute(insert.create(), java.sql.Statement.RETURN_GENERATED_KEYS);
			if (ps != null) {
				ResultSet rs = ps.getGeneratedKeys();
				if (this.AIField != null && rs.next())
					this.setField(loader, this.AIField, rs.getInt(1));
			} else {
				return null;
			}
			QueryCache.getCache().cleanCache(this.getName());
		} catch (InvocationTargetException | NoSuchMethodException | SQLException | SecurityException e) {
			log.error(e);
		}
		return this.loader.getLoadedObject();
	}

	public Object insert(Insert insert, Connection connection)
			throws IllegalArgumentException, IllegalAccessException, SecurityException, SQLException {
		try {
			PreparedStatement ps = this.dataBase.execute(insert.create(), connection,
					java.sql.Statement.RETURN_GENERATED_KEYS);
			if (this.AIField != null) {
				ResultSet rs = ps.getGeneratedKeys();
				if (this.AIField != null && rs.next())
					this.setField(loader, this.AIField, rs.getInt(1));
			}
			QueryCache.getCache().cleanCache(this.getName());
		} catch (InvocationTargetException | NoSuchMethodException e) {
			log.error(e);
		}
		return this.loader.getLoadedObject();
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
	public Object insert(Insert insert, Object obj)
			throws SQLException, IllegalArgumentException, IllegalAccessException {
		PreparedStatement ps = this.dataBase.execute(insert.create());
		QueryCache.getCache().cleanCache(this.getName());
		ResultSet rs = ps.getGeneratedKeys();
		ClassLoader loader = new ClassLoader(obj);
		try {
			if (this.AIField != null && rs.next())
				this.setField(loader, this.AIField, rs.getInt(1));
		} catch (InvocationTargetException | NoSuchMethodException e) {
			log.error(e);
		}
		return obj;
	}

	public Object insertOrUpdate(Insert insert) throws Exception {
		if (this.autoUpdate) {
			if (this.AIField == null) {
				throw new Exception("When a primary key is empty does not automatically update table");
			}
			Query query = new Query(insert.getObj());
			query.addField(this.AIField);
			if (query.query().size() == 1) {
				Update update = new Update(insert.getObj());
				update.addCondition(this.AIField.getName(), this.AIField.get(insert.getObj()));
				update.update();
				query = new Query(insert.getObj());
				return query.query().get(0);
			} else {
				return insert(insert);
			}
		}
		return insert(insert);
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public boolean isMust() {
		return isMust;
	}

	public Iterator<Field> iterator() {
		return this.map.keySet().iterator();
	}

	public List<Object> query(Connection connection, String sql) throws SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
		ResultSet rs = this.dataBase.executeQuery(sql);
		List<Object> objects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(cls, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					String columnName = this.map.get(field) == null ? field.getName() : this.map.get(field).getName();
					if (columnName.contains("."))
						columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
					if (rs.getObject(columnName) != null)
						this.setField(loader, field, rs.getObject(columnName));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
					continue;
				}
			}
			objects.add(loader.getLoadedObject());
		}
		return objects;
	}

	public List<Object> query(Query query) throws SQLException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, IllegalArgumentException {
		return this.query(query, false);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Query query, boolean mapping) {
		String sql = query.create();
		List<T> objects = QueryCache.getCache().getQuery(sql);
		if(objects!=null)
			return objects;
		objects = new ArrayList<T>();
		try {
			ResultSet rs = this.dataBase.executeQuery(sql);
			while (rs.next()) {
				loader = new ClassLoader(this.cls);
				Iterator<Field> iterator = query.getFieldMap().keySet().iterator();
				while (iterator.hasNext()) {
					Field field = iterator.next();
					try {
						String columnName = this.map.get(field) == null ? field.getName()
								: this.map.get(field).getName();
						if (columnName.contains(".") && mapping)
							columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
						if (rs.getObject(columnName) != null)
							this.setField(loader, field, rs.getObject(columnName));
					} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException
							| IllegalArgumentException | SecurityException | SQLException e) {
						log.error(e);
						e.printStackTrace();
						continue;
					}
				}
				objects.add((T) loader.getLoadedObject());
			}
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
		QueryCache.getCache().addQuery(this.getName(),sql, objects);
		return objects;
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> query(String sql ) {
		List<T> objects = QueryCache.getCache().getQuery(sql);
		if(objects!=null)
			return objects;
		objects = new ArrayList<T>();
		try {
			ResultSet rs = this.dataBase.executeQuery(sql);
			while (rs.next()) {
				loader = new ClassLoader(this.cls);
				Iterator<Field> iterator = this.map.keySet().iterator();
				while (iterator.hasNext()) {
					Field field = iterator.next();
					try {
						String columnName = this.map.get(field) == null ? field.getName()
								: this.map.get(field).getName();
						if (columnName.contains("."))
							columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
						if (rs.getObject(columnName) != null)
							this.setField(loader, field, rs.getObject(columnName));
					} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException
							| IllegalArgumentException | SecurityException | SQLException e) {
						log.error(e);
						e.printStackTrace();
						continue;
					}
				}
				objects.add((T) loader.getLoadedObject());
			}
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
		QueryCache.getCache().addQuery(this.getName(),sql, objects);
		return objects;
	}

	public List<Object> query(Query query, Connection connection) throws SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
		ResultSet rs = this.dataBase.executeQuery(query.create(), connection);
		List<Object> objects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(cls, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					String columnName = this.map.get(field) == null ? field.getName() : this.map.get(field).getName();
					if (columnName.contains("."))
						columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
					if (rs.getObject(columnName) != null)
						this.setField(loader, field, rs.getObject(columnName));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			objects.add(loader.getLoadedObject());
		}
		return objects;
	}
	public void setField(ClassLoader loader, Field field, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		field.setAccessible(true);
		if (loader.hasMethod(ClassLoader.createFieldSetMethod(field), field.getType()))
			loader.set(field.getName(), field.getType(), ClassLoader.castType(value, field.getType()));
		else{
			field.setAccessible(true);
			field.set(loader.getLoadedObject(),ClassLoader.castType(value, field.getType()));
		}
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

	public List<Object> showTab() {
		String sql = "SELECT * FROM " + this.name;
		List<Object> objs = new ArrayList<Object>();
		try {
			ResultSet rs = this.dataBase.executeQuery(sql);
			while (rs.next()) {
				Object obj = this.cls.newInstance();
				Iterator<Field> i = this.iterator();
				while (i.hasNext()) {
					Field f = i.next();
					f.setAccessible(true);
					try {
						f.set(obj, rs.getObject(this.map.get(f).getName()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						f.set(obj, Boolean.parseBoolean((String) rs.getObject(this.map.get(f).getName())));
					}
				}
				objs.add(obj);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objs;
	}

	@Override
	public String toString() {
		return "DBTab [object=" + object + ", name=" + name + ", isMust=" + isMust + ", include=" + include + ", value="
				+ value + ", DBName=" + DBName + ", autoUpdate=" + autoUpdate + ", cls=" + cls + ", loader=" + loader
				+ ", map=" + map + ", columns=" + columns + ", session=" + session + ", AIField=" + AIField + "]";
	}

	public int update(Update update) {
		String sql = update.create();
		sql = FilterSql(sql);
		int start = 7;
		int end = sql.indexOf(" ", 7);
		String sub = sql.substring(start, end);
		sql = sql.replaceFirst(sub, this.name);
		QueryCache.getCache().cleanCache(this.getName());
		return this.dataBase.executeUpdate(sql);
	}

	public int update(Update update, Connection connection) throws SQLException {
		String sql = update.create();
		sql = FilterSql(sql);
		int start = 7;
		int end = sql.indexOf(" ", 7);
		String sub = sql.substring(start, end);
		sql = sql.replaceFirst(sub, this.name);
		QueryCache.getCache().cleanCache(this.getName());
		return this.dataBase.executeUpdate(sql, connection);
	}
	

	public void setInclude(String include) {
		log.debug("contain orthers xml file" + include);
		this.include = include;
	}

	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public void setMap(Map<Field, DBColumn> map) {
		this.map = map;
	}

	public void setAIField(Field aIField) {
		AIField = aIField;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public void setCollate(String collate) {
		this.collate = collate;
	}

	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}

	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}

	public void setDBName(String dBName) {
		if (this.object != null) {
			dBName = StringUtil.decodeVar(dBName, this.object);
		}
		DBName = dBName;
	}


	public void setMust(boolean isMust) {
		this.isMust = isMust;
	}

	public void setName(String name) {
		if (this.object != null) {
			name = StringUtil.decodeVar(name, this.object);
		}
		this.name = name;
	}

	public void setPrimary_key(Field primary_key) {
		Primary_key = primary_key;
	}

	public void setValue(String value) {
		if (this.object != null) {
			value = StringUtil.decodeVar(value, this.object);
		}
		this.value = value;
	}

	public Map<Field, DBColumn> getDBColumns() {
		return this.map;
	}

	public String getDBName() {
		return DBName;
	}

	public Map<Field, DBColumn> getFieldMap() {
		return map;
	}

	public String getInclude() {
		return include;
	}

	public ClassLoader getLoader() {
		return loader;
	}

	public String getName() {
		return name;
	}

	public Field getPrimary_key() {
		return Primary_key;
	}

	public String getValue() {
		return value;
	}
}
