package com.YaNan.frame.debug.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.reflect.cache.CacheContainer;
import com.YaNan.frame.reflect.cache.ClassInfoCache;

public class ClassLoaderDebug {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		long t1s = System.currentTimeMillis();
		for(int i = 0;i<1000000;i++)
			new Math();
		System.out.println(System.currentTimeMillis()-t1s);
		new ClassLoader(Math.class);
		long t2s = System.currentTimeMillis();
		for(int i=0;i<1000000;i++)
			new ClassLoader(Math.class);
		System.out.println(System.currentTimeMillis()-t2s);
		long t3s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++)
			Math.add(i,10);
		System.out.println("native excute times:"+(System.currentTimeMillis()-t3s));
		new ClassLoader(Math.class);
		Math math = new Math();
		ClassLoader loader = new ClassLoader(Math.class);
		Class<?>[] clss = new Class<?>[]{ int.class,int.class};
		ClassInfoCache cache = CacheContainer.getClassCache(Math.class);
		long t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			loader.invokeMethod(math,"add",clss ,i,10);
		}
		System.out.println("class loader invoke:"+(System.currentTimeMillis()-t4s));
		System.out.println("class loader QPS:"+100000000/(System.currentTimeMillis()-t4s));
		//获取对象
		
		//获取方法
		Method method = loader.getMethod("add", int.class,int.class);
		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			method.invoke(math, i,10);
		}
		System.out.println("reflect invoke:"+(System.currentTimeMillis()-t4s));
		
	}
	public static class Math{
		String str;
		public Math(){
			this.str="hello";
		}
		public static int add(int a,int b){
			return a+b;
		}
	}
}

