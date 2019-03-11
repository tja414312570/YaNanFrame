package com.YaNan.frame.servlets.servletSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.ActionDispatcher;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.servletSupport.exception.SecretKeyNotExists;
import com.YaNan.frame.servlets.servletSupport.exception.UnsupportFileType;
import com.YaNan.frame.util.StringUtil;

public class MultiFormServlet extends DefaultServlet {
	public transient static String uploadDir = "Tmp";
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, MultiFormServlet.class);

	public void MultiFormSupport(ClassLoader loader, ServletBean bean) {
		try {
			if (ServletFileUpload.isMultipartContent(this.RequestContext)) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// 获取上传文件配置
				Upload uploadConf = loader.getLoadedClass().getAnnotation(Upload.class);
				if (uploadConf != null && uploadConf.MaxSize() != -1
						&& this.RequestContext.getContentLength() > uploadConf.MaxSize()) {
					// 文件总大小超过限制
					throw new Exception("The total size of the uploaded file exceeds the limit size");
				}
				if (bean.decode()) {
					if (uploadDir.contains("./"))
						uploadDir.replace("./", ".");
					if (uploadDir.contains(".\\"))
						uploadDir.replace(".\\", ".");
					if (uploadDir.subSequence(0, 1).equals("."))
						uploadDir = this.RequestContext.getServletContext().getRealPath("/") + uploadDir.substring(1);
				}
				// 设置内存中缓存文件大小
				factory.setSizeThreshold(4096);
				File uploadPath = new File(uploadDir);
				if (!uploadPath.exists())
					uploadPath.mkdirs();
				// 设置缓存文件位置
				factory.setRepository(uploadPath);
				ServletFileUpload sfUpload = new ServletFileUpload(factory);
				List<?> lists = sfUpload.parseRequest(RequestContext);
				Iterator<?> iterator = lists.iterator();
				while (iterator.hasNext()) {
					FileItem fileItem = (FileItem) iterator.next();
					if (fileItem.isFormField()) {
						// 对文本域进行赋值
						if (!ActionDispatcher.valuation(loader, fileItem.getFieldName(), fileItem.getString(),
								ResponseContext))
							return;
						// 文件域处理
					} else {
						this.isUploadField(fileItem, loader, uploadConf, uploadPath);
					}
					fileItem.delete();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				loader.invokeMethod("exception", new Class<?>[] { Exception.class }, e);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				e1.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}
	}

	public void isUploadField(FileItem fileItem, ClassLoader loader, Upload uploadConf, File uploadPath)
			throws IOException, InvalidKeyException, SecretKeyNotExists, IllegalBlockSizeException,
			BadPaddingException {
		InputStream inputStream = fileItem.getInputStream();
		// File uploadPath = new
		// File(request.getServletContext().getRealPath("/")+uploadDir);
		File fileTmp = null;
		if (uploadPath.exists())
			uploadPath.mkdirs();
		if (uploadConf != null) {
			fileTmp = this.doUpload(fileItem, inputStream, loader, uploadConf);
		} else {
			fileTmp = new File(uploadPath, fileItem.getName());
			this.doUpload(fileTmp, inputStream);
		}
		this.doFile(loader, fileItem.getFieldName(), fileTmp);
	}

	/**
	 * 普通文件上传
	 * 
	 * @param fileTmp
	 * @param inputStream
	 * @throws IOException
	 */
	public void doUpload(File fileTmp, InputStream inputStream) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileTmp);
		byte[] fileBuffer = new byte[1024];
		int line;
		while ((line = inputStream.read(fileBuffer)) != -1) {
			fos.write(fileBuffer, 0, line);
		}
		fos.flush();
		fos.close();
		inputStream.close();
	}

	/**
	 * 带配置的文件上传
	 * 
	 * @param fileTmp
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws SecretKeyNotExists
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws UnsupportFileType
	 * @throws Exception
	 */
	public File doUpload(FileItem fileItem, InputStream inputStream, ClassLoader loader, Upload uploadConf)
			throws IOException, SecretKeyNotExists, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		/**
		 * 字符串变量赋值
		 */
		uploadDir = StringUtil.decodeVar(uploadConf.Path(), loader.getLoadedObject());
		if (!new File(uploadDir).exists())
			new File(uploadDir).mkdirs();
		String fileName = fileItem.getName();
		File fileTmp = new File(uploadDir, fileName);
		int pointIndex = fileName.indexOf(".");
		String fileType = (pointIndex != -1 ? fileName.substring(pointIndex + 1) : "");
		// 不允许上传的类型
		if (!uploadConf.DeniedType().equals("")) {
			String deniedType = uploadConf.DeniedType();
			String[] types = deniedType.split(",");
			for (String dt : types) {
				if (fileType.equals(dt)) {
					new UnsupportFileType("Unsupported file upload type");
				}
			}
		}
		this.doUpload(fileTmp, inputStream);
		return fileTmp;
	}

	/**
	 * 当一个文件上传后立刻执行方法，如果多个文件，此方法多次执行，同时传入一个File 的参数
	 * 
	 * @param loader
	 * @param field
	 * @param file
	 * @return
	 */
	public void doFile(ClassLoader loader, String field, File file) {
		try {
			if (loader.hasMethod(ClassLoader.createFieldSetMethod(field), File.class)) {
				loader.set(field, file);
			} else {
				ClassLoader cLoader = new ClassLoader(loader.getLoadedClass().getSuperclass(), false);
				if (cLoader.hasMethod(ClassLoader.createFieldSetMethod(field), File.class)) {
					cLoader.set(field, file);
				}
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public void UploadException(Exception exception) {
		exception.printStackTrace();
	}

}
