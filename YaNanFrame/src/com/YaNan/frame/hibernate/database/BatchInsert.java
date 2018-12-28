package com.YaNan.frame.hibernate.database;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;

/**
 * 该类用于提供给DATab的query一个查询的SQL语句的生成方法 提过一个构造器，传入一个DBTab型的表对象，应为他需要使用DBTab context
 * 20181228 支持批量导入，自增键自动排除，删除、添加列等，增加数据库批量插入功能。
 * 
 * @author yanan
 *
 */
public class BatchInsert extends OperateImplement{
	private Log log = PlugsFactory.getPlugsInstance(Log.class,BatchInsert.class);
	private Collection<DBColumn> columns = new ArrayList<DBColumn>();
	/**
	 * 默认构造方法
	 * @param objects
	 */
	public BatchInsert(Object[] objects){
		if(objects==null||objects.length==0)
			throw new RuntimeException("Batch Insert object length is 0");
		this.dataTables = new DBTab(objects[0]);
		this.columns = this.dataTables.getFieldMap().values();
		for(Object object : objects){
			this.preparedParameter(object);
		}
	}
	public Collection<DBColumn> getColumns() {
		return columns;
	}
	/**
	 * 批量导入支持
	 * @param objects
	 */
	public BatchInsert(List<Object> objects){
		if(objects==null||objects.size()==0)
			throw new RuntimeException("Batch Insert object length is 0");
		this.dataTables = new DBTab(objects.get(0));
		this.columns = this.dataTables.getFieldMap().values();
		for(Object object : objects){
			this.preparedParameter(object);
		}
	}
	/**
	 * 批量导入支持
	 * @param objects
	 */
	public BatchInsert(Class<?> tabClass,String...columns){
		this.dataTables = new DBTab(tabClass);
		if(columns.length>0){
			this.columns = new ArrayList<DBColumn>();
			this.addColumn(columns);
		}else{
			this.columns = this.dataTables.getFieldMap().values();
		}
	}
	/**
	 * 添加需要修改的字段
	 * @param columns
	 */
	public void addColumn(String...columns){
		for(String columnName : columns){
			DBColumn column = this.dataTables.getDBColumn(columnName);
			if(column!=null){
				if(!this.columns.contains(column))
					this.columns.add(column);
			}else{
				log.warn("Column \""+columnName+"\" is not exist at table class "+this.dataTables.getDataTablesClass());
			}
		}
	}
	/**
	 * 移除不需要添加的字段
	 * @param columns
	 */
	public void removeColumn(String...columns){
		for(String columnName : columns){
			DBColumn column = this.dataTables.getDBColumn(columnName);
			if(column!=null){
				this.columns.remove(column);
			}else{
				log.warn("Column \""+columnName+"\" is not exist at table class "+this.dataTables.getDataTablesClass());
			}
		}
	}
	/**
	 * 增加要导入的数据
	 * @param objects
	 */
	public void addInsert(List<Object> objects){
		for(Object object : objects){
			this.preparedParameter(object);
		}
	}
	/**
	 * 增加要导入的数据
	 * @param objects
	 */
	public void addInsert(Object... objects){
		for(Object object : objects){
			this.preparedParameter(object);
		}
	}
	/**
	 * 增加要导入的数据
	 * @param object
	 */
	public void addInsert(Object object){
		this.preparedParameter(object);
	}
	/**
	 * 增加要导入的数据
	 * @param object
	 */
	public void addInsertData(List<Object> listData){
		Object[] parameters = new Object[this.dataTables.getFieldMap().size()];
		for(int i = 0;i<(listData.size()>this.dataTables.getFieldMap().size()?
				this.dataTables.getFieldMap().size():listData.size());i++){
			parameters[i] = listData.get(i);
		}
	}
	/**
	 * 增加要导入的数据
	 * @param object
	 */
	public void addInsertData(Object[] arrayData){
		Object[] parameters = new Object[this.dataTables.getFieldMap().size()];
		for(int i = 0;i<(arrayData.length>this.dataTables.getFieldMap().size()?
				this.dataTables.getFieldMap().size():arrayData.length);i++){
			parameters[i] =arrayData[i];
		}
	}
	/**
	 * 增加要导入的数据
	 * @param object
	 */
	public void addInsertMapList(List<Map<String,Object>> mapDatas){
		for(Map<String,Object> map: mapDatas){
			this.addInsertMap(map);
		}
	}
	/**
	 * 准备参数
	 * @param object
	 */
	public void addInsertMap(Map<String,Object> mapDatas) {
		Object[] parameters = new Object[this.dataTables.getFieldMap().size()];
		Iterator<DBColumn> fI = this.columns.iterator();
		DBColumn column;
		int i = 0;
		//获取需要插入的列值
		while(fI.hasNext()&&(column = fI.next())!=null&&!column.isAuto_Increment()){
			parameters[i++] = mapDatas.get(column.getName());
		}
		this.parameters.add(parameters);
	}
	/**
	 * 准备参数
	 * @param object
	 */
	private void preparedParameter(Object object) {
		Object[] parameters = new Object[this.dataTables.getFieldMap().size()];
		Iterator<DBColumn> fI = this.columns.iterator();
		DBColumn column;
		ClassLoader loader = new ClassLoader(object);
		int i = 0;
		//获取需要插入的列值
		while(fI.hasNext()&&(column = fI.next())!=null&&!column.isAuto_Increment()){
			try {
				parameters[i++] = loader.get(column.getField());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("could not get column value at \""+object+"\" at field \""+column.getField()+"\"",e);
			}
		}
		this.parameters.add(parameters);
	}
	/**
	 * 此方法用于创建数据库操作的sql语句。可以用于调试
	 */
	@Override
	public String create() {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(this.dataTables.getName()).append("(");
		if (this.dataTables.getFieldMap().size() == 0) 
			log.error("没有任何字段，请检查植入对象是否所有元素的值都为null或是否所有元素都设置了自增。具体请查看sql语句");
		Iterator<DBColumn> iterator = this.columns.iterator();
		DBColumn column;
		StringBuilder fb = new StringBuilder();
		while(iterator.hasNext()&&(column = iterator.next())!=null&&!column.isAuto_Increment()){
			sb.append(column.getName()).append(iterator.hasNext() ? "," : ") VALUES(");
			fb.append("?").append(iterator.hasNext() ? "," :")");
		}
		return sb.append(fb).toString();
	}
	/**
	 * 批量导入，导入数量不大于Integer.MAX_VALUE以下
	 * @return
	 */
	public int[] batchInsert(){
		return (int[])this.dataTables.batchInsert(this,false);
	}
	/**
	 * 批量导入，数量大于Integer.MAX_VALUE以下
	 * @return
	 */
	public long[] batchLargeInsert(){
		return (long[])this.dataTables.batchInsert(this,true);
	}
	/**
	 * 批量导入,返回可能为null，int[],long[]
	 * @return
	 */
	public Object insert(){
		return this.dataTables.batchInsert(this,this.getParameters().size()>Integer.MAX_VALUE);
	}
}
