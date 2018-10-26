package com.a.encrypt;

import java.util.List;

import com.YaNan.frame.util.StringUtil;

public class TestVariable {
	public static void main(String[] args) {
		String str = "SELECT name FROM student"
				+ "<trim prefix = 'where' prefixoverride='and or' suffix='' suffixoverride='and or'>"
				+ "<if test='id != null and id > 0'>id = #{id}</if>" + "and"
				+ "<if test = 'name!=null'>name like '%'||${name}||'%'</if>" + "</trim>"
				+ "<if test='id == null'>id = 0</if>";
		List<String> vars = StringUtil.findAllVars(str,"${ }", "#{ }","' '");
		System.out.println(vars);
	}
}
