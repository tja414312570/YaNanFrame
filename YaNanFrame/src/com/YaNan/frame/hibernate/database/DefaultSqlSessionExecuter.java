package com.YaNan.frame.hibernate.database;

import java.sql.SQLException;
import java.util.List;

import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.hibernate.database.entity.SqlFragmentManger;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.plugin.annotations.Register;
/**
 * 框架默认sqlsession的实现类
 * @author yanan
 *
 */
@Register
public class DefaultSqlSessionExecuter implements SqlSession{
	/**
	 * 从数据库中查询数据</br>
	 * 除非为java基础数据类型和String，否则参数只有第一个有效，无须再mapper中定义参数类型</br>
	 * !该查询条件表明满足该语句的数据在数据库中最多只有一条，否则会抛出异常</br>
	 */
	@Override
	public <T> T selectOne(String sqlId, Object... parameters) {
		SqlFragment frag = SqlFragmentManger.getSqlFragment(sqlId);
		PreparedSql pre = frag.getPreparedSql(parameters);
		try {
			return pre.queryOne();
		} catch (SQLException e) {
			throw new RuntimeException("faild to execute query \""+sqlId+"\"",e);
		}
	}
	/**
	 * 从数据库中查询结果集，需要从mapper中定义返回类型，返回类型为一个list或其实现类。</br>
	 * 除非为java基础数据类型和String，否则参数只有第一个有效，无须再mapper中定义参数类型</br>
	 */
	@Override
	public <T> List<T> selectList(String sqlId, Object... params) {
		SqlFragment frag = SqlFragmentManger.getSqlFragment(sqlId);
		PreparedSql pre = frag.getPreparedSql(params);
		try {
			return pre.query();
		} catch (SQLException e) {
			throw new RuntimeException("faild to execute query \""+sqlId+"\"",e);
		}
	}

}
