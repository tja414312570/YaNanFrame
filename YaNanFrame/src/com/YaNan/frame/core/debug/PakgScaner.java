package com.YaNan.frame.core.debug;

import java.io.File;
import java.lang.reflect.Method;

import com.YaNan.frame.Native.Path;
import com.YaNan.frame.Native.Path.PathInter;
import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.ActionResults;
import com.YaNan.frame.core.servlet.annotations.ActionResults.Result;

public class PakgScaner {
	public static void main(String[] args) {
		String node = "com.action";
		System.out.println(node.contains("."));
		node = node.replace(".", "/");
		System.out.println(node);
		String classPath = "WebRoot/WEB-INF/classes/";
		File sourceDir = new File(classPath);
		File files = new File(classPath+"/"+node);
		System.out.println("file path:"+files.getAbsolutePath());
		Path scanner = new Path(files.getAbsolutePath());
		scanner.scanner(new PathInter(){
			@Override
			public void find(File file) {
				System.out.println("file:"+file.getAbsolutePath());
				if(file.getName().length()>6&&file.getName().contains(".class")){
					String className = file.getAbsolutePath().replace(sourceDir.getAbsolutePath(), "").replace("\\","." ).replace("/", ".").replace("$", ".").replace(".java", ".class");
					className= className.substring(0, 1).equals(".")?className.substring(1, className.length()):className;
					try {
						Class<?> cls = Class.forName(className);
						Method[] methods = cls.getMethods();
						for(Method method :methods){
							Action action = method.getAnnotation(Action.class);
							if(action!=null){
								System.out.println(action.value());
								ActionResults results = method.getAnnotation(ActionResults.class);
								for(Result result : results.value()){
									System.out.println(result.name()+result.value());
								}
							}
							
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		});
	}
}
