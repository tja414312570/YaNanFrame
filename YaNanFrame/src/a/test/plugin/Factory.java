package a.test.plugin;

import java.io.File;

import com.YaNan.frame.plugin.PlugsFactory;

public class Factory {
	private File location;
	int i;
	private Factory(){};
	public Factory(String key,int id){
		this.location = new File(key);
	}
	public Factory(String key){
		this.location = new File(key);
	}
	public Factory(File key){
		this.location = key;
	}
	public static Factory newInstance(){
		return new Factory();
	}
	public void Out(String hello){
		System.out.println(location.getAbsolutePath()+"   "+hello+"   "+ location.exists());
	}
	public Factory newBean(){
		return new Factory();
	}
	@Override
	public String toString() {
		return "Factory [location=" + location + "]";
	}
	public void init(){
		System.out.println((i++)+"初始化后调用方法"+this.location);
	}
	public void init(String key,int id){
		System.out.println((i++)+"初始化后调用方法key:"+key+"   id:"+id);
	}
	public void init2(int id,String key){
		System.out.println((i++)+"初始化后调用方法id:"+id+"   key:"+key);
	}
}
