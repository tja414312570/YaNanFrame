package com.YaNan.Demo.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.YaNan.frame.Native.Path;
import com.YaNan.frame.Native.Path.PathInter;
import com.YaNan.frame.Native.PathScanner;

import sun.reflect.generics.scope.ClassScope;

import java.util.Properties;

public class TestLoadJar {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
//		Map<String, String> map = System.getenv(); 
//		Iterator<Entry<String, String>> it = map.entrySet().iterator(); 
//		while(it.hasNext()) 
//		{ 
//		  Entry entry = (Entry)it.next(); 
//		  System.out.print(entry.getKey()+"="); 
//		  System.out.println(entry.getValue()); 
//		} 
//		
//		Properties properties = System.getProperties(); 
//		Iterator i = properties.entrySet().iterator(); 
//		while(i.hasNext()) 
//		{ 
//		  Entry entry = (Entry)i.next(); 
//		  System.out.print(entry.getKey()+"="); 
//		  System.out.println(entry.getValue()); 
//		} 
		
		Path scanner = new Path("/Applications/MyEclipse 2017 CI/MyEclipse 2017 CI.app/Contents/Profile/binary/com.sun.java.jdk8.macosx.x86_64_1.8.0.v112/jre/");
		scanner.filter(".jar");
		scanner.scanner(new PathInter(){
			@Override
			public void find(File file) {
				System.out.println(file);
			}
		});
		
//		File file=new File("/Volumes/GENERAL/tomcat work groups/apache-tomcat-8.0.46/bin.jar");//jar包的路径  
//		URL url=file.toURI().toURL();  
//        URLClassLoader loader=new URLClassLoader(new URL[]{url});//创建类加载器  
//        Class<?> cls=loader.loadClass("org.apache.commons.lang3.StringUtils");//加载指定类，注意一定要带上类的包名  
//        Method method=cls.getMethod("center",String.class,int.class,String.class);//方法名和对应的各个参数的类型  
//        Object o=method.invoke(null,"chen",Integer.valueOf(10),"0");//调用得到的上边的方法method(静态方法，第一个参数可以为null)  
//        System.out.println(String.valueOf(o));//输出"000chen000","chen"字符串两边各加3个"0"字符串  
//        loader.close();
	}
}
