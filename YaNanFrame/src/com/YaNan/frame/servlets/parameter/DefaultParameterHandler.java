package com.YaNan.frame.servlets.parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.ParameterHandlerCache;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.servlets.annotations.Groups;
import com.YaNan.frame.servlets.annotations.restful.ParameterType;
import com.YaNan.frame.servlets.parameter.annotations.CookieValue;
import com.YaNan.frame.servlets.parameter.annotations.PathVariable;
import com.YaNan.frame.servlets.parameter.annotations.RequestBody;
import com.YaNan.frame.servlets.parameter.annotations.RequestHeader;
import com.YaNan.frame.servlets.parameter.annotations.RequestParam;
import com.YaNan.frame.servlets.parameter.annotations.SessionAttributes;
import com.YaNan.frame.servlets.parameter.annotations.UUID;

/**
 * 2018-7-15 重新修改parseBaseType代码，将包装类型和原始类型分开
 * 2018-7-9 重新修改获取参数逻辑
 * 默认的参数调配器，优先级最低
 * 该调配器匹配任何类型的数据，但实际使用中可能存在某些数据不能处理
 * 支持put，delete参数处理
 * 支持int long char等基本类型
 * 支持String，数组等
 * 支持UUID Date标签
 * 支持普通pojo
 * 不支持文件上传，二进制数据
 * 
 * @author yanan
 *
 */
@Register(attribute = "*", signlTon = false,priority=Integer.MAX_VALUE)
public class DefaultParameterHandler implements ParameterHandler {
	// 路径变量
	private Map<String, String> pathParameter;
	// 获取PathVariables 值的迭代器
	private Iterator<String> pathIterator;
	// 请求参数集合的枚举
	private Iterator<Entry<String, String[]>> entrySetiterator;
	// 获取session attribute 名称迭代器
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	private ServletBean servletBean;
	private ParameterHandlerCache parameterHandlerCache;
	//字符编码
	private Charset charset = StandardCharsets.UTF_8;
	//数据读取缓存  设置为8k
	protected byte[] bodyData = new byte[8192];
	//此参数处理器用于存储servlet api 不处理的参数
	private Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();
	//参数名缓存
	private StringBuilder tmpName = new StringBuilder();
	//参数值缓存
	private StringBuilder tmpValue = new StringBuilder();
	private Log log = PlugsFactory.getPlugsInstance(Log.class,this.getClass());
	
	protected int readRequestBody(byte[] body, int len) throws IOException {
		int offset = 0;
		do {
			int inputLen = servletRequest.getInputStream().read(body, offset, len - offset);
			if (inputLen <= 0)
				return offset;
			offset += inputLen;
		} while ((len - offset) > 0);
		return len;
	}

