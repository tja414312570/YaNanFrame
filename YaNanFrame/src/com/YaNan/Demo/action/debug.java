package com.YaNan.Demo.action;

import java.util.List;

import javax.servlet.Servlet;

import org.junit.Test;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.util.StringUtil;

public class debug {
	@Test
	public void test() {
		System.out.println(StringUtil.matchURI("/DashBoard/DataTabels", "/DashBoard/DataTables"));
	}
}
