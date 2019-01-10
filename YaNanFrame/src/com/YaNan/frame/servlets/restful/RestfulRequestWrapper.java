package com.YaNan.frame.servlets.restful;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * RESTFULE REQUEST PUT & DELETE PARAMETER PREREADY
 * @author yanan
 *
 */
public class RestfulRequestWrapper extends HttpServletRequestWrapper {
	protected byte[] bodyData;
	// 此参数处理器用于存储servlet api 不处理的参数
	private Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();
	// 参数名缓存
	private StringBuilder tmpName = new StringBuilder();
	// 参数值缓存
	private StringBuilder tmpValue = new StringBuilder();
	// 字符编码
	private Charset charset = StandardCharsets.UTF_8;

	public RestfulRequestWrapper(HttpServletRequest servletRequest) throws IOException {
		super(servletRequest);
		int len = servletRequest.getContentLength();
		if (len >= 0) {
			bodyData = new byte[len];
			this.readRequestBody(bodyData, len,servletRequest);
		}
		if (servletRequest.getParameterMap().size() > 0) {
			this.parameters.putAll(servletRequest.getParameterMap());
		} else if (len >= 0 && servletRequest.getContentType()!=null&&servletRequest.getContentType().toLowerCase().indexOf("boundary")==-1) {
			this.processParameters(bodyData, 0, len, charset);
		}
	}
	protected int readRequestBody(byte[] body, int len,HttpServletRequest servletRequest) throws IOException {
		int offset = 0;
		do {
			int inputLen = servletRequest.getInputStream().read(body, offset, len - offset);
			if (inputLen <= 0)
				return offset;
			offset += inputLen;
		} while ((len - offset) > 0);
		return len;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = this.parameters.get(name);
		return values;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	@Override
	public String getParameter(String name) {
		String[] values = this.parameters.get(name);
		if (values != null) {
			if (values.length == 0)
				return "";
			return values[0];
		}
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return this.parameters;
	}
	public void addParameter(String key, String value) {
		if (key == null)
			return;
		String[] values = this.parameters.get(key);
		if (values == null || values.length == 0) {
			if (value == null)
				parameters.put(key, null);
			else
				parameters.put(key, new String[] { value });
		} else {
			if (value != null) {
				String[] tmp = values;
				values = new String[values.length + 1];
				for (int i = 0; i < tmp.length; i++)
					values[i] = tmp[i];
				values[tmp.length] = value;
				parameters.put(key, values);
			}
		}
	}

	/**
	 *	read parameter
	 * 
	 * @param bytes
	 * @param start
	 * @param len
	 * @param charset
	 * @throws UnsupportedEncodingException
	 */
	private void processParameters(byte bytes[], int start, int len, Charset charset)
			throws UnsupportedEncodingException {
		int pos = start;
		int end = start + len;
		while (pos < end) {
			int nameStart = pos;
			int nameEnd = -1;
			int valueStart = -1;
			int valueEnd = -1;
			boolean parsingName = true;
			boolean decodeName = false;
			boolean decodeValue = false;
			boolean parameterComplete = false;
			do {
				switch (bytes[pos]) {
				case '=':
					if (parsingName) {
						// Name finished. Value starts from next character
						nameEnd = pos;
						parsingName = false;
						valueStart = ++pos;
					} else {
						// Equals character in value
						pos++;
					}
					break;
				case '&':
					if (parsingName) {
						// Name finished. No value.
						nameEnd = pos;
					} else {
						// Value finished
						valueEnd = pos;
					}
					parameterComplete = true;
					pos++;
					break;
				case '%':
				case '+':
					// Decoding required
					if (parsingName) {
						decodeName = true;
					} else {
						decodeValue = true;
					}
					pos++;
					break;
				default:
					pos++;
					break;
				}
			} while (!parameterComplete && pos < end);
			if (pos == end) {
				if (nameEnd == -1)
					nameEnd = pos;
				else if (valueStart > -1 && valueEnd == -1)
					valueEnd = pos;
			}
			tmpName = new StringBuilder(new String(bytes, nameStart, nameEnd - nameStart, charset));
			if (valueStart >= 0)
				tmpValue = new StringBuilder(new String(bytes, valueStart, valueEnd - valueStart, charset));
			else
				tmpValue = new StringBuilder(new String(bytes, 0, 0));
			String name;
			String value;
			try{
				if (decodeName)
					tmpName = new StringBuilder(URLDecoder.decode(tmpName.toString(), charset.toString()));
				name = tmpName.toString();
				if (valueStart >= 0) {
					if (decodeValue)
						tmpValue = new StringBuilder(URLDecoder.decode(tmpValue.toString(), charset.toString()));
					value = tmpValue.toString();
				} else
					value = "";
				addParameter(name, value);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	
	  @Override
	    public ServletInputStream getInputStream() throws IOException {
	        final ByteArrayInputStream bais = new ByteArrayInputStream(bodyData);
	        return new ServletInputStream() {
	            @Override
	            public int read() throws IOException {
	                return bais.read();
	            }
				@Override
				public boolean isFinished() {
					return false;
				}
				@Override
				public boolean isReady() {
					return false;
				}
				@Override
				public void setReadListener(ReadListener arg0) {
					
				}
	        };
	    }

	    @Override
	    public BufferedReader getReader() throws IOException {
	        return new BufferedReader(new InputStreamReader(this.getInputStream()));
	    }
}
