package com.YaNan.Demo.model;

import java.io.File;
import java.util.List;

import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.Delete;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.hibernate.database.Update;

public class Hibernate {
	public static void main(String[] args) {
		
		File file = new File(Hibernate.class.getClassLoader().getResource("").getPath().replace("%20"," "),"hibernate.xml");
		DBFactory factory = DBFactory.getDBFactory();
		factory.init(file);
		//将学生数据插入数据表中
		Hibernate hibernate = new Hibernate();
		System.out.println(hibernate.insertStudent());
		
//		System.out.println(hibernate.queryStudent());
//		System.out.println(hibernate.updateStudent());
//		System.out.println(hibernate.queryStudent());
//		System.out.println(hibernate.deleteStudent());
	}
	//插入数据
	public boolean insertStudent(){
		Student student = new Student();
		student.setName("hello world");
		student.setAge(16);
		student.setId(0);
		Insert insert = new Insert(student);
		return insert.insert();
	}
	//更新数据
	public int updateStudent(){
		Student student = new Student();
		student.setName("hello java");
		student.setAge(20);
		student.setId(10);
		Update update = new Update(student);
		update.addCondition("id", 0);
		return update.update();
	}
	//查询数据
	public List<?> queryStudent(){
		Query query = new Query(Student.class);
		return query.query();
	}
	//删除数据
	public int deleteStudent(){
		Delete delete = new Delete(Student.class);
		return delete.delete();
	}
}