package com.YaNan.frame.hibernate.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBColumn;
import com.YaNan.frame.hibernate.database.DBTab;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.reflect.ClassLoader;

@Register(priority=Integer.MAX_VALUE)
public class DefaultOrmBuilder implements OrmBuilder{
	@Override
	public List<Object> builder(ResultSet resultSet, SqlFragment sqlFragment) {
		try {
			List<Object> results = new ArrayList<Object>();
			//1获取返回值类型
			Class<?> resultType = sqlFragment.getResultTypeClass();
			//2判断类型时否是List map 等聚合函数
			if(ClassLoader.implementsOf(resultType, Map.class)){
				this.wrapperMap(resultSet, results,resultType);
			}else{
				if(ClassLoader.isBaseType(resultType))
					while(resultSet.next())
						results.add(ClassLoader.castType( resultSet.getObject(1), resultType));
				else
					this.wrapperBean(resultSet,results,resultType);
			}
			return results;
		} catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("failed to wrapper the result set!",e);
		}
	}
	private void wrapperBean(ResultSet resultSet, List<Object> result, Class<?> resultType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
		DBTab tab = new DBTab(resultType);
		while (resultSet.next()) {
			//可以使用PlugsHandler代理类，实现aop。但对Gson序列化有影响
//			Object beanInstance = PlugsFactory.getPlugsInstance(resultType);
			ClassLoader loader = new ClassLoader(resultType);
			Iterator<DBColumn> columnIterator = tab.getDBColumns().values().iterator();
			while(columnIterator.hasNext()){
				DBColumn dbColumn = columnIterator.next();
				Field field = dbColumn.getField();
				Object object = resultSet.getObject(dbColumn.getName());
				if(object==null)
					continue;
				loader.set(field,ClassLoader.castType(object,field.getType()));
			}
			result.add(loader.getLoadedObject());
		}
	}
	private void wrapperMap(ResultSet resultSet,List<Object> results, Class<?> resultType) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		String[] colNameArray = this.getColumnName(metaData);
		while (resultSet.next()) {
			Map<String,Object> map = new HashMap<String,Object>();
			for(int i = 0 ;i<colNameArray.length;i++){
				map.put(colNameArray[i], resultSet.getObject(i+1));
			}
			results.add(map);
		}
	}
	private String[] getColumnName(ResultSetMetaData metaData) throws SQLException{
		int colCount = metaData.getColumnCount();
		String[] colNameArray = new String[colCount];
		for(int i = 0;i<colCount;i++)
			colNameArray[i] = metaData.getColumnLabel(i+1);
		return colNameArray;
	}
}
