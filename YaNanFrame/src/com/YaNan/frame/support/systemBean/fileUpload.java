package com.YaNan.frame.support.systemBean;

import java.io.File;
import java.util.ArrayList;

import com.YaNan.frame.core.servletSupport.DefaultServlet;
import com.YaNan.frame.core.servletSupport.ExpandUpload;
import com.YaNan.frame.hibernate.Path;

public class fileUpload extends DefaultServlet implements ExpandUpload {
	public String CKEditorFuncNum;
	private Path uploadDir;
	public File upload;

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String up() {
		return "";
	}

	public String getCKEditorFuncNum() {
		return CKEditorFuncNum;
	}

	public void setCKEditorFuncNum(String cKEditorFuncNum) {
		CKEditorFuncNum = cKEditorFuncNum;
	}

	@Override
	public Object uploadCompleted(ArrayList<File> fileList) {
		String callback = "<script type=\"text/javascript\">"
				+ "window.parent.CKEDITOR.tools.callFunction("
				+ CKEditorFuncNum + ",'http://"
				+ RequestContext.getServerName() + ":"
				+ RequestContext.getServerPort()
				+ RequestContext.getContextPath() + uploadDir.path
				+ upload.getName() + "',''" + ")" + "</script>";
		return callback.replace("\\", "/");
	}

	@Override
	public Object uploadInit(Path uploadDir) {
		this.uploadDir = uploadDir;
		return "";
	}
}
