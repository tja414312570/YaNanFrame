package com.YaNan.frame.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.YaNan.frame.plugin.annotations.Register;

/**
 * 默认日志工具，未完善
 * @author yanan
 *
 */
@Register
public class DefaultLog implements Log{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String className = "";
	public DefaultLog(){
		
	}
	public DefaultLog(Class<?> clzz){
		this.className = ": "+clzz.getName();
	}
	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void error(String s, Throwable e) {
		System.out.println(sdf.format(new Date()) +" Error" +className+ ":" + s );		
		e.printStackTrace();
	}

	@Override
	public void debug(String s) {
		System.out.println(sdf.format(new Date()) +" Debug"+className + ":" + s);	
	}

	@Override
	public void trace(String s) {
		System.out.println(sdf.format(new Date()) +" Trace"+className + ":" + s);	
	}

	@Override
	public void error(String s) {
		System.out.println(sdf.format(new Date()) +" Error"+className + ":" + s);	
	}

	@Override
	public void warn(String s) {
		System.out.println(sdf.format(new Date()) +" Warn"+className+ ":" + s);
	}
	@Override
	public void error(Throwable e) {
		System.out.println(sdf.format(new Date()) +" Error" +className+ ":");
		e.printStackTrace();
	}
}
