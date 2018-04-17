package com.YaNan.frame.core.servlet.debug;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.YaNan.frame.core.servlet.REQUEST_METHOD;

public class PerformanceTest {
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
	
	@Test
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
