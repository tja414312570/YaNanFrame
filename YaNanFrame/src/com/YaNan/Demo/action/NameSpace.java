package com.YaNan.Demo.action;


import com.YaNan.Demo.model.Student;
import com.YaNan.frame.core.servlet.annotations.Action;
public class NameSpace {
	
	private Student student;
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	@Action
	public String name(){
		return student.toString();
	}
}
