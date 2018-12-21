package com.YaNan.frame.hibernate.database;

import java.util.List;

import com.YaNan.frame.plugin.annotations.Service;

/**
 * sql会话即接口，未完善
 * @author yanan
 *
 */
@Service
public interface SqlSession {

	<T> T selectOne(String sql,Object...parameters);
	
	<T> List<T> selectList(String sqlId, Object... parameters);

}
