package com.YaNan.frame.core.reflect.debug;

import java.lang.reflect.InvocationTargetException;

import com.YaNan.frame.core.reflect.ClassLoader;

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
		for(int i =0;i<1000000;i++)
			Math.add(i,10);
		System.out.println("excute times:"+(System.currentTimeMillis()-t3s));
		new ClassLoader(Math.class);
		long t4s = System.currentTimeMillis();
		for(int i =0;i<1000000;i++){
			new ClassLoader(Math.class).invokeMethod("add",i,10);
//			Method method = ClassInfoCache.getClassInfoCache(Math.class).getMethod("add", parameterTypes);
//			Object object = method.invoke(null,10,10);
		}
		
		//	
		System.out.println("excute times:"+(System.currentTimeMillis()-t4s));
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

