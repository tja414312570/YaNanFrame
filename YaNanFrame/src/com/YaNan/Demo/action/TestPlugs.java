package com.YaNan.Demo.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.YaNan.Demo.model.Student;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.ParameterDescription;
import com.YaNan.frame.servlets.ParameterDescription.ParameterType;
import com.YaNan.frame.servlets.annotations.Action;
import com.YaNan.frame.servlets.annotations.PathVariable;
import com.YaNan.frame.servlets.annotations.RequestMapping;

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
	@RequestMapping()
	public String sayHello(){
		return "hello";
	}
	@RequestMapping("/user/{username}/doc/{id}")
	public String say( String username,int id,Writer writer) throws IOException{
		writer.write("<h1>hello Restful-MVC</h1>");
		return "hello "+username+",your id is " +id;
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


