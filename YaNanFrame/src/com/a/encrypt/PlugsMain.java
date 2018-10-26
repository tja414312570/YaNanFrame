package com.a.encrypt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextListener;

import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.hibernate.database.entity.SqlFragmentManger;
import com.YaNan.frame.hibernate.database.fragment.PreparedFragment;
import com.YaNan.frame.hibernate.database.fragment.SqlFragment;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.google.gson.Gson;

public class PlugsMain {
	
	@Service
	private SqlSession sqlSession;
	
	public <T> Object queryList(String sqlId,T... params){
		Object obj = sqlSession.selectList(sqlId,params);
		return obj;
	}
	public <T> Object queryOne(String sqlId,T... params){
		Object obj = sqlSession.selectOne(sqlId,params);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws SQLException {
		ServletContextListener servletContext = PlugsFactory.getPlugsInstance(HibernateContextInit.class);
		servletContext.contextInitialized(null);
		PlugsMain main = PlugsFactory.getPlugsInstance(PlugsMain.class);
		Map<String,String> parameter = new HashMap<String,String>();
 		parameter.put("id", "10");
 		parameter.put("name", "java");
 		Student student = new Student();
 		student.setId(10);
 		student.setName("java");
//		SqlFragment frag = SqlFragmentManger.getSqlFragment("testSql.query4");
		
//		PreparedSql pre = frag.getPreparedSql(parameter);
 		Object object = main.queryOne("testSql.query4",10,20);
//		System.out.println(new Gson().toJson((List<Student>)object));
 		System.out.println(object+":"+object.getClass());
	}

}
