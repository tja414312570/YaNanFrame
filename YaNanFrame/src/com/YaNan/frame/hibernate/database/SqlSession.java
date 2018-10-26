package com.YaNan.frame.hibernate.database;

import java.util.List;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface SqlSession {

	<T> T selectOne(String sql,Object...parameters);
	
	<T> List<T> selectList(String sqlId, Object... parameters);

}
