package com.YaNan.frame.servlets;

import java.util.HashMap;
import java.util.Map;

public class ResourceCache {
	private static Map<Integer,Object> resourceCache = new HashMap<Integer,Object>();
	public void putResource(int key,Object value){
		resourceCache.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getResource(int key){
		return (T) resourceCache.get(key);
	}
	public static void putResource(Object value,String...strings){
		if(strings.length==0)
			return ;
		resourceCache.put(hash(strings), value);
	}
	public static int hash(String...objects){
		int hash=0;
		for(int i =0;i<objects.length;i++)
			hash +=objects[i].hashCode();
		return hash;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getResource(String...strings){
		if(strings.length==0)
			return null;
		return (T) resourceCache.get(hash(strings));
	}
}
