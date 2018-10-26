package com.YaNan.frame.path;

import java.io.File;
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
	public static List<File> getResource(String pathExpress){
		if(pathExpress==null)
			throw new RuntimeException("path express is null");
		int cpIndex = pathExpress.indexOf(CLASSPATH);
		if(cpIndex>-1)
			pathExpress= ResourceManager.class.getClassLoader().getResource("").getPath().replace("%20"," ")+pathExpress.substring(cpIndex+CLASSPATH.length());
		int index = pathExpress.indexOf("*");
		int qndex = pathExpress.indexOf("?");
		if(qndex>-1&&qndex<index)
			index = qndex;
		if(index==-1){
			File file = new File(pathExpress);
			if(!file.exists())
				throw new RuntimeException("resource \"" +pathExpress+"\" is not exists!");
			List<File> fileList = new ArrayList<File>();
			fileList.add(file);
			return fileList;
		}
		String path = pathExpress.substring(0, index);
		qndex = path.lastIndexOf("/");
		if(qndex>0)
			path = path.substring(0,qndex);
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
	public static void main(String[] args) {
		String path = "/Volumes/GENERAL/webProjects/BaTuMapping/conf/wrapper/*.xml";
		List<File> list = getResource(path);
		System.out.println(list);
	}
}
