package com.YaNan.Demo.action;

import java.lang.reflect.Method;

import org.junit.Test;

import com.YaNan.Demo.model.Student;
import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.PathVariable;
import com.YaNan.frame.core.servlet.annotations.RequestMapping;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

public class TestPlugs {
	private Student student;
	private String name;
	@Test
	public void test(){
		Method[] methods = TestPlugs.class.getMethods();
		for(Method method :methods){
			System.out.println(method);
		}
	}
	@Action
	public Class<? extends Log> getLogs(){
		Log log = PlugsFactory.getPlugsInstance(Log.class,this.getClass());
		System.out.println(log);
		log.debug("调试信息");
		return log.getClass();
	}
	@Action
	public String pogo(){
		System.out.println(student);
		return student.toString();
	}
	@Action(args="name:your default name",CorssOrgin=true)
	public String getName(){
		return "hello"+name;
	}
	@RequestMapping(value="/user/{name}")
	public String name(@PathVariable String name,@PathVariable int id){
		return "hello"+name;
	}
	@Action(args="student")
	public String getStudent(){
		return this.student.toString();
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public void setName(String name) {
		this.name = name;
	}
}
