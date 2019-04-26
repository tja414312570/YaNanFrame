package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 
 * @author Administrator
 *
 */
public class Insert extends OperateImplement{
	private Object obj;
	protected List<String> fieldList = new LinkedList<String>();
	private Logger log = LoggerFactory.getLogger(Insert.class);
	private int generatedKey;
	public List<String> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}
	public void setFieldMap(Map<String,Object> fieldMap) {
		for(Entry<String,Object> entry : fieldMap.entrySet()){
			this.fieldList.add(entry.getKey());
			this.addParameters(entry.getValue());
		}
	}
	public int getGeneratedKey() {
		return generatedKey;
	}
	/**
	 * 默认构造方法
	 * @param obj
	 */
	public Insert(Object obj) {
		this.dataTables = new DBTab(obj);
		this.obj = obj;
		Iterator<Field> fI = this.dataTables.getFieldMap().keySet().iterator();
		Field field;
//		try {
//			while(fI.hasNext()&&
//					(field = fI.next())!=null&&
//					!this.dataTables.getDBColumn(field).isAuto_Increment()&&
//					this.dataTables.getLoader().get(field.getName())!=null&&
//					this.fieldList.add(this.dataTables.getFieldMap().get(field).getName())&&
//					this.parameters.add(this.dataTables.getLoader().get(field.getName())));
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//				| SecurityException e1) {
//			e1.printStackTrace();
//		}
		while(fI.hasNext()&&(field = fI.next())!=null){
			try {
//				这段代码和下面代码作用在本程序中等同
//				if(!this.dataTables.getDBColumn(field).isAuto_Increment()
//						&&this.dataTables.getLoader().get(field.getName())!=null
//						&&this.fieldList.add(this.dataTables.getFieldMap().get(field).getName())
//						&&this.parameters.add(this.dataTables.getLoader().get(field.getName())));
				
				if(!this.dataTables.getDBColumn(field).isAuto_Increment()){
					Object value = this.dataTables.getLoader().get(field);
					if(value!=null){
						String columnName=this.dataTables.getFieldMap().get(field).getName();
						if(columnName!=null){
							int point = columnName.indexOf(".");
							if(point>-1){
								columnName = columnName.substring(0, point);
								if(!columnName.equals(this.dataTables.getSimpleName()))
									continue;
							}
							this.fieldList.add(columnName);
							this.parameters.add(value);
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	/**
	 * 此方法用于创建数据库操作的sql语句。可以用于调试
	 */
	@Override
	public String create() {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(this.dataTables.getName()).append("(");
		if (this.fieldList.size() == 0) 
			log.error("没有任何字段，请检查植入对象是否所有元素的值都为null或是否所有元素都设置了自增。具体请查看sql语句");
		Iterator<String> iterator = this.fieldList.iterator();
		while (iterator.hasNext())
				sb.append(iterator.next()).append(iterator.hasNext() ? "," : ") VALUES(");
		iterator = this.fieldList.iterator();
		while (iterator.hasNext()){
			iterator.next();
			sb.append("?").append(iterator.hasNext()? "," :")");
		}
		return sb.toString();
	}
	/**
	 * 导入数据到数据库
	 * @return
	 */
	public boolean insert() {
		return this.insertGk()>=0;
	}
	/**
	 * 导入数据库，传入一个接受改变的对象
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public int insertGk() {
		this.generatedKey = this.dataTables.insert(this);
		return this.generatedKey;
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
					f = this.dataTables.getDataTablesClass().getDeclaredField(field);
					f.setAccessible(true);
					update.addCondition(f, f.get(obj));
					query.addCondition(f.getName(), f.get(this.obj));
				}
				if (query.query().size() != 0) {
					if(this.dataTables.getPrimary_key()!=null)
						update.removeField(this.dataTables.getPrimary_key().getName());
					return update.update()>0;
				} else {
					return insert();
				}
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				log.error(e.getMessage(),e);
				return false;
			}
		} else {
			return insert();
		}
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
				this.fieldList.remove(f);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
	}
}
