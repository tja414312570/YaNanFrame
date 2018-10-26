package com.a.encrypt;

import java.util.List;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface SqlSession {

	@SuppressWarnings("unchecked")
	<T> T selectOne(String sql,T...parameters);
	
	@SuppressWarnings("unchecked")
	<T> List<T> selectList(String sqlId, T... parameters);

}
