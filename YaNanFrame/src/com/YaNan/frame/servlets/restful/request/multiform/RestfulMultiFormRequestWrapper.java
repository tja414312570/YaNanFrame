package com.YaNan.frame.servlets.restful.request.multiform;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.YaNan.frame.servlets.restful.RestfulRequestWrapper;

/**
 * RESTFULE REQUEST MULTIFORM PARAMETER PREREADY
 * 
 * @author yanan
 * @date 20180805
 *
 */
public class RestfulMultiFormRequestWrapper extends RestfulRequestWrapper {
	public RestfulMultiFormRequestWrapper(HttpServletRequest servletRequest) throws IOException, FileUploadException {
		super(servletRequest);
		this.MultiFormSupport(servletRequest);
	}
	/**
	 * @param servletRequest
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	public void MultiFormSupport(HttpServletRequest servletRequest) throws FileUploadException {
		if (ServletFileUpload.isMultipartContent(servletRequest)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 设置内存中缓存文件大小
			factory.setSizeThreshold(4096);
			File uploadPath = new File(System.getProperty("java.io.tmpdir") + File.separator);
			if (!uploadPath.exists())
				uploadPath.mkdirs();
			// 设置缓存文件位置
			factory.setRepository(uploadPath);
			ServletFileUpload sfUpload = new ServletFileUpload(factory);
			List<FileItem> lists = sfUpload.parseRequest(servletRequest);
			Iterator<FileItem> iterator = lists.iterator();
			while (iterator.hasNext()) {
				FileItem fileItem = iterator.next();
				if (fileItem.isFormField()) {
					this.addParameter(fileItem.getFieldName(), fileItem.getString());
					continue;
					// 文件域处理
				}
			}
		}
	}

}
