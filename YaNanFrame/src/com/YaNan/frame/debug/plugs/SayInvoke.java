package com.YaNan.frame.debug.plugs;

import org.junit.Test;

import com.YaNan.frame.debug.plug.SayHello;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugs.debug.service.Say;

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
		for(int i = 0;i<100000;i++){
			say=PlugsFactory.getPlugsInstance(Say.class);
		}
		System.out.println("plugs create  :"+(System.currentTimeMillis()-t));
		System.out.println("say:"+say.say("Plugs"));
		t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++)
			say.say("Plugs");
		System.out.println("plugs execute  :"+(System.currentTimeMillis()-t));
		System.out.println("say:"+say.say("error"));
	}
	@Test
	public void testAop() throws Exception{
		Say say=PlugsFactory.getPlugsInstance(Say.class);
		System.out.println(say.say("参数不满足"));
		System.out.println(say.say("正常参数"));
		System.out.println(say.say("错误结果"));
		System.out.println(say.say("error"));//方法中抛出的异常
	}
}
