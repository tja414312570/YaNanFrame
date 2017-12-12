# YaNanFrame
com.yanan.frame
此框架为M-C（为什么去掉V层？V层都交给前端啊）模式框架，提供持久层和交互层的实现。采用注解方式配置，支持多种交互方式，对象化的数据库层操作，
我们的理念为，程序员只关心核心业务逻辑，除此之外，全都交给框架吧。
来吧  一起为美好的明天奋斗

使用案例：
1 交互层


1）、action注解的使用（@Action)
package com.YaNan.demo;

import java.io.IOException;

import com.YaNan.frame.core.annotations.Action;
import com.YaNan.frame.core.annotations.RESPONSE_METHOD;
import com.YaNan.frame.core.annotations.Validate;
import com.YaNan.frame.core.servletSupport.DefaultServlet;
/**
 * 如果需要获取  HttpServletRequest HttpServletRespone等，请继承自各对应Servlet扩展类
 * 对应对象的命名  HttpServletRequest RequestContext
 * 				HttpServletResponse ResponseContext
 * 				Token tokenContext
 * 如果只需要get方式请求  无需继承任何扩展类
 * 各扩展类型说明(下面的扩展类依次继承关系）
 * DefaultServlet 默认Servlet扩展，支持get方式传参，支持获取HttpServlet对象
 * MultiFormServlet 表单Servlet扩展 继承自DefalutServlet 支持post请求 
 * TokenServlet tokenServlet扩展，继承自MultiFormServlet 支持获取Token，以及支持@TokenObject注解 依赖组件  com.YaNan.frame.session
 * 
 * @author yanan
 *
 */
