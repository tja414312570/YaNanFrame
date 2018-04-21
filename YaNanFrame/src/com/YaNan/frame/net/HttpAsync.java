package com.YaNan.frame.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpAsync extends Async<URL, Double, Boolean> {
	private int bufferSize = 1024;
	private OutputStream os;
	private NetListener netListener;
	private URL url;

	public void setNetListener(NetListener netListener) {
		this.netListener = netListener;
	}

	@Override
	public void proExecute(URL... paramas) {
		super.proExecute(paramas);
		System.out.println("网路请求开始");
	}

	@Override
	public void onExecuted(Boolean... results) {
		super.onExecuted(results[0]);
		System.out.println("网络请求完成");
	}

	public HttpAsync(URL url) {
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

	@Override
	public Boolean[] doInBackground(URL... parameters) {
		InputStream is;
		try {
			int total = getConnection().getContentLength();
			is = getInputStream();
			if (this.netListener != null)
				netListener.onResponse(this);
			byte[] byts = new byte[this.bufferSize];
			int curr = 0;
			int buffer;
			while ((buffer = is.read(byts)) != -1) {
				os.write(byts, 0, buffer);
				curr += buffer;
				if (this.netListener != null)
					netListener.Progress(curr, total);
			}
			os.close();
			is.close();
			if (this.netListener != null)
				netListener.completed();
			return new Boolean[] { true };
		} catch (IOException e) {
			e.printStackTrace();
			if (this.netListener != null)
				netListener.onError(e);
			;
			return new Boolean[] { false };
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