	public String[] getParameterValues(String name) {
		String[] values = this.parameters.get(name);
		return values;
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	public String getParameter(String name) {
		String[] values = this.parameters.get(name);
		if (values != null) {
			if (values.length == 0)
				return "";
			return values[0];
		}
		return null;
	}

	public Map<String, String[]> getParameterMap() {
		return this.parameters;
	}

	public void addParameter(String key, String value){
		if (key == null)return;
		String[] values = this.parameters.get(key);
		if(values==null||values.length==0){
			if (value == null) 
				parameters.put(key, null);
			else
				parameters.put(key, new String[]{value});
		}else{
			if (value != null) {
				String[] tmp = values;
				values = new String[values.length+1];
				for(int i = 0;i<tmp.length;i++)
					values[i] = tmp[i];
				values[tmp.length] = value;
				parameters.put(key, values);
			}
		}
	}

	/**
	 * pug get等方式参数处理
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
			if (decodeName)
				tmpName = new StringBuilder(URLDecoder.decode(tmpName.toString(), charset.toString()));
			name = tmpName.toString();
			if (valueStart >= 0) {
				if (decodeValue)
					tmpValue = new StringBuilder(URLDecoder.decode(tmpValue.toString(), charset.toString()));
				value = tmpValue.toString();
			} else value = "";
			addParameter(name, value);
		}

	}
	public synchronized void parseParameter(){
		try {
			//当上下文长度大于0时，处理上下文内容，maybe，如果实际开发中不需要参数时 也会解析，获取可以放到 getParameter中，加个锁更好
			//偶尔想想这样可能存在bug，被恶意发送大量数据到服务器
			int len = this.servletRequest.getContentLength();
			if (len >= 0) {
				this.readRequestBody(bodyData, len);
				this.processParameters(bodyData, 0, len, charset);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	@Override
	public void initHandler(HttpServletRequest request, HttpServletResponse response, ServletBean servletBean,
			ParameterHandlerCache parameterHandlerCache) {
		this.pathParameter = this.getPathValues(servletBean.getPathVariable(), URLSupport.getRelativePath(request));
		if(pathParameter!=null)
			pathIterator = pathParameter.values().iterator();
		this.servletBean = servletBean;
		this.servletRequest = request;
		this.servletResponse = response;
		this.parameterHandlerCache = parameterHandlerCache;
		if(request.getParameterMap().size()>0)
			this.parameters = request.getParameterMap();
		else
			this.parseParameter();
		entrySetiterator = this.parameters.entrySet().iterator();

	}

	/**
	 * 获取路径中的变量 需要传入一个对变量的描述的集合 返回根据路径获取的变量后的集合 该集合存储类型 变量名==》变量值
	 * 
	 * @param pathVariables
	 * @param path
	 * @return
	 */
	public Map<String, String> getPathValues(final Map<Integer, String> pathVariables, String path) {
		if(pathVariables==null)
			return null;
		Map<String, String> pathValues = new LinkedHashMap<String, String>();
		int Index = path.indexOf("/");
		int count = 0;
		while (Index >= 0) {
			count++;
			String pvi = pathVariables.get(count);
			int Endex = path.indexOf("/", Index + 1);
			if (Endex < 0) {
				if (pvi != null)
					pathValues.put(pvi, path.substring(Index + 1));
				break;
			}
			if (pvi != null)
				pathValues.put(pvi, path.substring(Index + 1, Endex));
			Index = Endex;
		}
		return pathValues;
	}

	@Override
	public Object getParameter(Parameter paras, Annotation parameterAnnotation) {
		try {
			// 获取注解的类型
			Class<? extends Annotation> annoType = parameterAnnotation.annotationType();
			// sessionAttribute的特殊性，提前处理
			if (annoType.equals(SessionAttributes.class))
				return this.sessionAttributes((SessionAttributes) parameterAnnotation, paras);
			// 判断参数是否为基本类型
			if (isBaseType(paras.getType())) {
				if (annoType.equals(CookieValue.class))
					return this.cookieValue((CookieValue) parameterAnnotation, paras);
				else if (annoType.equals(PathVariable.class))
					return this.pathVariable((PathVariable) parameterAnnotation, paras);
				else if (annoType.equals(RequestBody.class))
					return this.requestBody((RequestBody) parameterAnnotation, paras);
				else if (annoType.equals(RequestHeader.class))
					return this.requestHeader((RequestHeader) parameterAnnotation, paras);
				else if (annoType.equals(RequestParam.class))
					return this.requestParam((RequestParam) parameterAnnotation, paras);
				else if (annoType.equals(UUID.class))
					return this.uuid((UUID) parameterAnnotation, paras);
				else if (annoType.equals(com.YaNan.frame.servlets.parameter.annotations.Date.class))
					return this.date((com.YaNan.frame.servlets.parameter.annotations.Date) parameterAnnotation, paras);
				else if (annoType.equals(com.YaNan.frame.plugin.annotations.Service.class))
					return this.service((com.YaNan.frame.plugin.annotations.Service) parameterAnnotation, paras);
				else
					return null;// 不支持的的类型
			} else {
				// 处理特殊参数 如 pojo session cookie model request response write
				// header
				if (paras.getType().equals(HttpServletRequest.class))// request
					return this.servletRequest;
				else if (paras.getType().equals(HttpServletResponse.class))// response
					return this.servletResponse;
				else if (paras.getType().equals(PrintWriter.class) || paras.getType().equals(Writer.class))// writer
					return this.servletResponse.getWriter();
				else if (paras.getType().equals(InputStream.class))// inputStream
					return this.servletRequest.getInputStream();
				else if (paras.getType().equals(OutputStream.class))// OutputStream
					return this.servletResponse.getOutputStream();
				else if (paras.getType().equals(HttpSession.class))// session
					return this.servletRequest.getSession();
				else if (paras.getType().equals(Cookie[].class))// cookies
					return this.servletRequest.getCookies();
				else if (paras.getType().equals(Locale.class))// Locale
					return this.servletRequest.getLocale();
				else {
					// pojo 类型 异常部分可能需要处理
					/**
					 * 第二个参数需要处理
					 */
					return this.pojoParameterBind(paras.getType(), null, servletRequest, servletResponse, servletBean,
							pathParameter);
					// ;
				}
			}
		} catch (Exception e) {
			log.error("An error occurred while processing the parameters,\r\nClass :"+servletBean.getServletClass().getName()
					+",\r\nMehod :"+servletBean.getMethod().getName()
					+",\r\nParamter:"+paras.getName(),e);
		}
		return null;
	}

	private Object service(com.YaNan.frame.plugin.annotations.Service parameterAnnotation, Parameter paras) {
		return PlugsFactory.getPlugsInstance(paras.getType());
	}

	/**
	 * parameter 部分
	 */
	private Object date(com.YaNan.frame.servlets.parameter.annotations.Date parameterAnnotation, Parameter paras)
			throws ParseException {
		return parseBaseType(paras.getType(), new SimpleDateFormat(parameterAnnotation.format()).format(new Date()),
				null);
	}

	private Object uuid(UUID parameterAnnotation, Parameter paras) throws ParseException {
		String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
		int endIndex = parameterAnnotation.endIndex();
		if (parameterAnnotation.value() == -1)
			endIndex = parameterAnnotation.value();
		return parseBaseType(paras.getType(), uuid.substring(parameterAnnotation.beginIndex(), endIndex), null);
	}

	private Object pathVariable(PathVariable parameterAnnotation, Parameter paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		String value = pathParameter.get(parameterName);
		if(value!=null){
			return parseBaseType(paras.getType(), value, null);
		}else if(parameterAnnotation.defaultValue().equals("")){
			return parseBaseType(paras.getType(), null, null);
		}else{
			return parseBaseType(paras.getType(), parameterAnnotation.defaultValue(), null);
		}
	}

	private Object requestBody(RequestBody parameterAnnotation, Parameter paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		if(paras.getType().isArray() ){
			return parseBaseTypes(paras.getType(), this.getParameterValues(parameterName), null);
		}else{
			String para = this.getParameter(parameterName);
			if(para!=null){
				return parseBaseType(paras.getType(), para,null);
			}else if(parameterAnnotation.defaultValue().equals("")){
				return parseBaseType(paras.getType(), null, null);
			}else{
				return parseBaseType(paras.getType(),parameterAnnotation.defaultValue(),null);
			}
		}
	}

	private Object requestHeader(RequestHeader parameterAnnotation, Parameter paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		String header = this.servletRequest.getHeader(parameterName);
		if(header!=null){
			return parseBaseType(paras.getType(), this.servletRequest.getHeader(parameterName), null);
		}else if(parameterAnnotation.defaultValue().equals("")){
			return parseBaseType(paras.getType(), null, null);
		}else{
			return parseBaseType(paras.getType(), parameterAnnotation.defaultValue(), null);
		}
	}

	private Object requestParam(RequestParam parameterAnnotation, Parameter paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
		return paras.getType().isArray()
				? parseBaseTypes(paras.getType(), this.getParameterValues(parameterName), null)
				:this.getParameter(parameterName)!=null?parseBaseType(paras.getType(), this.getParameter(parameterName), null):
					parameterAnnotation.defaultValue().equals("")?parseBaseType(paras.getType(),null, null):parseBaseType(paras.getType(),parameterAnnotation.defaultValue(), null);
	}

	private Object sessionAttributes(SessionAttributes parameterAnnotation, Parameter paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		return servletRequest.getSession().getAttribute(parameterName);
	}

	private Object cookieValue(CookieValue anno, Parameter paras) throws ParseException {
		String parameterName = anno.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		for (Cookie cookie : servletRequest.getCookies()) {
			if (cookie.getName().equals(parameterName))
				return parseBaseType(paras.getType(), cookie.getValue(), null);
		}
		return parseBaseType(paras.getType(), null, null);
	}

	/**
	 * field部分
	 */
	private Object sessionAttributes(SessionAttributes parameterAnnotation, Field paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		return servletRequest.getSession().getAttribute(parameterName);
	}

	private Object date(com.YaNan.frame.servlets.parameter.annotations.Date parameterAnnotation, Field paras)
			throws ParseException, IOException {
		if (this.matchGroups(this.servletBean.getMethod().getAnnotation(Groups.class), parameterAnnotation.groups())
				|| this.matchGroups(this.servletBean.getServletClass().getAnnotation(Groups.class),
						parameterAnnotation.groups()))
			return parseBaseType(paras.getType(), new SimpleDateFormat(parameterAnnotation.format()).format(new Date()),
					null);
		return this.getParameter(paras);
	}

	private Object uuid(UUID parameterAnnotation, Field paras) throws ParseException, IOException {
		if (this.matchGroups(this.servletBean.getMethod().getAnnotation(Groups.class), parameterAnnotation.groups())
				|| this.matchGroups(this.servletBean.getServletClass().getAnnotation(Groups.class),
						parameterAnnotation.groups())) {
			String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
			int endIndex = parameterAnnotation.endIndex();
			if (parameterAnnotation.value() >= 0)
				endIndex = parameterAnnotation.value();
			if (endIndex < 0)
				endIndex = uuid.length();
			return parseBaseType(paras.getType(), uuid.substring(parameterAnnotation.beginIndex(), endIndex), null);
		}
		return this.getParameter(paras);

	}
	private boolean matchGroups(Groups groups, Class<?>[] groups2) {
		if (groups2.length == 0)
			return true;
		if (groups == null)
			return false;
		Class<?>[] groups1 = groups.value();
		for (Class<?> ogc : groups1)
			for (Class<?> ngc : groups2)
				if (ogc.equals(ngc))
					return true;
		return false;
	}

	private Object pathVariable(PathVariable parameterAnnotation, Field paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		String value = pathParameter.get(parameterName);
		if(value!=null){
			return parseBaseType(paras.getType(), value, null);
		}else if(parameterAnnotation.defaultValue().equals("")){
			return parseBaseType(paras.getType(), null, null);
		}else{
			return parseBaseType(paras.getType(), parameterAnnotation.defaultValue(), null);
		}
	}

	private Object requestBody(RequestBody parameterAnnotation, Field paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		if(paras.getType().isArray() ){
			return parseBaseTypes(paras.getType(), this.getParameterValues(parameterName), null);
		}else{
			String para = this.getParameter(parameterName);
			if(para!=null){
				return parseBaseType(paras.getType(), para,null);
			}else if(parameterAnnotation.defaultValue().equals("")){
				return parseBaseType(paras.getType(), null, null);
			}else{
				return parseBaseType(paras.getType(),parameterAnnotation.defaultValue(),null);
			}
		}
	}

	private Object requestHeader(RequestHeader parameterAnnotation, Field paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		String header = this.servletRequest.getHeader(parameterName);
		if(header!=null){
			return parseBaseType(paras.getType(), this.servletRequest.getHeader(parameterName), null);
		}else if(parameterAnnotation.defaultValue().equals("")){
			return parseBaseType(paras.getType(), null, null);
		}else{
			return parseBaseType(paras.getType(), parameterAnnotation.defaultValue(), null);
		}
	}

	private Object requestParam(RequestParam parameterAnnotation, Field paras) throws ParseException {
		String parameterName = parameterAnnotation.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		return paras.getType().isArray()
				? parseBaseTypes(paras.getType(), this.getParameterValues(parameterName), null)
						:this.getParameter(parameterName)!=null?parseBaseType(paras.getType(), this.getParameter(parameterName), null):
							parameterAnnotation.defaultValue().equals("")?null:parseBaseType(paras.getType(), parameterAnnotation.defaultValue(), null);
	}

	private Object cookieValue(CookieValue anno, Field paras) throws ParseException {
		String parameterName = anno.value();// 从注解中获取参数名
		if (parameterName.trim().equals(""))
			parameterName = paras.getName();// 此方法只适合 jdk 1.8 以上，并要求编译器编译为
											// 存储方法参数 否则可能出现 arg0 arg1等编译后修改的参数名
		for (Cookie cookie : servletRequest.getCookies()) {
			if (cookie.getName().equals(parameterName))
				return parseBaseType(paras.getType(), cookie.getValue(), null);
		}
		return parseBaseType(paras.getType(), null, null);
	}

	@Override
	public Object getParameter(Parameter paras) throws IOException {
		if (isBaseType(paras.getType())) {
			try {
				// 如按循序获取参数 只支持 pathVariable 与 requestParameter
				// 如果没有的话会造成HttpServletResponse.SC_INTERNAL_SERVER_ERROR
				if (pathIterator!=null&&pathIterator.hasNext())// 判断pathValues的迭代器是否有下一个值，如果有则赋值
					return parseBaseType(paras.getType(), pathIterator.next(), null);
				else if (entrySetiterator.hasNext()) {// 判断requestParameter参数中是否有该参数
					Entry<String, String[]> entry = entrySetiterator.next();
					return parseBaseTypes(paras.getType(), entry.getValue().length == 1 && entry.getValue()[0].equals("")
							? new String[] { entry.getKey() } : entry.getValue(), null);
				}
				return parseBaseType(paras.getType(), null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 处理特殊参数 如 pojo session cookie model request response write header
			if (paras.getType().equals(HttpServletRequest.class))// request
				return this.servletRequest;
			else if (paras.getType().equals(HttpServletResponse.class))// response
				return this.servletResponse;
			else if (paras.getType().equals(PrintWriter.class) || paras.getType().equals(Writer.class))// writer
				return this.servletResponse.getWriter();
			else if (paras.getType().equals(InputStream.class))// inputStream
				return this.servletRequest.getInputStream();
			else if (paras.getType().equals(OutputStream.class))// OutputStream
				return this.servletResponse.getOutputStream();
			else if (paras.getType().equals(HttpSession.class))// session
				return this.servletRequest.getSession();
			else if (paras.getType().equals(Cookie[].class))// cookies
				return this.servletRequest.getCookies();
			else if (paras.getType().equals(Locale.class))// Locale
				return this.servletRequest.getLocale();
			else {
				// pojo 类型 异常部分可能需要处理
				try {
					return this.pojoParameterBind(paras.getType(), null, servletRequest, servletResponse, servletBean,
							pathParameter);
				} catch (Exception e) {
					log.error("An error occurred while processing the parameters,\r\nClass :"+servletBean.getServletClass().getName()
							+",\r\nMehod :"+servletBean.getMethod().getName()
							+",\r\nParamter:"+paras.getName(),e);
				}
				// return
				// this.pojoParameterBind(paras.getType(),paramEntry.getValue().getName(),
				// request, response, servletBean, pathParameter);
			}
		}
		return null;
	}

	/**
	 * 提供简单pojo支持 className.field形式 例如 student.name 不提供数组的实现 student.age
	 * 
	 * @param pojoClass
	 * @param path
	 * @param request
	 * @param response
	 * @param servletBean
	 * @param pathParameter
	 * @return
	 * @throws Exception
	 */
	protected Object pojoParameterBind(Class<?> pojoClass, String path, HttpServletRequest request,
			HttpServletResponse response, ServletBean servletBean, Map<String, String> pathParameter) throws Exception {
		// 对pojo类进行ClassLoader的包装，ClassLoader会在内部产生一个pojo类的实例
		ClassLoader loader = new ClassLoader(pojoClass);
		// 获取所有的field
		Field[] fields = loader.getDeclaredFields();
		for (Field field : fields) {
			if (response.isCommitted())
				return null;
			// 获取到fieldName 并组成新的名称
			List<Annotation> annos = PlugsFactory.getAnnotationGroup(field, ParameterType.class);// .get(ParameterType.class);
			if (annos != null && annos.size() != 0) {
				Annotation parameterAnnotation = annos.get(0);
				ParameterHandler parameterHandler = parameterHandlerCache.getParameterHandler(parameterAnnotation);
				Object value = parameterHandler.getParameter(field, parameterAnnotation);
				if (value != null) {
					String fieldSetMethod = ClassLoader.createFieldSetMethod(field);
					if (loader.hasDeclaredMethod(fieldSetMethod, field.getType()))
						loader.invokeMethod(fieldSetMethod, value);
					else
						loader.setFieldValue(field, value);
				}
				continue;
			}
			ParameterHandler parameterHandler = parameterHandlerCache.getParameterHandler(field.getType());
			Object value = parameterHandler.getParameter(field);
			if (value != null) {
				String fieldSetMethod = ClassLoader.createFieldSetMethod(field);
				if (loader.hasDeclaredMethod(fieldSetMethod, field.getType()))
					loader.invokeMethod(fieldSetMethod, value);
				else
					loader.setFieldValue(field, value);
			}
		}
		return loader.getLoadedObject();
	}

	/**
	 * 判断类是否为可支持的基本类型
	 * 
	 * @param clzz
	 * @return
	 */
	protected static boolean isBaseType(Class<?> clzz) {
		if (clzz.equals(String.class))
			return true;
		if (clzz.equals(boolean.class))
			return true;
		if (clzz.equals(int.class))
			return true;
		if (clzz.equals(float.class))
			return true;
		if (clzz.equals(byte.class))
			return true;
		if (clzz.equals(short.class))
			return true;
		if (clzz.equals(long.class))
			return true;
		if (clzz.equals(double.class))
			return true;
		if (clzz.equals(char.class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Byte.class))
			return true;
		if (clzz.equals(Short.class))
			return true;
		if (clzz.equals(Integer.class))
			return true;
		if (clzz.equals(Long.class))
			return true;
		if (clzz.equals(Float.class))
			return true;
		if (clzz.equals(Double.class))
			return true;
		if (clzz.equals(Boolean.class))
			return true;
		if (clzz.equals(Character.class))
			return true;
		// 日期
		if (clzz.equals(Date.class))
			return true;

		// 以上所有类型的数组类型
		if (clzz.equals(String[].class))
			return true;
		if (clzz.equals(boolean[].class))
			return true;
		if (clzz.equals(int[].class))
			return true;
		if (clzz.equals(float[].class))
			return true;
		if (clzz.equals(byte[].class))
			return true;
		if (clzz.equals(short[].class))
			return true;
		if (clzz.equals(long[].class))
			return true;
		if (clzz.equals(double[].class))
			return true;
		if (clzz.equals(char[].class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Short[].class))
			return true;
		if (clzz.equals(Integer[].class))
			return true;
		if (clzz.equals(Long[].class))
			return true;
		if (clzz.equals(Float[].class))
			return true;
		if (clzz.equals(Double[].class))
			return true;
		if (clzz.equals(Boolean[].class))
			return true;
		if (clzz.equals(Character[].class))
			return true;
		// 日期
		if (clzz.equals(Date[].class))
			return true;

		return false;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseBaseTypes(Class<?> clzz, String[] arg, String format) throws ParseException {
		if (!clzz.isArray())
			return parseBaseType(clzz, arg[0], format);
		if (clzz.equals(String[].class))
			return arg;
		Object[] args = new Object[arg.length];
		for (int i = 0; i < args.length; i++)
			args[i] = parseBaseType(clzz, arg[i], format);
		return args;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseBaseType(Class<?> clzz, String arg, String format) throws ParseException {
		// 匹配时应该考虑优先级 比如常用的String int boolean应该放在前面 其实 包装类型应该分开
		if (clzz.equals(String.class))
			return arg;
		// 8个基本数据类型及其包装类型
		if (clzz.equals(int.class))
			return arg==null?0:Integer.parseInt(arg);
		if(clzz.equals(Integer.class))
			return arg==null?null:Integer.valueOf(arg);
		
		if (clzz.equals(boolean.class))
			return arg==null?false:Boolean.parseBoolean(arg);
		if(clzz.equals(Boolean.class))
			return arg==null?null:Boolean.valueOf(arg);
		
		if (clzz.equals(float.class))
			return arg==null?0.0f:Float.parseFloat(arg);
		if(clzz.equals(Float.class))
			return arg==null?null:Float.valueOf(arg);
		
		if (clzz.equals(short.class))
			return arg==null?0:Short.parseShort(arg);
		if( clzz.equals(Short.class))
			return arg==null?null:Short.valueOf(arg);
					
		if (clzz.equals(long.class))
			return arg==null?0l:Long.parseLong(arg);
		if(clzz.equals(Long.class))
			return arg==null?null:Long.valueOf(arg);
		
		if (clzz.equals(double.class) )
			return arg==null?0.0f:Double.parseDouble(arg);
		if(clzz.equals(Double.class))
			return arg==null?null:Double.valueOf(arg);
		
		if (clzz.equals(char.class) )
			return arg==null?null:arg.charAt(0);
		if(clzz.equals(Character.class))
			return arg==null?null:Character.valueOf(arg.charAt(0));
		
		if (clzz.equals(char[].class))
			return arg==null?null:arg.toCharArray();
		
		if (clzz.equals(byte.class)||clzz.equals(Byte.class))
			return arg==null?null:Byte.parseByte(arg);
		// 日期
		if (clzz.equals(Date.class))
			return format == null ? DATE_FORMAT.parse(arg) : new SimpleDateFormat(format).parse(arg);
		return arg;
	}

	/**
	 * 将sessionAttribute 转化为目标类型
	 * 
	 * @param clzz
	 * @param arg
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	protected static Object parseSessionAttribute(Class<?> clzz, Object arg, String format) throws ParseException {
		if (isBaseType(clzz))
			return parseBaseType(clzz, arg.toString(), format);
		return arg;
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

	@Override
	public Object getParameter(Field field, Annotation parameterAnnotation) {
		try {
			// 获取注解的类型
			Class<? extends Annotation> annoType = parameterAnnotation.annotationType();
			// sessionAttribute的特殊性，提前处理
			if (annoType.equals(SessionAttributes.class))
				return this.sessionAttributes((SessionAttributes) parameterAnnotation, field);
			// 判断参数是否为基本类型
			if (isBaseType(field.getType())) {
				if (annoType.equals(CookieValue.class))
					return this.cookieValue((CookieValue) parameterAnnotation, field);
				else if (annoType.equals(PathVariable.class))
					return this.pathVariable((PathVariable) parameterAnnotation, field);
				else if (annoType.equals(RequestBody.class))
					return this.requestBody((RequestBody) parameterAnnotation, field);
				else if (annoType.equals(RequestHeader.class))
					return this.requestHeader((RequestHeader) parameterAnnotation, field);
				else if (annoType.equals(RequestParam.class))
					return this.requestParam((RequestParam) parameterAnnotation, field);
				else if (annoType.equals(UUID.class))
					return this.uuid((UUID) parameterAnnotation, field);
				else if (annoType.equals(com.YaNan.frame.servlets.parameter.annotations.Date.class))
					return this.date((com.YaNan.frame.servlets.parameter.annotations.Date) parameterAnnotation, field);
				else
					return null;// 不支持的的类型
			} else {
				// 处理特殊参数 如 pojo session cookie model request response write
				// header
				if (field.getType().equals(HttpServletRequest.class))// request
					return this.servletRequest;
				else if (field.getType().equals(HttpServletResponse.class))// response
					return this.servletResponse;
				else if (field.getType().equals(PrintWriter.class) || field.getType().equals(Writer.class))// writer
					return this.servletResponse.getWriter();
				else if (field.getType().equals(InputStream.class))// inputStream
					return this.servletRequest.getInputStream();
				else if (field.getType().equals(OutputStream.class))// OutputStream
					return this.servletResponse.getOutputStream();
				else if (field.getType().equals(HttpSession.class))// session
					return this.servletRequest.getSession();
				else if (field.getType().equals(Cookie[].class))// cookies
					return this.servletRequest.getCookies();
				else if (field.getType().equals(Locale.class))// Locale
					return this.servletRequest.getLocale();
				else {
					// pojo 类型 异常部分可能需要处理
					/**
					 * 第二个参数需要处理
					 */
					return this.pojoParameterBind(field.getType(), null, servletRequest, servletResponse, servletBean,
							pathParameter);
					// ;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getParameter(Field field) throws IOException {
		if (isBaseType(field.getType())) {
			try {
				String name = field.getName();
				// 如按循序获取参数 只支持 pathVariable 与 requestParameter
				// 如果没有的话会造成HttpServletResponse.SC_INTERNAL_SERVER_ERROR
				String value = null;
				if(pathParameter!=null)
					value = pathParameter.get(name);
				if (value != null)
					return parseBaseType(field.getType(), value, null);
				if (field.getType().isArray()) {
					String[] values = servletRequest.getParameterValues(name);
					if (values != null)
						return parseBaseTypes(field.getType(), values, null);
					values = this.getParameterValues(name);
					if (values != null)
						return parseBaseTypes(field.getType(), values, null);
				} else {
					value = servletRequest.getParameter(name);
					if (value != null)
						return parseBaseType(field.getType(), value, null);
					value = this.getParameter(name);
					if (value != null)
						return parseBaseType(field.getType(), value, null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 处理特殊参数 如 pojo session cookie model request response write header
			if (field.getType().equals(HttpServletRequest.class))// request
				return this.servletRequest;
			else if (field.getType().equals(HttpServletResponse.class))// response
				return this.servletResponse;
			else if (field.getType().equals(PrintWriter.class) || field.getType().equals(Writer.class))// writer
				return this.servletResponse.getWriter();
			else if (field.getType().equals(InputStream.class))// inputStream
				return this.servletRequest.getInputStream();
			else if (field.getType().equals(OutputStream.class))// OutputStream
				return this.servletResponse.getOutputStream();
			else if (field.getType().equals(HttpSession.class))// session
				return this.servletRequest.getSession();
			else if (field.getType().equals(Cookie[].class))// cookies
				return this.servletRequest.getCookies();
			else if (field.getType().equals(Locale.class))// Locale
				return this.servletRequest.getLocale();
			else {
				// pojo 类型 异常部分可能需要处理
				try {
					return this.pojoParameterBind(field.getType(), null, servletRequest, servletResponse, servletBean,
							pathParameter);
				} catch (Exception e) {
					log.error("An error occurred while processing the parameters at "+field,e);
				}
				// return
				// this.pojoParameterBind(paras.getType(),paramEntry.getValue().getName(),
				// request, response, servletBean, pathParameter);
			}
		}
		return null;
	}

}
