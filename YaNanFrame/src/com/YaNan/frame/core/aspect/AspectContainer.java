package com.YaNan.frame.core.aspect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.Native.PackageScanner;
import com.YaNan.frame.Native.PackageScanner.ClassInter;
import com.YaNan.frame.core.aspect.annotations.Aspect;
import com.YaNan.frame.core.aspect.interfaces.AspectInterface;
import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.stringSupport.StringSupport;

public class AspectContainer {
	private File classPath;
	private Map<String,List<AspectInterface>> aspectPools = new HashMap<String,List<AspectInterface>>();
	private static AspectContainer container;
	public static AspectContainer getAspectContainer(){
		if(container==null)
			container = new AspectContainer();
		return container;
	}
	public void init(){
		if(this.classPath==null){
			this.classPath = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "));
		}
		PackageScanner scanner = new PackageScanner();
		scanner.setClassPath(classPath.getAbsolutePath());
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				Aspect aspect = cls.getAnnotation(Aspect.class);
				if(aspect!=null){
					if(ClassLoader.implementOf(cls,AspectInterface.class)){
						try {
							String[] points = aspect.point().split(",");
							for(String point : points)
							if(aspectPools.containsKey(point))
								aspectPools.get(aspect.point()).add(aspect.index(),(AspectInterface)cls.newInstance());
							else{
								List<AspectInterface>  list = new ArrayList<AspectInterface>();
								list.add(aspect.index(), (AspectInterface)cls.newInstance());
								aspectPools.put(point, list);
							}
						} catch (InstantiationException | IllegalAccessException e) {
							try {
								throw new Exception("Aspect class ["+cls.getName()+"] Could not to be instance!");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}else{
						try {
							throw new Exception("Aspect class ["+cls.getName()+"] is not implements AspectInterface!");
						} catch (Exception e) {
							e.printStackTrace();
						};
					}
				}
			}
		});
	}
	public List<AspectInterface> match(String string) {
		for(String point : aspectPools.keySet()){
			if(StringSupport.match(string,point))return aspectPools.get(point);
		}
		return null;
	}
	public File getClassPath() {
		return classPath;
	}
	public void setClassPath(File classPath) {
		this.classPath = classPath;
	}
	public  Map<String, List<AspectInterface>> getAspectPools() {
		return aspectPools;
	}
	public void setAspectPools(Map<String, List<AspectInterface>> aspectPools) {
		this.aspectPools = aspectPools;
	}
	public static AspectContainer getContainer() {
		return container;
	}
	public static void setContainer(AspectContainer container) {
		AspectContainer.container = container;
	}
}
