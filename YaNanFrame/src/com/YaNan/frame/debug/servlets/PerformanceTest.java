package com.YaNan.frame.debug.servlets;

import java.util.HashMap;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.YaNan.frame.servlets.REQUEST_METHOD;
import com.YaNan.frame.util.StringUtil;

public class PerformanceTest {
	@Test
	public void testMatch(){
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		for(int i = 0;i<10000;i++)
			map.put(i+"", i);
		map.put("/user/doc", "hello");
		long t = System.currentTimeMillis();
		for(int i = 0;i<10000000;i++)
			StringUtil.matchURI("/user/doc", "/*/*");
		System.out.println("String util :"+(System.currentTimeMillis()-t));
		System.out.println("match result :"+StringUtil.matchURI("/user/doc", "/*/*"));
		 t = System.currentTimeMillis();
		for(int i = 0;i<10000000;i++)
			map.get("/user/doc");
		System.out.println("Map get :"+(System.currentTimeMillis()-t));
		System.out.println("map result :"+map.get("/user/doc"));
		t = System.currentTimeMillis();
		for(int i = 0;i<10000000;i++)
			"/user/doc".matches("/*/*");
		System.out.println("regex java:"+(System.currentTimeMillis()-t));
		System.out.println("match result :"+"/user/doc".matches("/*"));
	}
//	@Test
	public void testArray(){
		String src = "hello";
		char[] chars = src.toCharArray();
		System.out.println(chars.getClass());
		for(char c :chars){
			System.out.println(c);
		}
		byte[] bytes = src.getBytes();
		System.out.println(bytes.getClass());
		for(byte b :bytes){
			System.out.println(b);
		}
	}
//	@Test
	public void testSwitch(){
		long t = System.currentTimeMillis();
		for(int i = 0;i<100000000;i++){
			switch(i){
			
			}
		}
		System.out.println("integer switch execute time:"+(System.currentTimeMillis()-t));
		int rt = REQUEST_METHOD.GET;
		t = System.currentTimeMillis();
		for(int i = 0;i<100000000;i++){
			switch(rt){
		  	case REQUEST_METHOD.DELETE:
	        	break;
	        case REQUEST_METHOD.GET:
	        	break;
	        case REQUEST_METHOD.POST:
	        	break;
	        case REQUEST_METHOD.PUT:
	        	break;
			default:
			break;
		}}
		System.out.println("integer switch execute time:"+(System.currentTimeMillis()-t));
	}
	
	public void testMap(){
		Map<String,Object> object = new LinkedHashMap<String,Object>();
		Map<String,Object> objects = new LinkedHashMap<String,Object>();
		long t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++){
			objects.put(i+"", i);
		}
		System.out.println("put :"+(System.currentTimeMillis()-t));
		for(int i = 0;i<100;i++)
			object.put(i+"", i);
		//简单迭代器类型
		t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++){
				object.get("99");
		}
		System.out.println("get :"+(System.currentTimeMillis()-t));
		
		t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++){
			Iterator<String> iterator = object.keySet().iterator();
			while(iterator.hasNext())
				iterator.next();
		}
		System.out.println("iterator key:"+(System.currentTimeMillis()-t));
		
		t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++){
			Iterator<Object> viterator = object.values().iterator();
			while(viterator.hasNext())
				viterator.next();
		}
		System.out.println("iterator value:"+(System.currentTimeMillis()-t));
		t = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++){
			Iterator<Entry<String, Object>> eiterator = object.entrySet().iterator();
			while(eiterator.hasNext())
				eiterator.next();
		}
		System.out.println("iterator entity:"+(System.currentTimeMillis()-t));
	}
}
