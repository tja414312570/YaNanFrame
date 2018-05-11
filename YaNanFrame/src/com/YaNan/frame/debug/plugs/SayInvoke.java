package com.YaNan.frame.debug.plugs;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.servlet.ServletContextListener;

import org.junit.Test;

import com.YaNan.frame.debug.plug.SayHello;
import com.YaNan.frame.debug.plugs.service.Say;
import com.YaNan.frame.plugin.PluginAppincationContextInit;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.ServletBuilder;
import com.YaNan.frame.servlets.ServletDispatcher;

public class SayInvoke {
	public static void main(String[] args) throws Exception {
		Say say = null;
		long t = System.currentTimeMillis();
		for(int i = 0;i<100000;i++)
			say = new SayHello();
		System.out.println("native create :"+(System.currentTimeMillis()-t));
		 t = System.currentTimeMillis();
		for(int i = 0;i<100000;i++)
			say.say("hello");
		System.out.println("native execute :"+(System.currentTimeMillis()-t));
		t = System.currentTimeMillis();
		say=PlugsFactory.getPlugsInstance(Say.class);
		for(int i = 0;i<50000000;i++){
			say=PlugsFactory.getPlugsInstance(Say.class);
		}
		System.out.println("plugs create  :"+(System.currentTimeMillis()-t));
		System.out.println("say:"+say.say("Plugs"));
		t = System.currentTimeMillis();
		for(int i = 0;i<50000000;i++)
			say.say("Plugs");
		System.out.println("plugs execute  :"+(System.currentTimeMillis()-t));
		System.out.println("say:"+say.say("error"));
	}
//	@Test
	public void testAop() throws Exception{
		Say say=PlugsFactory.getPlugsInstance(Say.class);
		System.out.println(say.say("参数不满足"));
		System.out.println(say.say("正常参数"));
		System.out.println(say.say("错误结果"));
		System.out.println(say.say("error"));//方法中抛出的异常
	}
	@Test
	public void test() throws Exception{
		List<ServletContextListener> contextListernerList = PlugsFactory.getPlugsInstanceList(ServletContextListener.class);
		for(ServletContextListener contenxtInitListener :contextListernerList){
			contenxtInitListener.contextInitialized(null);
		}
	}
}
