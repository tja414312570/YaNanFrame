package com.YaNan.frame.plugs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegisterConfigureWrapper {
	private int priority= 0;
	private boolean signlTon=true;
	private String attribute= "*";
	private File file;
	
	public RegisterConfigureWrapper(File file) throws IOException {
		super();
		this.file = file;
		InputStream in = new BufferedInputStream(new FileInputStream(file));   
		Properties p = new Properties();   
		p.load(in);
		String k = p.getProperty("priority", "0");
		System.out.println(p);
	}

	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
