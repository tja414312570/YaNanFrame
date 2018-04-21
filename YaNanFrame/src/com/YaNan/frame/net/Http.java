package com.YaNan.frame.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Http {
	private int bufferSize = 1024;
	private OutputStream os;
	private URL url;

	public Http(URL url) {
		this.url = url;
	}

	public URLConnection getConnection() throws IOException {
		return url.openConnection();
	}

	public InputStream getInputStream() throws IOException {
		return getConnection().getInputStream();
	}

	public void setOutputStream(OutputStream outputStream) throws IOException {
		this.os = outputStream;
	}

	public void setFile(File file) throws FileNotFoundException, IOException {
		setOutputStream(new FileOutputStream(file));
	}

	public boolean get() {
		InputStream is;
		try {
			is = getInputStream();
			byte[] byts = new byte[this.bufferSize];
			int buffer;
			while ((buffer = is.read(byts)) != -1)
				os.write(byts, 0, buffer);
			os.close();
			is.close();
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
