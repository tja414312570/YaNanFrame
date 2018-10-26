package com.YaNan.frame.plugin.autowired.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.YaNan.frame.path.Path;
import com.YaNan.frame.path.Path.PathInter;

public class PropertyManager {
	private static PropertyManager manager;
	private Map<String,String> propertyPools;
	private PropertyManager(){
		propertyPools  = new HashMap<String,String>();
	}
	public static PropertyManager getInstance(){
		if(manager==null)
			synchronized (PropertyManager.class) {
				if(manager == null)
					manager = new PropertyManager();
			}
		return manager;
	}
	public String getProperty(String name){
		return this.propertyPools.get(name);
	}
	public void scanAllProperty(){
		File dir = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "));
		Path path = new Path(dir);
		path.filter("**.properties");
		path.scanner(new PathInter() {
			@Override
			public void find(File file) {
				Properties properties = new Properties();
				try {
					InputStream is = new FileInputStream(file);
					properties.load(is);
					Iterator<Entry<Object, Object>> iterator = properties.entrySet().iterator();
					Entry<Object, Object> entry;
					while(iterator.hasNext()){
						entry=iterator.next();
						if(entry.getValue()!=null)
							propertyPools.put(entry.getKey().toString(),entry.getValue().toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.rebuild();
	}
	public synchronized void rebuild(){
		Map<String,String> tempProp = propertyPools;
		propertyPools =new HashMap<String,String>();
			Iterator<Entry<String,String>> iterator = tempProp.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				if(propertyPools.containsKey(entry.getKey()))
					continue;
				propertyPools.put(entry.getKey(), getValues(entry.getValue(),tempProp));
			}
	}
	private String getValues(String orginValue,Map<String,String> tempProp) {
		if(orginValue==null)
			return null;
		String tempKey;
		String tempValue;
		int index,endex = 0;
		while((index=orginValue.indexOf("${", endex))>-1
				&&(endex=orginValue.indexOf("}",index+2))>-1
				&&(tempKey=orginValue.substring(index+2, endex))!=null
				&&!tempKey.trim().equals("")){
			tempValue = propertyPools.get(tempKey);
			if(tempValue==null)
				tempValue = tempProp.get(tempKey);
			if(tempValue == null){
				endex = endex+1;
			}else{
				tempValue = getValues(tempValue,tempProp);
				propertyPools.put(tempKey, tempValue);
				orginValue = orginValue.substring(0,index)+tempValue+orginValue.substring(endex+1);
				endex = endex-tempKey.length()-2+tempValue.length();
			}
		}
		return orginValue;
	}
	
}
