package com.YaNan.frame.debug.RTDT;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class WS{
 public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
	 System.out.println(WS.class.getResource("/"));
	 URL url =  new URL("file:/Volumes/GENERAL/git/Frame/YaNanFrame/WebRoot/WEB-INF/classes/");
	 URL[] urls = {url};
	 URLClassLoader urlClassLoader = new URLClassLoader(urls);
	 Class<?> cls = urlClassLoader.loadClass("com.YaNan.frame.debug.RTDT.WS");
	 System.out.println(cls.getName());
	 WS ws = (WS) cls.newInstance();
	 System.out.println(ws.sayHello("hello"));
 }
 public String sayHello(String hello){
	 return hello+" !";
 }
}
