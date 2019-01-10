package a.test.plugin;

import java.io.File;

public class Factory {
	private File location;
	private Factory(){};
	public static Factory newInstance(){
		return new Factory();
	}
	public void Out(String hello){
		System.out.println(location.getAbsolutePath()+"   "+hello);
	}
	public Factory newBean(){
		return new Factory();
	}
	public void init(){
		System.out.println("初始化后调用方法");
	}
}
