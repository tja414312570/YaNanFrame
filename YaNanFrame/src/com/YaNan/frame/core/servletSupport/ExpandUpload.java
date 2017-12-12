package com.YaNan.frame.core.servletSupport;

import java.io.File;
import java.util.ArrayList;

import com.YaNan.frame.hibernate.Path;
import com.YaNan.frame.service.ClassInfo;

/**
 * 该接口不提供session接口，如需要使用session， 请在实现类继承DefaultServlet，使用SessionContext获取
 * 
 * @author Administrator
 *
 */
@ClassInfo(version = 0)
public interface ExpandUpload {
	public Object uploadCompleted(ArrayList<File> fileList);

	public Object uploadInit(Path uploadDir);
}
