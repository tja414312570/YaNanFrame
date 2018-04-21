package com.YaNan.frame.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpPost {
	public static class Method {
		public final static String GET = "GET";
		public final static String POST = "POST";
	}

	public static class RequestHeader {
		public final static String ACCEPT = "Accepet";
		public final static String ACCEPT_ENCODING = "Accept-Encoding";
		public final static String ACCEPT_LANGUAG = "Accept-Language";
		public final static String CONNECTION = "Connection";
		public final static String CONTENT_LENGTH = "Content-Length";
		public final static String CONTENT_TYPE = "Content-Type";
		public final static String COOKIE = "Cookie";
		public final static String HOST = "HclientOutputStreamt";
		public final static String ORIGIN = "Origin";
		public final static String REFERER = "Referer";
		public final static String USER_AGENT = "User-Agent";

		public static class ContentType {
			// public final static String
		}
	}

	private String encoding = "UTF-8";
	private int bufferSize = 1024;
	private Map<String, Object> requestHeader = new LinkedHashMap<String, Object>();
	private List<String> requestParams = new ArrayList<String>();
	private static HttpURLConnection httpURLConnection;
	private OutputStream clientOutputStream;
	private InputStream clientInputStream;
	private URL url;
	private boolean doInput = true;
	private boolean doOutput = true;

	public HttpPost(URL url) {
		this.url = url;
	}

	public HttpURLConnection getConnection() throws IOException {
		return (HttpURLConnection) this.url.openConnection();
	}

	public HttpURLConnection openConnection() throws IOException {
		if (httpURLConnection == null) {
			httpURLConnection = getConnection();
			httpURLConnection.addRequestProperty("encoding", encoding);
			httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
			if (this.requestHeader.size() != 0) {
				Iterator<String> iterator = this.requestHeader.keySet()
						.iterator();
				httpURLConnection.addRequestProperty(iterator + "",
						this.requestHeader.get(iterator) + "");
			}

			httpURLConnection.setDoInput(doInput);
			httpURLConnection.setDoOutput(doOutput);
			httpURLConnection.setRequestMethod(Method.POST);
		}
		return httpURLConnection;
	}

	public void addRequestProperty(String name, Object object) {
		this.requestHeader.put(name, object);
	}

	public InputStream getInputStream() throws IOException {
		return this.openConnection().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return this.openConnection().getOutputStream();
	}

	public void setOutputStream(OutputStream outputStream) throws IOException {
		this.clientOutputStream = outputStream;
	}

	public void setInputStream(InputStream inputStream) throws IOException {
		this.clientInputStream = inputStream;
	}

	public void addRequstParameter(String name, String value) {
		this.requestParams.add(name + "=" + value);
	}

	public void addRequestParameter(String params) {
		this.requestParams.add(params);
	}

	public void setFile(File file) throws FileNotFoundException, IOException {
		setOutputStream(new FileOutputStream(file));
	}

	public String getRequestParameter() {
		String parameter = "";
		if (this.requestParams.size() != 0) {
			Iterator<String> iterator = this.requestParams.iterator();
			while (iterator.hasNext()) {
				parameter += iterator.next() + (iterator.hasNext() ? "&" : "");
			}
		}
		return parameter;
	}

	public boolean get() {

		try {
			OutputStream netOutputStreamt = getOutputStream();
			netOutputStreamt.write(getRequestParameter().getBytes());
			netOutputStreamt.flush();
			if (clientInputStream != null) {
				byte[] byts = new byte[this.bufferSize];
				int buffer;
				while ((buffer = clientInputStream.read(byts)) != -1)
					netOutputStreamt.write(byts, 0, buffer);
				netOutputStreamt.flush();
			}
			if (netOutputStreamt != null)
				netOutputStreamt.close();
			if (clientOutputStream != null) {
				InputStream get = getInputStream();
				byte[] byts = new byte[this.bufferSize];
				int buffer;
				while ((buffer = get.read(byts)) != -1)
					clientOutputStream.write(byts, 0, buffer);
				clientOutputStream.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}
