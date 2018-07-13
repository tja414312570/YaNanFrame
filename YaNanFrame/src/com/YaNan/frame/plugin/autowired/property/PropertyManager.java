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
	void scanAllProperty(){
		File dir = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "));
		Path path = new Path(dir);
		path.filter(".properties");
		path.scanner(new PathInter() {
			@Override
			public void find(File file) {
				Properties properties = new Properties();
				try {
					InputStream is = new FileInputStream(file);
					properties.load(is);
					Iterator<Entry<Object, Object>> iterator = properties.entrySet().iterator();
					while(iterator.hasNext()){
						Entry<Object, Object> entry = iterator.next();
						propertyPools.put(entry.getKey().toString(), entry.getValue().toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
}
