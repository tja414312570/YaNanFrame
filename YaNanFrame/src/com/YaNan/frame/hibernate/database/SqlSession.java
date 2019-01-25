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
	/**
	 * 查询唯一结果
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	<T> T selectOne(String sqlId,Object...parameters);
	/**
	 * 查询作为列表返回
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	<T> List<T> selectList(String sqlId, Object... parameters);
	/**
	 * 插入一条数据
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	<T> T insert(String sqlId,Object...parameters);
	/**
	 * 批量插入数据 未实现
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	<T> List<T> insertBatch(String sqlId,Object...parameters);
	/**
	 * 更新数据
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	<T> T update(String sqlId,Object...parameters);
	/**
	 * 删除数据
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	int delete(String sqlId,Object...parameters);

}
