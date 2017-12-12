package com.YaNan.frame.support.systemBean;

import java.io.File;

import com.YaNan.frame.core.servletSupport.DefaultServlet;

public class DesUpload extends DefaultServlet{
	private File file;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
//	public String upload(){
//		if(type.trim().equals("private")){
//			DH.setKeyFile(this.file,DH.TYPE.private_key);
//			return DH.checkKey();
//		}else{
//			if(type.trim().equals("public"))
//				DH.setKeyFile(this.file,DH.TYPE.public_key);
//		}
//	}

}