public class ActionAnnotationsTest extends DefaultServlet{
	@Validate(RegExpression="[\\S]{2,}",Failed="请输入字少两个字符"/*,isNull="请输入中文  两个字符以上"*/)
	private String name;
	// 以下两个接口测试  action中method（返回内容的方式）
	@Action(method=RESPONSE_METHOD.FORWARD)
	public String testForward(){
		return "index.html";
	}
	@Action(method=RESPONSE_METHOD.REDIRCET)
	public String testRedirect(){
		return "index.html";
	}
	//以下  命名空间的使用 访问路径 项目路径/test/testNamespace.do
	 * 以下  命名空间的使用
	@Action(namespace="test")
	public String testNamespace(){
		return "你得到了内容";
	}
	// 以下   output属性的使用，某些情况下  我们可能不是返回字符类型数据
	@Action(output=true)
	public void testOutput(){
		try {
			this.ResponseContext.getWriter().write("你得到了你想要的内容".toCharArray());
			this.ResponseContext.getWriter().flush();
			this.ResponseContext.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//解析返回结果  其中 ${}中为该对象拥有的属性的值，没有则返回原内容
	@Action(decode=true)
	public String testDecode(){
		this.setName("解码内容");
		return "解码结果：${name}";
	}
	//跨域支持
	@Action(CorssOrgin=true)
	public String testCO(){
		return "跨域显示内容";
	}
	// args参数  为一个数组,需要验证的参数，必须配合使用注解@Validate。但@Validate可以独立使用，独立使用时仅在请求中含有该参数时有效
	 * args参数  为一个数组,需要验证的参数，必须配合使用注解@Validate。但@Validate可以独立使用，独立使用时仅在请求中含有该参数时有效
	@Action(args={"name"})
	public String testArgs(){
		return this.name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}


2）、result注解的使用（@Result)
package com.UFO.action.BBQ;

import com.YaNan.frame.core.annotations.Action;
import com.YaNan.frame.core.annotations.ActionResults.Result;
import com.YaNan.frame.core.annotations.RESPONSE_METHOD;

/**
 * TokenServlet简单案例
 * @author yanan
 *
 */
public class ResultAnnotationsTest{
	private int result;
	@Result(name = "success", value = "index.html",method=RESPONSE_METHOD.FORWARD)
	@Result(name = "failed", value = "index.html",method=RESPONSE_METHOD.REDIRCET)
	@Action
	public String getResult(){
		if(result<10)
			return "你输入的数字是"+this.result;
		if(result>1000)
			return "failed";
		return "success";
	}
	public void setResult(int result) {
		this.result = result;
	}
}

3、TokenDefault的使用
package com.UFO.action.BBQ;

import com.YaNan.frame.core.annotations.Action;
import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.annotation.TokenObject;
import com.YaNan.frame.core.session.servletSupport.TokenServlet;
/**
 * TokenServlet简单案例
 * @author yanan
 *
 */
public class TokenServletTest extends TokenServlet{
	@TokenObject
	private Student student;
	/**
	 * 保存学生信息
	 * @return
	 */
	@Action
	public String saveStudentInfo(){
		Student student = new Student();
		student.name="java";
		student.age = 22;
		this.TokenContext.set(student);
		//or this.TokenContext.set(Student.class,student);
		return "保存学生信息成功";
	}
	/**
	 * 获取学生信息
	 * @return
	 */
	@Action
	public String getStudentInfo(){
		return this.student.toString();
	}
	@Action
	public String saveStudentInfo2(){
		Student student = new Student();
		student.name="php";
		student.age =5;
		Token token = this.TokenContext;
		token.set(Student.class, student);
		return "保存学生信息成功";
	}
	@Action
	public String getStudentInfo2(){
		return this.TokenContext.get(Student.class).toString();
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
}
class Student{
	@Override
	public String toString() {
		return "Student [name=" + name + ", age=" + age + "]";
	}
	String name;
	int age;
}

持久层：
package com.UFO.dao.impl;

import com.UFO.model.user.TestUser2;
import com.YaNan.frame.core.annotations.Action;
import com.YaNan.frame.hibernate.database.Delete;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.hibernate.database.Update;
import com.google.gson.Gson;

/**  
* 创建时间：2017年12月12日 下午4:33:19  
* @version 1.0   
* @since JDK 1.7.0_21  
* 文件名称：DaoTest.java  
* 类说明：  框架的使用
*/
public class DaoTestUser2 extends TestUser2{
	
	/*
	 * 增加
	 */
	@Action
	public String add(){
		this.setName("女神");
		this.setAge(18);
		this.setTiphone("15656565656");
		
		Insert insert = new Insert(this);
		return insert.insert()?"{status:'success',message:'信息提交成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
	}
	
	/*
	 * 查询   通过Tid来查看所在行
	 */
	@Action
	public String select(){
		Query query = new Query(this);
		query.addCondition("Tid", this.Tid=1);
		return "{\"data\":"+new Gson().toJson(query.query())+"}";
	}
	
	/*
	 * 修改
	 */
	@Action
	public String up(){
		this.name = "hh";
		this.Tid = 2;
		Update update = new Update(this,"name");
		//条件，通过id来修改name属性
		update.addCondition("Tid",this.Tid);
		
		return update.update()>0?"{status:'success',message:'数据修改成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
	}
	
	/*
	 * 删除
	 */
	@Action
	public String delete(){
		Delete delete = new Delete(this);
		delete.addCondition("Tid", this.Tid = 2);
		return delete.delete() >0?"{status:'success',message:'数据修改成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
	}
}
2）、使用main方法进行数据库调试
package com;

import java.io.File;

import com.UFO.model.user.Record;
import com.YaNan.frame.core.annotations.Action;
import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.Delete;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.hibernate.database.Update;
import com.google.gson.Gson;

public class text extends Record {
	
	
	
	//添加信息
	@Action
	public String add(){
		this.name = "张三";
		Insert insert = new Insert(this);
		return insert.insert()?"{status:'success',message:'信息提交成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
			
	}
	
	
	//查询信息
	public String select(){
		
		Query query = new Query(this);
		return new Gson().toJson(query.query());
		
		
	}
	
	
	
	//修改信息
	public String update(){
		this.name = "魏六";
		this.id = 1;
		Update update = new Update(this,"name");
		update.addCondition("id",this.id);
		return update.update()>0?"{status:'success',message:'数据修改成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
	}

	
	
	 //删除信息
	public String delete(){
		
		Delete delete = new Delete(this);
		delete.addCondition("id","2");
		
		return delete.delete()>0?"{status:'success',message:'数据删除成功'}":"{status:'failed',reason:'InnerErr',message:'服务器内部错误！'}";
		
		
		
	}
	
	public static void main(String[] args) {
		File file = new File("src/Hibernate.xml");
		DBFactory.getDBFactory().init(file);
		text text = new text();
		System.out.println(text.delete());
		 
	}

}

