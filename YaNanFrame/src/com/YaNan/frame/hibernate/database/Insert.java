package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Insert extends OperateImplement{
	private Object obj;
	private Map<String, String> fieldMap = new LinkedHashMap<String, String>();
	private Log log = PlugsFactory.getPlugsInstance(Log.class,Insert.class);
	/**
	 * 默认构造方法
	 * @param obj
	 */
	public Insert(Object obj) {
		this.dbTab = new DBTab(obj);
		this.obj = obj;
		try {
			if (!this.dbTab.exists()) {
				if (this.dbTab.isMust())
					this.dbTab.create();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Iterator<Field> fI = this.dbTab.getFieldMap().keySet().iterator();
		while(fI.hasNext()){
			Field field = fI.next();
			try {
				if (this.dbTab.getDBColumn(field).isAuto_Increment())
					continue;
				if(this.dbTab.getLoader().get(field.getName())!=null){
					this.fieldMap.put(this.dbTab.getFieldMap().get(field).getName(),"'"+this.dbTab.getLoader().get(field.getName()).toString().replace("'", "\\'")+"'");
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 此方法用于创建数据库操作的sql语句。可以用于调试
	 */
	@Override
	public String create() {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(this.dbTab.getName()).append("(");
		if (this.fieldMap.size() == 0) 
			log.error("没有任何字段，请检查植入对象是否所有元素的值都为null或是否所有元素都设置了自增。具体请查看sql语句");
		Iterator<String> iterator = this.fieldMap.keySet().iterator();
		while (iterator.hasNext()) {
				sb.append(iterator.next()).append(iterator.hasNext() ? "," : ") VALUES(");
		}
		iterator = this.fieldMap.keySet().iterator();
		while (iterator.hasNext())
			sb.append(this.fieldMap.get(iterator.next())).append(iterator.hasNext() ? "," : ")");
		return sb.toString();
	}
	/**
	 * 导入数据到数据库
	 * @return
	 */
	public boolean insert() {
		try {
			return this.dbTab.insert(this)!=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	/**
	 * 导入或更新，如果纯在，则为导入，否则为更新
	 * @return
	 */
	public boolean insertOrUpdate(String... fields) {
		if (fields.length > 0) {
		Query query = new Query(this.obj);
		Update update = new Update(this.obj);
		try{
			for(String field : fields){
					Field f;
					f = this.dbTab.getCls().getDeclaredField(field);
					f.setAccessible(true);
					update.addCondition(f, f.get(obj));
					query.addCondition(f.getName(), f.get(this.obj));
				}
				if (query.query().size() != 0) {
					if(this.dbTab.getPrimary_key()!=null)
						update.removeField(this.dbTab.getPrimary_key().getName());
					return update.update()>0;
				} else {
					return insert();
				}
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return insert();
		}
	}
	/**
	 * 导入数据库，传入一个接受改变的对象
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public boolean insert(Object object) {
		try {
			Object result = this.dbTab.insert(this);
			ClassLoader.DisClone(object,result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 移除不需要获取的字段
	 * @param string
	 */
	public void removeField(String... string) {
		for(String str :string){
			Field f;
			try {
				f = this.obj.getClass().getDeclaredField(str);
				this.fieldMap.remove(f);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
	}
	public void addAutoIncrement(String... strings) {
		for(String string : strings)
		try {
			String name = this.dbTab.getDBColumn(string).getName();
			this.fieldMap.put(name, "last_insert_id()+1");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			continue;
		}
	}
	public void addAutoIncrement(Field... fields) {
		for(Field field : fields)
		try {
			field.setAccessible(true);
			String name = this.dbTab.getDBColumn(field).getName();
			this.fieldMap.put(name, "last_insert_id()+1");
		} catch (SecurityException e) {
			e.printStackTrace();
			continue;
		}
	}
}
