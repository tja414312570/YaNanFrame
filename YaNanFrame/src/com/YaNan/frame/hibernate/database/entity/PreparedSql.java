package com.YaNan.frame.hibernate.database.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.hibernate.orm.OrmBuilder;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement; 

/**
 * sql执行类
 * 所有sqlsession提供的方法都在此处实现
 * @author yanan
 *
 */
public class PreparedSql {
	private String sql;
	private List<Object> parameter;
	private SqlFragment sqlFragment;
	private Log log = PlugsFactory.getPlugsInstance(Log.class,PreparedSql.class);
	@Override
	public String toString() {
		return "PreparedSql [sql=" + sql + ", parameter=" + parameter + ", sqlFragment=" + sqlFragment + "]";
	}
	public PreparedSql(String sql, List<Object> parameter, SqlFragment sqlFragment) {
		super();
		this.sql = sql;
		this.parameter = parameter;
		this.sqlFragment = sqlFragment;
	}
	@SuppressWarnings("unchecked")
	public <T> T query() throws SQLException {
		log.debug("prepared sql:"+this.sql);
		log.debug("prepared parameter:"+this.parameter);
		Connection  connection = this.sqlFragment.getDataBase().getConnection();
		PreparedStatement ps = (PreparedStatement) connection
				.prepareStatement(sql);
		Iterator<Object> collect = parameter.iterator();
		int i = 0 ;
		while(collect.hasNext()){
			ps.setObject(++i, collect.next());
		}
		ResultSet rs = ps.executeQuery();
		this.sqlFragment.getDataBase().releaseConnection(connection);
		//通过orm Builder 来组装返回结果
		OrmBuilder builder = PlugsFactory.getPlugsInstanceByAttributeStrict(OrmBuilder.class, sqlFragment.getResultType());
		List<Object> result = builder.builder(rs, sqlFragment);
		log.debug("result rows:"+result.size());
		rs.close();
		return (T) result;
	}
	@SuppressWarnings("unchecked")
	public <T> T queryOne() throws SQLException {
		log.debug("prepared sql:"+this.sql);
		log.debug("prepared parameter:"+this.parameter);
		Connection  connection = this.sqlFragment.getDataBase().getConnection();
		PreparedStatement ps = (PreparedStatement) connection
				.prepareStatement(sql);
		Iterator<Object> collect = parameter.iterator();
		int i = 0 ;
		while(collect.hasNext()){
			ps.setObject(++i, collect.next());
		}
		ResultSet rs = ps.executeQuery();
		this.sqlFragment.getDataBase().releaseConnection(connection);
		//通过orm Builder 来组装返回结果
		OrmBuilder builder = PlugsFactory.getPlugsInstanceByAttributeStrict(OrmBuilder.class, sqlFragment.getResultType());
		List<Object> result = builder.builder(rs, sqlFragment);
		if(result.size()>1)
			throw new RuntimeException("query result rows should is \"1\" but has \""+result.size()+"\"");
		rs.close();
		return (T) (result.size()==1?result.get(0):null);
	}
}
