package com.YaNan.frame.hibernate.database.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.YaNan.frame.hibernate.database.cache.QueryCache;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.hibernate.orm.OrmBuilder;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * sql执行类 所有sqlsession提供的方法都在此处实现
 * 
 * @author yanan
 *
 */
public class PreparedSql {
	private String sql;
	private List<Object> parameter;
	private SqlFragment sqlFragment;
	private Log log = PlugsFactory.getPlugsInstance(Log.class, PreparedSql.class);

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
		Connection connection = this.sqlFragment.getDataBase().getConnection();
		try {
			log.debug("prepared sql:" + this.sql);
			log.debug("prepared parameter:" + this.parameter);
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(sql);
			Iterator<Object> collect = parameter.iterator();
			this.preparedParameter(ps, collect);
			ResultSet rs = ps.executeQuery();
			this.sqlFragment.getDataBase().releaseConnection(connection);
			// 通过orm Builder 来组装返回结果
			OrmBuilder builder = PlugsFactory.getPlugsInstanceByAttributeStrict(OrmBuilder.class,
					sqlFragment.getResultType());
			List<Object> result = builder.builder(rs, sqlFragment);
			log.debug("result rows:" + result.size());
			rs.close();
			ps.close();
			return (T) result;
		} catch (Throwable t) {
			throw t;
		} finally {
			this.sqlFragment.getDataBase().releaseConnection(connection);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T queryOne() throws SQLException {
		Connection connection = this.sqlFragment.getDataBase().getConnection();
		try {
			log.debug("prepared sql:" + this.sql);
			log.debug("prepared parameter:" + this.parameter);
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(sql);
			Iterator<Object> collect = parameter.iterator();
			this.preparedParameter(ps, collect);
			ResultSet rs = ps.executeQuery();
			this.sqlFragment.getDataBase().releaseConnection(connection);
			if (sqlFragment.getResultType() == null)
				throw new RuntimeException("Query result type is not allowed to be empty");
			// 通过orm Builder 来组装返回结果
			OrmBuilder builder = PlugsFactory.getPlugsInstanceByAttributeStrict(OrmBuilder.class,
					sqlFragment.getResultType());
			List<Object> result = builder.builder(rs, sqlFragment);
			if (result.size() > 1)
				throw new RuntimeException("query result rows should is \"1\" but has \"" + result.size() + "\"");
			rs.close();
			ps.close();
			return (T) (result.size() == 1 ? result.get(0) : null);
		} catch (Throwable t) {
			throw t;
		} finally {
			this.sqlFragment.getDataBase().releaseConnection(connection);
		}

	}

	private void preparedParameter(PreparedStatement ps, Iterator<Object> collect) throws SQLException {
		int i = 0;
		while (collect.hasNext()) {
			ps.setObject(++i, collect.next());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T insert() throws SQLException {
		Connection connection = this.sqlFragment.getDataBase().getConnection();
		try {
			log.debug("prepared sql:" + this.sql);
			log.debug("prepared parameter:" + this.parameter);
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(sql,
					java.sql.Statement.RETURN_GENERATED_KEYS);
			Iterator<Object> collect = parameter.iterator();
			this.preparedParameter(ps, collect);
			Class<?> resultType = sqlFragment.getResultTypeClass();
			Object generatedKey = 0;
			Object result = ps.execute();
			QueryCache.getCache().cleanCache(this.sqlFragment.getDataBase().getName());// 清理数据库缓存
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				generatedKey = rs.getInt(1);
			rs.close();
			ps.close();
			log.debug("execute result:" + result + ",generatedKey:" + generatedKey);
			if (resultType != null) {
				if (resultType.equals(int.class) || resultType.equals(Integer.class)) {
					return (T) generatedKey;
				}
				if (resultType.equals(boolean.class) || resultType.equals(Boolean.class)) {
					return (T) result;
				}
			}
			return null;
		} catch (Throwable t) {
			throw t;
		} finally {
			this.sqlFragment.getDataBase().releaseConnection(connection);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T update() throws SQLException {
		Connection connection = this.sqlFragment.getDataBase().getConnection();
		try {
			log.debug("prepared sql:" + this.sql);
			log.debug("prepared parameter:" + this.parameter);
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(sql);
			Iterator<Object> collect = parameter.iterator();
			this.preparedParameter(ps, collect);
			Class<?> resultType = sqlFragment.getResultTypeClass();
			Object result = ps.executeUpdate();
			QueryCache.getCache().cleanCache(this.sqlFragment.getDataBase().getName());// 清理数据库缓存
			ps.close();
			log.debug("execute result:" + result);
			if (resultType != null) {
				if (resultType.equals(int.class) || resultType.equals(Integer.class)) {
					return (T) result;
				}
				if (resultType.equals(boolean.class) || resultType.equals(Boolean.class)) {
					return (T) ((Object) ((int) result > 0));
				}
			}
			return null;
		} catch (Throwable t) {
			throw t;
		} finally {
			this.sqlFragment.getDataBase().releaseConnection(connection);
		}
	}
}
