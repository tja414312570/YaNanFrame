package com.YaNan.frame.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.typesafe.config.Config;

public class ConfigContext {
	private final static ConfigContext context = new ConfigContext();
	private Map<String,Config> configMap;
	private Map<String,Config> objectMap;
	public static void addConf(Config config){
		Iterator<Entry<String, Config>> confIterator = config.configEntrySet().iterator();
		while(confIterator.hasNext()){
			Entry<String, Config> entry = confIterator.next();
			context.addConfig(entry.getKey(),config);
		}
		Iterator<Entry<String, Object>> objectIterator = config.simpleObjectEntrySet().iterator();
		while(objectIterator.hasNext()){
			Entry<String, Object> entry = objectIterator.next();
			context.addObject(entry.getKey(),config);
		}
	}
	private void addConfig(String key, Config value) {
		if(configMap==null){
			synchronized (this) {
				if(configMap==null)
					configMap = new HashMap<String,Config>();
			}
		}
		if(!configMap.containsKey(key))
			configMap.put(key, value);
	}
	private void addObject(String key, Config value) {
		if(objectMap==null){
			synchronized (this) {
				if(objectMap==null)
					objectMap = new HashMap<String,Config>();
			}
		}
		if(!objectMap.containsKey(key))
			objectMap.put(key, value);
	}
	public static Config getConfig(String key){
		if(context.configMap==null)
			return null;
		Config config = context.configMap.get(key);
		if(config==null)
			return null;
		return config.getConfig(key);
	}
	public static String getString(String key){
		return getString(key,null);
	}
	public static String getString(String key,String defaultValue){
		if(context.objectMap==null)
			return defaultValue;
		Config config = context.objectMap.get(key);
		if(config==null)
			return defaultValue;
		return config.getString(key,defaultValue);
	}
	public static boolean getBoolean(String key){
		return getBoolean(key,false);
	}
	public static boolean getBoolean(String key,boolean defaultValue){
		if(context.objectMap==null)
			return defaultValue;
		Config config = context.objectMap.get(key);
		if(config==null)
			return defaultValue;
		return config.getBoolean(key,defaultValue);
	}
	public static int getInt(String key){
		return getInt(key,0);
	}
	public static int getInt(String key,int defaultValue){
		if(context.objectMap==null)
			return defaultValue;
		Config config = context.objectMap.get(key);
		if(config==null)
			return defaultValue;
		return config.getInt(key,defaultValue);
	}
	public static long getLong(String key){
		return getLong(key,0l);
	}
	public static long getLong(String key,long defaultValue){
		if(context.objectMap==null)
			return defaultValue;
		Config config = context.objectMap.get(key);
		if(config==null)
			return defaultValue;
		return config.getLong(key,defaultValue);
	}
	public static double getDouble(String key){
		return getDouble(key,0d);
	}
	public static double getDouble(String key,double defaultValue){
		if(context.objectMap==null)
			return defaultValue;
		Config config = context.objectMap.get(key);
		if(config==null)
			return defaultValue;
		return config.getDouble(key,defaultValue);
	}
}
