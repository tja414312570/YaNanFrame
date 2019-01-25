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
	private Class<?> dataTablesClass;
	private String collate;
	private Map<String, String> columns = new LinkedHashMap<String, String>();
	private DataBase dataBase;
	private String DBName;
	private String include;
	private boolean isMust;
	private ClassLoader loader;
	private Map<Field, DBColumn> map = new LinkedHashMap<Field, DBColumn>();
	private Map<String, DBColumn> nameMap = new HashMap<String, DBColumn>();
	private String name;
	private Object dataTablesObject;
	private Field Primary_key;
	private Map<String, ResultSet> session = new HashMap<String, ResultSet>();
	private String value;
	private boolean exist;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, DBTab.class);
	private String[] columnsArray;

	public Object getDataTablesObject() {
		return dataTablesObject;
	}

	public void setDataTablesObject(Object dataTablesObject) {
		this.dataTablesObject = dataTablesObject;
	}

	/**
	 * 默认构造器，需要传入一个Class《？》的class 构造器会默认从DataBase中获得connection
	 * 同时该构造器会对dataTablesClass进行处理，进行class与Field的映射
	 * 
	 * @param dataTablesClass
	 */
	public DBTab(Class<?> dataTablesClass) {
		this(new ClassLoader(dataTablesClass).getLoadedObject());
	}

	public DBTab(com.YaNan.frame.hibernate.database.entity.Tab tabEntity)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this(new ClassLoader(tabEntity.getCLASS()).getLoadedClass());

	}

	public DBTab(Object obj) {
		this.dataTablesClass = obj.getClass();
		this.dataTablesObject = obj;
		this.loader = new ClassLoader(obj);
		Tab tab = this.loader.getLoadedClass().getAnnotation(Tab.class);
		if (tab == null) {
			Class<?> sCls = this.dataTablesClass.getSuperclass();
			tab = sCls.getAnnotation(Tab.class);
			if (tab != null) {
				this.dataTablesClass = sCls;
			}
		}
		// 如果表缓存中有当前类得表
		if (Class2TabMappingCache.hasTab(this.dataTablesClass)) {
			this.Clone(Class2TabMappingCache.getDBTab(this.dataTablesClass), obj);
			if (tab != null) {
				this.setName(tab.name().equals("") ? this.DBName + "." + this.dataTablesClass.getSimpleName()
						: this.DBName + "." + tab.name());
				try {
					if (!this.exist && !this.DBName.equals("") && this.isMust) {
						if (this.exists())
							this.exist = true;
						else if (this.create())
							this.exist = true;
					}
				} catch (Exception e) {
					log.error("the databaes [" + this.DBName + "] is't init ,please try to init database! at class ["
							+ this.dataTablesClass.getName() + "]");
					e.printStackTrace();
				}
			}
			// 如果表缓存中不存在当前表
		} else {
			// 重新解析数据表
			log.debug(
					"================================================================================================================");
			log.debug("the current class：" + dataTablesClass.getSimpleName());
			// 如果当前类有Tab注解
			if (tab != null) {
				this.setDBName(tab.DB());
				this.setInclude(tab.include());
				this.setMust(tab.isMust());
				this.setName(tab.name().equals("") ? this.DBName + "." + dataTablesClass.getSimpleName()
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
				this.setName(dataTablesClass.getSimpleName());
				this.setValue("");
				this.setAutoUpdate(false);
			}
			this.setMap(dataTablesClass);
			try {
				if (this.DBName != null && !this.DBName.equals("")) {
					this.dataBase = DBFactory.HasDB(this.DBName) ? DBFactory.getDataBase(this.DBName)
							: DBFactory.getDefaultDB();
					this.dataBase.addTab(this);
				}
				Class2TabMappingCache.addTab(this);
				if (!this.exist && !this.DBName.equals("") && this.isMust) {
					if (this.exists())
						this.exist = true;
					else if (this.create())
						this.exist = true;
				}
			} catch (Exception e) {
				log.error("the databaes [" + this.DBName + "] is't init ,please try to init database! at class ["
						+ this.dataTablesClass.getName() + "]");
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
		this.dataTablesObject = obj;
		this.name = tab.name;
		this.isMust = tab.isMust;
		this.include = tab.include;
		this.value = tab.value;
		this.DBName = tab.DBName;
		this.dataTablesClass = tab.dataTablesClass;
		this.map = tab.map;
		this.session = tab.session;
		this.AIField = tab.AIField;
		this.Primary_key = tab.Primary_key;
		this.charset = tab.charset;
		this.collate = tab.collate;
		this.exist = tab.exist;
		this.nameMap = tab.nameMap;
		this.columnsArray = tab.columnsArray;
	}

	public boolean create() {
		return create(new Create(this)) > 0;
	}

	public int create(Create create) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		return this.dataBase.executeUpdate(create.create());

	}

	public int delete() {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		return this.dataBase.executeUpdate("DROP TABLE " + this.getName());
	}

	public int delete(Delete delete) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		PreparedStatement ps  = null;
		try {
			ps= this.dataBase.execute(delete.create());
			if (ps != null) {
				this.preparedParameter(ps,delete.getParameters());
				ps.execute();
				QueryCache.getCache().cleanCache(this.getName());// 清理查询缓存
			}
			return ps.executeUpdate();
		} catch (SQLException | SecurityException e) {
			log.error("error to execute sql:" + delete.create());
			log.error("parameter:" + delete.getParameters());
			log.error(e);
		}finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("error to close preparesStatement at sql:" + delete.create());
					log.error("parameter:" + delete.getParameters());
					log.error(e);
				}
		}
		return 0;
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
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		PreparedStatement ps = this.dataBase.executeQuery(sql);
		ResultSet rs = ps.executeQuery();
		boolean exist = rs.next();
		rs.close();
		ps.close();
		return exist;
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

	public Class<?> getDataTablesClass() {
		return dataTablesClass;
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
		log.debug("--------------------------------------------------------------------------------------------------");
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
			this.nameMap.put(db.getName(), db);
			return db;
		}
		DBColumn db = new DBColumn(field);
		this.map.put(field, db);
		this.nameMap.put(db.getName(), db);
		return db;
	}

	public DBColumn getDBColumn(String field) {
		Field f = this.loader.getDeclaredField(field);
		if (f == null)
			throw new RuntimeException("could not find field "+field+" at class" + this.dataTablesClass.getName());
		return getDBColumn(f);
	}
	public DBColumn getDBColumnByColumn(String column){
		return this.nameMap.get(column);
	}

	public int insert(Insert insert) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		int gk = -1;
		try {
			PreparedStatement ps = this.dataBase.execute(insert.create(), java.sql.Statement.RETURN_GENERATED_KEYS);
			if (ps != null) {
				this.preparedParameter(ps,insert.getParameters());
				ps.execute();
				QueryCache.getCache().cleanCache(this.getName());// 清理查询缓存
				ResultSet rs = ps.getGeneratedKeys();
				if (this.AIField != null && rs.next())
					gk = rs.getInt(1);
				else
					gk = 0;
				rs.close();
				ps.close();
			}
		} catch (SQLException | SecurityException e) {
			log.error("error to execute sql:" + insert.create());
			log.error("parameter:" + insert.getParameters());
			log.error(e);
		}
		return gk;
	}
	
	public Object batchInsert(BatchInsert insert,boolean large) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		Object executeResult = null;
		try {
			PreparedStatement ps = this.dataBase.execute(insert.create(), java.sql.Statement.RETURN_GENERATED_KEYS);
			if (ps != null) {
				this.preparedBatchParameter(ps,insert.getParameters(),insert.getColumns().size());
				if(!large)
					executeResult = ps.executeBatch();
				else
					executeResult = ps.executeLargeBatch();
				QueryCache.getCache().cleanCache(this.getName());// 清理查询缓存
				ps.close();
			}
		} catch (SQLException | SecurityException e) {
			log.error("error to execute sql:" + insert.create());
			log.error("parameter:" + insert.getParameters());
			log.error(e);
		}
		return executeResult;
	}
	/**
	 * 尊卑参数
	 * @param preparedStatement
	 * @param parameters
	 * @param columnNum 
	 * @throws SQLException
	 */
	private void preparedBatchParameter(PreparedStatement preparedStatement, List<Object> parameters, int columnNum) throws SQLException {
		Iterator<Object> iterator = parameters.iterator();
		while (iterator.hasNext()){
			Object[] columnValues = (Object[]) iterator.next();
			for(int j = 0;j < (columnNum<columnValues.length?columnNum:columnValues.length);j++)
				preparedStatement.setObject(j+1, j<columnValues.length?columnValues[j]:null);
			preparedStatement.addBatch();
		}
	}
	/**
	 * 准备参数
	 * @param preparedStatement
	 * @param parameters
	 * @throws SQLException
	 */
	private void preparedParameter(PreparedStatement preparedStatement,List<Object> parameters) throws SQLException {
		Iterator<Object> iterator = parameters.iterator();
		int i = 0;
		while (iterator.hasNext())
			preparedStatement.setObject(++i, iterator.next());
	}

	public int insert(Insert insert, Connection connection)
			throws IllegalArgumentException, IllegalAccessException, SecurityException, SQLException {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		int gk = -1;
		PreparedStatement ps = this.dataBase.execute(insert.create(), connection,
				java.sql.Statement.RETURN_GENERATED_KEYS);
		if (ps != null) {
			this.preparedParameter(ps,insert.getParameters());
			ps.execute();
			QueryCache.getCache().cleanCache(this.getName());// 清理查询缓存
			ResultSet rs = ps.getGeneratedKeys();
			if (this.AIField != null && rs.next())
				gk = rs.getInt(1);
			else
				gk = 0;
			rs.close();
			ps.close();
		}
		return gk;
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
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		PreparedStatement ps = this.dataBase.execute(insert.create());
		QueryCache.getCache().cleanCache(this.getName());
		ResultSet rs = ps.getGeneratedKeys();
		ClassLoader loader = new ClassLoader(obj);
		try {
			if (this.AIField != null && rs.next())
				loader.set(AIField, rs.getInt(1));
		} catch (InvocationTargetException | NoSuchMethodException e) {
			log.error(e);
		}
		rs.close();
		ps.close();
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
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		PreparedStatement ps = this.dataBase.executeQuery(sql);
		ResultSet rs = ps.executeQuery();
		List<Object> dataTablesObjects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(dataTablesClass, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					String columnName = this.map.get(field) == null ? field.getName() : this.map.get(field).getName();
					if (columnName.contains("."))
						columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
					if (rs.getObject(columnName) != null)
						loader.set(field, rs.getObject(columnName));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
					continue;
				}
			}
			dataTablesObjects.add(loader.getLoadedObject());
		}
		rs.close();
		ps.close();
		return dataTablesObjects;
	}

	public List<Object> query(Query query) throws SQLException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, IllegalArgumentException {
		return this.query(query, false);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Query query, boolean mapping) {
		if (this.dataBase == null){
			Query subQuery = query.getSubQuery();
			while(subQuery!=null&&(this.dataBase=subQuery.getDbTab().getDataBase())==null)
				subQuery = subQuery.getSubQuery();
		}
		if (this.dataBase == null)
				throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
						+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		String sql = query.create();
		List<T> dataTablesObjects = QueryCache.getCache().getQuery(sql);
		if (dataTablesObjects != null)
			return dataTablesObjects;
		dataTablesObjects = new ArrayList<T>();
		try {
			PreparedStatement ps = this.dataBase.executeQuery(sql);
			preparedParameter(ps, query.getParameters());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				loader = new ClassLoader(this.dataTablesClass);
				Iterator<Field> iterator = query.getFieldMap().keySet().iterator();
				while (iterator.hasNext()) {
					Field field = iterator.next();
					try {
						String columnName = this.map.get(field) == null ? field.getName()
								: this.map.get(field).getName();
						if (columnName.contains(".") && mapping)
							columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
						if (rs!=null&&rs.getObject(columnName) != null)
							loader.set(field, rs.getObject(columnName));
					} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException
							| IllegalArgumentException | SecurityException | SQLException e) {
						log.error("sql:" + query.create());
						log.error(e);
						continue;
					}
				}
				dataTablesObjects.add((T) loader.getLoadedObject());
			}
			rs.close();
			ps.close();
			QueryCache.getCache().addQuery(this.getName(), sql, dataTablesObjects);
		} catch (SQLException e) {
			log.error("sql:" + query.create());
			log.error(e);
		}
		return dataTablesObjects;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String sql) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		List<T> dataTablesObjects = QueryCache.getCache().getQuery(sql);
		if (dataTablesObjects != null)
			return dataTablesObjects;
		dataTablesObjects = new ArrayList<T>();
		try {
			PreparedStatement ps = this.dataBase.executeQuery(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				loader = new ClassLoader(this.dataTablesClass);
				Iterator<Field> iterator = this.map.keySet().iterator();
				while (iterator.hasNext()) {
					Field field = iterator.next();
					try {
						String columnName = this.map.get(field) == null ? field.getName()
								: this.map.get(field).getName();
						if (columnName.contains("."))
							columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
						if (rs.getObject(columnName) != null)
							loader.set(field, rs.getObject(columnName));
					} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException
							| IllegalArgumentException | SecurityException | SQLException e) {
						log.error("sql:" + sql);
						log.error(e);
						continue;
					}
				}
				dataTablesObjects.add((T) loader.getLoadedObject());
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("sql:" + sql);
			log.error(e);
		}
		QueryCache.getCache().addQuery(this.getName(), sql, dataTablesObjects);
		return dataTablesObjects;
	}

	public List<Object> query(Query query, Connection connection) throws SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		PreparedStatement ps = (PreparedStatement) connection
				.prepareStatement(query.create());
		preparedParameter(ps, query.getParameters());
		ResultSet rs = ps.executeQuery();
		List<Object> dataTablesObjects = new ArrayList<Object>();
		while (rs.next()) {
			loader = new ClassLoader(dataTablesClass, true);
			Iterator<Field> iterator = this.map.keySet().iterator();
			while (iterator.hasNext()) {
				Field field = iterator.next();
				try {
					String columnName = this.map.get(field) == null ? field.getName() : this.map.get(field).getName();
					if (columnName.contains("."))
						columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
					if (rs.getObject(columnName) != null)
						loader.set(field, rs.getObject(columnName));
				} catch (InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			dataTablesObjects.add(loader.getLoadedObject());
		}
		rs.close();
		return dataTablesObjects;
	}

	private void setMap(Class<?> dataTablesClass) {
		DBColumn dbColumn;
		for (Field field : dataTablesClass.getDeclaredFields()) {
			dbColumn = getDBColumn(field);
			if (dbColumn != null) {
				this.map.put(field, dbColumn);
			}
		}
		this.columnsArray = new String[this.map.size()];
		Iterator<DBColumn> iterator = this.map.values().iterator();
		int i = 0;
		while(iterator.hasNext()){
			this.columnsArray[i++] = iterator.next().getName();
		}
	}
	public String[] getColumnNameArray(){
		return this.columnsArray;
	}
	public List<Object> showTab() {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		String sql = "SELECT * FROM " + this.name;
		List<Object> objs = new ArrayList<Object>();
		try {
			PreparedStatement ps = this.dataBase.executeQuery(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Object obj = this.dataTablesClass.newInstance();
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
			rs.close();
			ps.close();
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objs;
	}

	@Override
	public String toString() {
		return "DBTab [dataTablesObject=" + dataTablesObject + ", name=" + name + ", isMust=" + isMust + ", include="
				+ include + ", value=" + value + ", DBName=" + DBName + ", autoUpdate=" + autoUpdate
				+ ", dataTablesClass=" + dataTablesClass + ", loader=" + loader + ", map=" + map + ", columns="
				+ columns + ", session=" + session + ", AIField=" + AIField + "]";
	}

	public int update(Update update) {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
		String sql = update.create();
		sql = FilterSql(sql);
		int start = 7;
		int end = sql.indexOf(" ", 7);
		String sub = sql.substring(start, end);
		sql = sql.replaceFirst(sub, this.name);
		PreparedStatement ps  = null;
		try {
			ps= this.dataBase.execute(sql);
			if (ps != null) {
				this.preparedParameter(ps,update.getParameters());
				ps.execute();
				QueryCache.getCache().cleanCache(this.getName());// 清理查询缓存
			}
			return ps.executeUpdate();
		} catch (SQLException | SecurityException e) {
			log.error("error to execute sql:" + update.create());
			log.error("parameter:" + update.getParameters());
			log.error(e);
		}finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("error to close preparesStatement at sql:" + update.create());
					log.error("parameter:" + update.getParameters());
					log.error(e);
				}
		}
		return 0;
	}

	public int update(Update update, Connection connection) throws SQLException {
		if (this.dataBase == null)
			throw new RuntimeException("DataTable mapping class " + this.dataTablesClass.getName()
					+ " datatable is null,please try to configure the @Tab attribute DB to declare database ");
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

	public void setDataTablesClass(Class<?> dataTablesClass) {
		this.dataTablesClass = dataTablesClass;
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
		if (this.dataTablesObject != null) {
			dBName = StringUtil.decodeVar(dBName, this.dataTablesObject);
		}
		DBName = dBName;
	}

	public void setMust(boolean isMust) {
		this.isMust = isMust;
	}

	public void setName(String name) {
		if (this.dataTablesObject != null) {
			name = StringUtil.decodeVar(name, this.dataTablesObject);
		}
		this.name = name;
	}

	public void setPrimary_key(Field primary_key) {
		Primary_key = primary_key;
	}

	public void setValue(String value) {
		if (this.dataTablesObject != null) {
			value = StringUtil.decodeVar(value, this.dataTablesObject);
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
	public String getSimpleName() {
		return name==null?null:name.substring(0, name.indexOf("."));
	}
	public Field getPrimary_key() {
		return Primary_key;
	}

	public String getValue() {
		return value;
	}

	public void setLoaderObject(Object object) {
		this.loader = new ClassLoader(object);
	}
}
