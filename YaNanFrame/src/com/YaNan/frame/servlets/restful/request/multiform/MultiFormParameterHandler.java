package com.YaNan.frame.servlets.restful.request.multiform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.ParameterHandlerCache;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.parameter.ParameterHandler;

/**
 * 多媒体表单参数处理器
 * 
 * @author yanan
 *
 */
@Register(attribute = { "java.io.File", "org.apache.commons.fileupload.FileItem" }, signlTon = false)
public class MultiFormParameterHandler implements ParameterHandler {
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	private ServletBean servletBean;
	private Logger log = LoggerFactory.getLogger( this.getClass());
	private List<FileItem> fileItemList;

	@Override
	public void initHandler(HttpServletRequest request, HttpServletResponse response, ServletBean servletBean,
			ParameterHandlerCache parameterHandlerCache) {
		this.servletBean = servletBean;
		this.servletRequest = request;
		this.servletResponse = response;
		this.MultiFormSupport(request);
	}

	/**
	 * read parameter
	 * 
	 * @param bytes
	 * @param start
	 * @param len
	 * @param charset
	 * @throws FileUploadException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	public void MultiFormSupport(HttpServletRequest servletRequest) {
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
			List<FileItem> lists;
			try {
				lists = sfUpload.parseRequest(servletRequest);
				Iterator<FileItem> iterator = lists.iterator();
				while (iterator.hasNext()) {
					FileItem fileItem = iterator.next();
					if (!fileItem.isFormField()) {
						if (fileItemList == null)
							fileItemList = new ArrayList<FileItem>(4);
						fileItemList.add(fileItem);
						// 文件域处理
					}
				}
			} catch (FileUploadException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	@Override
	public Object getParameter(Parameter parameter, Annotation parameterAnnotation) {
		return null;
	}

	@Override
	public Object getParameter(Parameter parameter) throws IOException {
		if (fileItemList == null || fileItemList.size() == 0)
			return null;
		if (parameter.getType().equals(FileItem.class)) {
			if (parameter.getType().isArray()) {
				FileItem[] fileItems = new FileItem[fileItemList.size()];
				for (int i = 0; i < fileItemList.size(); i++) {
					fileItems[i] = fileItemList.get(i);
				}
				return fileItems;
			} else {
				return fileItemList.get(0);
			}
		}
		String tmpDir = System.getProperty("java.io.tmpdir") + File.separator;
		if (!parameter.getType().isArray()) {
			FileItem fileItem = fileItemList.get(0);
			File file = new File(tmpDir, fileItem.getName());
			return this.doUpload(file, fileItem.getInputStream());
		} else {
			File[] files = new File[fileItemList.size()];
			FileItem fileItem = null;
			for (int i = 0; i < fileItemList.size(); fileItem = fileItemList.get(i), i++) {
				files[i] = new File(tmpDir, fileItem.getName());
				this.doUpload(files[i], fileItem.getInputStream());
			}
			return files;
		}
	}

	@Override
	public Object getParameter(Field field, Annotation parameterAnnotation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParameter(Field field) throws IOException {
		if (fileItemList == null || fileItemList.size() == 0)
			return null;
		if (field.getType().equals(FileItem.class)) {
			if (field.getType().isArray()) {
				FileItem[] fileItems = new FileItem[fileItemList.size()];
				for (int i = 0; i < fileItemList.size(); i++) {
					fileItems[i] = fileItemList.get(i);
				}
				return fileItems;
			} else {
				return fileItemList.get(0);
			}
		}
		String tmpDir = System.getProperty("java.io.tmpdir") + File.separator;
		if (!field.getType().isArray()) {
			FileItem fileItem = fileItemList.get(0);
			File file = new File(tmpDir, fileItem.getName());
			return this.doUpload(file, fileItem.getInputStream());
		} else {
			File[] files = new File[fileItemList.size()];
			FileItem fileItem = null;
			for (int i = 0; i < fileItemList.size(); fileItem = fileItemList.get(i), i++) {
				files[i] = new File(tmpDir, fileItem.getName());
				this.doUpload(files[i], fileItem.getInputStream());
			}
			return files;
		}
	}

	/**
	 * 普通文件上传
	 * 
	 * @param fileTmp
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public File doUpload(File fileTmp, InputStream inputStream) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileTmp);
		byte[] fileBuffer = new byte[1024];
		int line;
		while ((line = inputStream.read(fileBuffer)) != -1) {
			fos.write(fileBuffer, 0, line);
		}
		fos.flush();
		fos.close();
		inputStream.close();
		return fileTmp;
	}

	@Override
	public ServletBean getServletBean() {
		return this.servletBean;
	}

	@Override
	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}

	@Override
	public HttpServletResponse getServletResponse() {
		// TODO Auto-generated method stub
		return this.servletResponse;
	}
}
