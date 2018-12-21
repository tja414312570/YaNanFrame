package com.YaNan.frame.servlets.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.reflect.cache.MethodHelper;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.exception.ServletRuntimeException;
import com.YaNan.frame.servlets.response.annotations.ResponseResource;

import sun.misc.BASE64Encoder;
/**
 * 支持文件的输出功能
 * @author yanan
 *
 */
@Register(attribute = {"java.io.File","com.YaNan.frame.servlets.response.annotations.ResponseResource"})
public class FileResponseHandler implements ResponseHandler {

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,
			Annotation responseAnnotation, ServletBean servletBean) throws ServletException, IOException {
		if (handlerResult.getClass().equals(File.class)) {
			ResourceAttr resource = this.wrapperResource((File) handlerResult,servletBean);
			this.write(request, response, resource);
		} else if (handlerResult.getClass().equals(ResourceAttr.class)) {
			this.write(request, response, (ResourceAttr)handlerResult);
		}

	}
	private ResourceAttr wrapperResource(File handlerResult, ServletBean servletBean) {
		if(handlerResult==null)
			throw new RuntimeException("resource file is null!");
		ResourceAttr attr = new ResourceAttr(handlerResult);
		//获取注解
		ResponseResource anno = MethodHelper.getMethodHelper(servletBean.getMethod())
				.getAnnotation(ResponseResource.class);
		if(anno!=null){
			attr.setBuffer(anno.buffer());
			attr.setEnableBCT(anno.enableBCT());
			attr.setAttachment(anno.attachment());
			if(!anno.fileName().equals("")){
				attr.setFileName(anno.fileName());
			}
		}
		return attr;
	}
	/**
	 * 写入响应
	 * @param request
	 * @param response
	 * @param file
	 * @throws IOException 
	 */
	public void write(HttpServletRequest request, HttpServletResponse response, ResourceAttr resource) throws IOException {
		if(resource.getFile()==null)
			throw new RuntimeException("resource file is null!");
		if(!resource.getFile().exists())
			throw new ServletRuntimeException(404,"resource file \""+resource.getFile().getName()+"\" is not exists!");
		if(resource.getFile().isDirectory())
			throw new RuntimeException("resource file \""+resource.getFile().getName()+"\" is a directory!");
		FileInputStream fileInputStream = new FileInputStream(resource.getFile());
		//use utf-8 encoding
		String filename = new String(resource.getFileName().getBytes("ISO8859-1"), "UTF-8");
		String agent = request.getHeader("User-Agent");
		String filenameEncoder = "";
		long fileLength = resource.getFile().length();
		if (agent.contains("MSIE")) {
			// IE
			filenameEncoder = URLEncoder.encode(filename, "utf-8");
			filenameEncoder = filenameEncoder.replace("+", " ");
		} else if (agent.contains("Firefox")) {
			// if is firefox 
			BASE64Encoder base64Encoder = new BASE64Encoder();
			filenameEncoder = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
		} else {
			// other browser
			filenameEncoder = URLEncoder.encode(filename, "utf-8");
		}
		//set response mime type
		response.setContentType(Files.probeContentType(Paths.get(resource.getFile().getName())));
		//set response content length
		response.setContentLengthLong(fileLength);
		//response as download
		response.setHeader("Content-Disposition", "attachment;filename=" + filenameEncoder);
		// if enable break point continue translate
		if(resource.enableBCT){
			String range = request.getHeader("Range");
			if (range != null) {
				long pos = Long.parseLong(range.replaceAll("bytes=", "").replaceAll("-", ""));
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				if (pos != 0) {
					String contentRange = new StringBuffer("bytes ").append(new Long(pos).toString()).append("-")
							.append(new Long(fileLength - 1).toString()).append("/").append(new Long(fileLength).toString())
							.toString();
					response.setHeader("Content-Range", contentRange);
					fileInputStream.skip(pos);
				}
			}
		}
		
		ServletOutputStream fos = response.getOutputStream();
		byte[] bytes = new byte[resource.getBuffer()];
		int len = 0;
		while (true) {
			len = fileInputStream.read(bytes);
			if (len == -1)
				break;
			fos.write(bytes, 0, len);
		}
		if (fileInputStream != null) {
			fileInputStream.close();
		}
		if (fos != null) {
			fos.close();
		}
	}
	/**
	 * 资源描述
	 * @author yanan
	 *
	 */
	public static class ResourceAttr {
		//buffer大小，默认2048
		private int buffer = 2048;
		private File file;
		//文件名
		private String fileName;
		//是否支持断点续传
		private boolean enableBCT = true;
		//是否下载文件
		private boolean attachment = true;
		
		public ResourceAttr(File file) {
			this.file = file;
			this.fileName = file.getName();
		}
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public boolean isEnableBCT() {
			return enableBCT;
		}
		public void setEnableBCT(boolean enableBCT) {
			this.enableBCT = enableBCT;
		}
		public int getBuffer() {
			return buffer;
		}
		public void setBuffer(int buffer) {
			this.buffer = buffer;
		}
		public boolean isAttachment() {
			return attachment;
		}
		public void setAttachment(boolean attachment) {
			this.attachment = attachment;
		}
	}

}
