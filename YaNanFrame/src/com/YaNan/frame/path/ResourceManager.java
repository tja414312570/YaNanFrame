package com.YaNan.frame.path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.YaNan.frame.path.Path.PathInter;

/**
 * 资源管理工具，获取文件
 * @author yanan
 *
 */
public class ResourceManager {
	final static String CLASSPATH = "classpath:";
	final static String PROJECT = "project:";
	public static String getPathExress(String pathExpress){
		if(pathExpress==null)
			throw new RuntimeException("path express is null");
		int cpIndex = pathExpress.indexOf(CLASSPATH);
		if(cpIndex>-1)
			pathExpress= classPath()+pathExpress.substring(cpIndex+CLASSPATH.length());
		cpIndex = pathExpress.indexOf(PROJECT);
		if(cpIndex>-1)
			try {
				pathExpress= projectPath()+pathExpress.substring(cpIndex+PROJECT.length());
			} catch (IOException e) {
				throw new RuntimeException("failed to get project director",e);
			}
		return pathExpress;
	}
	public static String projectPath() throws IOException {
		return new File("").getCanonicalPath().replace("%20"," ");
	}
	/**
	 * 通过路径表达式获取符合该路劲的所有资源
	 * @param pathExpress
	 * @return
	 */
	public static List<File> getResource(String pathExpress){
		pathExpress = getPathExress(pathExpress);
		int index = pathExpress.indexOf("*");
		int qndex = pathExpress.indexOf("?");
		if(qndex>-1&&qndex<index)
			index = qndex;
		if(index==-1){
			File file = new File(pathExpress);
			if(!file.exists())
				throw new RuntimeException("resource \"" +pathExpress+"\" is not exists! absolute:\""+file.getAbsolutePath()+"\"");
			List<File> fileList = new ArrayList<File>();
			fileList.add(file);
			return fileList;
		}
		String path = pathExpress.substring(0, index);
		qndex = path.lastIndexOf("/");
		if(qndex>0)
			path = path.substring(0,qndex);
		if(path==null||path.trim().equals(""))
			try {
				path = new File("").getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return getMatchFile(path,pathExpress);
	}
	private static List<File> getMatchFile(String pathExpress,String regex) {
		Path path = new Path(pathExpress);
		path.filter(regex);
		final List<File> fileList = new ArrayList<File>();
		path.scanner(new PathInter() {
			@Override
			public void find(File file) {
				fileList.add(file);
			}
		});
		return fileList;
	}
	public static String classPath() {
		return ResourceManager.class.getClassLoader().getResource("").getPath().replace("%20"," ");
	}
	public static String getClassPath(String name) {
		return name.replace("%20"," ").replace(ResourceManager.classPath(), "").replaceAll("/|\\\\", ".");
	}
}
