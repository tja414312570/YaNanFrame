package com.YaNan.Demo.action;

import java.util.List;

import javax.servlet.Servlet;

import org.junit.Test;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

public class debug {
	@Test
	public void test() {
		System.out.println("唤醒");
		Log log  = PlugsFactory.getPlugsInstance(Log.class);
		System.out.println("获取日志成功");
		log.debug("test 内容");
		 System.out.println("e");
		 List<Servlet> servlets = PlugsFactory.getPlugsInstanceList(Servlet.class);
		 System.out.println(servlets.size());
	}
}
