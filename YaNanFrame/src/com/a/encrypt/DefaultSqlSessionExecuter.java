package com.a.encrypt;

import java.sql.SQLException;
import java.util.List;

import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.hibernate.database.entity.SqlFragmentManger;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.plugin.annotations.Register;

@Register
public class DefaultSqlSessionExecuter implements SqlSession{

	@Override
	public <T> T selectOne(String sqlId, T... parameters) {
		SqlFragment frag = SqlFragmentManger.getSqlFragment(sqlId);
		PreparedSql pre = frag.getPreparedSql(parameters);
		try {
			return pre.queryOne();
		} catch (SQLException e) {
			throw new RuntimeException("faild to execute query \""+sqlId+"\"",e);
		}
	}

	@Override
	public <T> List<T> selectList(String sqlId, T... params) {
		SqlFragment frag = SqlFragmentManger.getSqlFragment(sqlId);
		PreparedSql pre = frag.getPreparedSql(params);
		try {
			return pre.query();
		} catch (SQLException e) {
			throw new RuntimeException("faild to execute query \""+sqlId+"\"",e);
		}
	}

}
