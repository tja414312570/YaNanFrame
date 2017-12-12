package com.YaNan.frame.Native;

import java.io.File;
import com.YaNan.frame.Native.Path.PathInter;

public class PackageScanner {
	private String packageName;
	private String classPath;
	private File sourcePath;
	private File packagePath;
	public PackageScanner(String packageName, String classPath) {
		this.packageName = packageName;
		this.classPath = classPath;
	}
	public PackageScanner(){
		
	}
	public void doScanner(final ClassInter inter) {
		if(this.packageName==null)this.packageName=".";
		String packagePath = this.packageName.replace(".", "/");
		this.sourcePath= new File(classPath);//资源目录
		this.packagePath= new File(classPath+"/"+packagePath);//包路径
		Path scanner = new Path(this.packagePath.getAbsolutePath());
		scanner.scanner(new PathInter(){
			@Override
			public void find(File file) {
				if(file.getName().length()>6&&file.getName().contains(".class")){
					String className = file.getAbsolutePath().replace(sourcePath.getAbsolutePath(), "").replace("\\","." ).replace("/", ".").replace("$", ".").replace(".class", "");
					className= className.substring(0, 1).equals(".")?className.substring(1, className.length()):className;
					try {
						Class<?> cls = Class.forName(className);
						inter.find(cls);
					} catch (ClassNotFoundException e) {
					}
				}
				
			}
		});
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public File getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(File sourcePath) {
		this.sourcePath = sourcePath;
	}
	public File getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(File packagePath) {
		this.packagePath = packagePath;
	}
	public static interface ClassInter {
		public void find(Class<?> cls);
	}
}
