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
		for(int i =0;i<100000000;i++)
			Math.add(i,10);
		System.out.println("excute times:"+(System.currentTimeMillis()-t3s));
		new ClassLoader(Math.class);
		long t4s = System.currentTimeMillis();
		 ClassLoader loader = new ClassLoader(Math.class);
		for(int i =0;i<100000000;i++){
			loader.getMethod("add", int.class,int.class);
		}
		System.out.println("get method times:"+(System.currentTimeMillis()-t4s));
		
		
		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			ClassLoader.createFieldGetMethod("abc");
		}
		System.out.println("create set or get times:"+(System.currentTimeMillis()-t4s));

		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			Integer.toString(100);
		}
		System.out.println("Integer.value:"+(System.currentTimeMillis()-t4s));
		
		String str;
		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			str = 100+"";
		}
		System.out.println("String ++:"+(System.currentTimeMillis()-t4s));
		
		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			String.valueOf(100);
		}
		System.out.println("String.valueOf:"+(System.currentTimeMillis()-t4s));
		
		
		
		t4s = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			loader.invokeMethod("add",i,10);
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

