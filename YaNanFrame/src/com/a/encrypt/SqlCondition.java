package com.a.encrypt;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.YaNan.frame.hibernate.database.fragment.Symbol.JAVASCRIPT;
import com.YaNan.frame.util.StringUtil;


public class SqlCondition {
	
	public static void main(String[] args) throws ScriptException {
		
		String proxyClassName = "$Cglib$YaNan$namespace$testCondition";
		String condition = " where id = #{id} and name like'%${name}%'" ;
		System.out.println(StringUtil.find(condition, "#{", "}", "?"));
		Map<String,String> arguments = new HashMap<String,String>();
		arguments.put("test","value");
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
		Bindings binder = scriptEngine.createBindings();
		binder.put("test", "q");
		Object result = scriptEngine.eval(condition, binder);
		System.out.println(result+"  "+result.getClass());
		System.out.println(result(condition,arguments));
	}
	public static boolean result(String condition,Object...arguments){
		
		return false;
	}
}
