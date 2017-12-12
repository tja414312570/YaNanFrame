package com.YaNan.frame.Native;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class was used to operate some file it content is key-value This class
 * need import *.Native.Files
 * 
 * @author PMJN
 * @version 0000
 * @since jdk1.6+
 */
public class Properties {
	private Map<String, String> map = new HashMap<String, String>();
	private Files file;

	/* single the default construct */
	@SuppressWarnings("unused")
	private Properties() {
	}

	/**
	 * this constructor keyValue(String filePath) need a String parameter
	 * filePath; when the new instance was builded the class will auto unDecode
	 * the context of this filePath you can use some method to do something that
	 * you want do
	 * 
	 * @param filePath
	 */
	public Properties(String filePath) {
		this.file = new Files(filePath);
		this.unDecode();
	}

	public Properties(Map<String, String> kv, String filePath) {
		this.map = kv;
		this.file = new Files(filePath);
		this.update();
	}

	@Override
	public Map<String, String> clone() {
		return this.map;
	}

	public void clone(Map<String, String> kv) {
		this.map = kv;
		this.update();
	}

	public void newKeyValueFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		this.file = new Files(filePath);
		this.unDecode();
	}

	/**
	 * Method createKey(String key,String value) create an new key-value to file
	 * when the file is exists
	 * 
	 * @param key
	 * @param value
	 */
	public void creatKey(String key, String value) {
		this.map.put(key, value);
		this.update();
	}

	/**
	 * Method get(String key) get an value when key is exists if the key is null
	 * will return null
	 * 
	 * @param key
	 * @return value
	 */
	public String get(String key) {
		return this.map.get(key);
	}

	/**
	 * Method update(String key,String value) update the value when the key is
	 * exists
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, String value) {
		this.map.replace(key, value);
		this.update();
	}

	/**
	 * return Iterator
	 * 
	 * @return Iterator<String>
	 */
	public Iterator<String> iterator() {
		return this.map.keySet().iterator();
	}

	/**
	 * delete an key-value
	 * 
	 * @param key
	 */
	public void del(String key) {
		this.map.remove(key);
		this.update();
	}

	/**
	 * clear the file
	 */
	public void clear() {
		this.map.clear();
		this.update();
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

	/**
	 * return the key size
	 * 
	 * @return integer
	 */
	public int size() {
		return this.map.size();
	}

	private void unDecode() {
		String fileContent;
		try {
			fileContent = this.file.read();
			String line[] = fileContent.split("\\n");
			for (int i = 0; i < line.length; i++) {
				String block[] = line[i].split("=");
				this.map.put(block[0].trim(), block[1].trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update() {
		try {

			Iterator<String> iterator = this.map.keySet().iterator();
			String fileContext = "";
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (key != null)
					fileContext += key + "=" + this.map.get(key) + "\n";
			}
			this.file.reWrite(fileContext);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}