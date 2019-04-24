package com.YaNan.frame.servlets;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.path.PackageScanner;
import com.YaNan.frame.path.PackageScanner.ClassInter;
import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.annotations.RESPONSE_METHOD;
/**
 * 该类用来读取servlet文件或servlet配置文件
 * 
 * @author Administrator
 *
 */
public class ServletBuilder{
	private static ServletBuilder servletInstance;
	private ServletMapping servletMannager;
	private List<Document> servletPaths = new ArrayList<Document>();
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, ServletBuilder.class);
	private File classPath;

	private ServletBuilder() {
		if(WebPath.getWebPath()!=null&&WebPath.getWebPath().getClassPath()!=null){
			String servletCfg = WebPath.getWebPath().getClassPath().realPath
					+ "servlet.xml";
			if (new File(servletCfg).exists()) 
				addServletXml(servletCfg);
		}
		this.servletMannager = ServletMapping.getInstance();
		this.init();
	}

	/**
	 * 获取InitServelt类实例
	 * 
	 * @return
	 */
	public static ServletBuilder getInstance() {
		servletInstance = (servletInstance == null ? new ServletBuilder()
				: servletInstance);
		return servletInstance;
	}

	/**
	 * 添加servlet文件路径
	 * 
	 * @param xmlPath
	 */
	@SuppressWarnings("unchecked")
	public void addServletXml(String xmlPath) {
		if (new File(xmlPath).exists()) {
			try {
				Document doc= toDocument(xmlPath);
					Node root = doc.getRootElement();
					List<Element> includeNode = root.selectNodes("include");
					for (Element e : includeNode) {
						String namespace = e.attributeValue("namespace");
						namespace=(namespace.equals("/")?"":namespace);
						String file = e.getTextTrim();
						String Path = WebPath.getWebPath().getClassPath().realPath+namespace+file;
						if (new File(Path).exists()) {
							addServletXml(Path);
						} else {
							log.error("could not find servlet defalut configure file '"+Path+"',mabye some system function is not work at this framework");
						}
					}
				servletPaths.add(doc);
			} catch (DocumentException e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
			}
		} else {
			log.error("could not find servlet xml file at xml path : "
					+ xmlPath);
		}
	}

	private Document toDocument(String xmlPath) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(xmlPath);
	}
	public void initByScanner(){
		if(this.classPath==null)
			this.classPath = new File(ResourceManager.classPath());
		PackageScanner scanner = new PackageScanner();
		scanner.setClassPath(classPath.getPath());
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				try{
					Method[] methods = cls.getMethods();
					methodIterator:		for(Method method :methods){
											List<ServletDispatcher> sds = PlugsFactory.getPlugsInstanceList(ServletDispatcher.class);
											for(ServletDispatcher sd : sds){
												Class<? extends Annotation>[] annosType = sd.getDispatcherAnnotation();
												for(Class<?  extends Annotation> annoType :annosType){
													if(annosType!=null&&method.getAnnotation(annoType)!=null)
														if(sd.getBuilder().builder(annoType,method.getAnnotation(annoType),cls,method,servletMannager))
															continue methodIterator;
												}
											}
									}
				}catch (Exception e){
					log.error("An error occurs when scanning class "+cls.getName(), e);
				}
				
			}
		});
	}
	/**
	 * 此方法为了兼容之前版本
	 * 初始化servelt文件的内容并与对应的javaBean，生成对应的servlet Action
	 */
	@SuppressWarnings("unchecked")
	public void init() {
		this.initByScanner();
		Iterator<Document> i = servletPaths.iterator();
		while (i.hasNext()) {
			Node root = i.next().getRootElement();
			List<Element> packNode = root.selectNodes("package");
			for (Element e : packNode) {
				final String nameSpace = e.attributeValue("namespace");
					List<Element> servletNode = e.selectNodes("servlet");
scan_elements:		for (Element s : servletNode) {
						String name = s.attributeValue("name");
						String childNameSpace = s.attributeValue("namespace");
						String decode = e.attributeValue("decode");
						String namespace =(nameSpace.trim().equals("*")&&childNameSpace!=null?"":nameSpace)
								+(childNameSpace==null ? ""
								: (nameSpace.trim().equals("*")&&childNameSpace!=null?"":"/") + childNameSpace);
						String className = s.attributeValue("class");
						String methodName = s.attributeValue("method");
						String outputStream = s.attributeValue("outputStream");
						String autoValue = s.attributeValue("auto");
						boolean auto = (autoValue !=null&&autoValue.equals("false")?false:true);
						List<Element> resultNode = s.selectNodes("result");
						if (className != null) {
							ServletBean bean = new ServletBean();
							Class<?> cls;
							try {
								cls = Class.forName(className);
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
								log.error("servlet ["
										+ name
										+ "] is incorrect ,because the class is not found,please check class ["+className+"]");
								continue scan_elements;
							}
							Method method;
							try {
								if(methodName==null)
									if (auto) {
										methodName = "execute";
										log.warn("servlet ["
												+ name
												+ "] set method [execute], at className ["
												+ className+"]");
									} else{
										log.error("servlet ["
												+ name
												+ "] is incorrect ,because the Method ["
												+methodName
												+"] is not exists,please check class:"
												+className);
										continue scan_elements;
									}
								method = cls.getDeclaredMethod(methodName);
							} catch (NoSuchMethodException | SecurityException e1) {
								e1.printStackTrace();
								log.error("servlet ["
										+ name
										+ "] is incorrect ,because the Method ["
										+methodName
										+"] is incorrect,please check class:"
										+className);
								continue scan_elements;
							}
							if (method != null) {
								bean.setMethod(method);
							} else {
								log.error("servlet ["
										+ name
										+ "] is incorrect ,because the Method ["
										+methodName
										+"] is incorrect,mabye it not exists or parameters is not null,please check class:"
										+className);
								continue scan_elements;
							}
							bean.setServletClass(cls);
							if (outputStream != null
									&& outputStream.equals("true"))
								bean.setOutputStream(true);
							if(decode!=null)
								bean.setDecode(true);
							if (!resultNode.isEmpty()) {
								for (Element result : resultNode) {
									if (result.attributeValue("name") != null
											&& result.getTextTrim() != null) {
										ServletResult resultObj =new ServletResult();
										resultObj.setName(namespace+result.attributeValue("name"));
										resultObj.setValue(result.getTextTrim());
										String rm =result.attributeValue("method");
										if(rm!=null)
											if(rm.equals("output"))
											resultObj.setMethod(RESPONSE_METHOD.OUTPUT);
											else if(rm.equals("redirect"))
											resultObj.setMethod(RESPONSE_METHOD.REDIRCET);
										bean.addResult(resultObj);
									} else {
										log.error("Result : name or value is not exist!! at servlet.xml/../result When servletName="
												+ name);
									}
								}
							}
							//this.servletMannager.add(name, bean);
						} else {
							log.error("Class:"
									+ className
									+ " not exist!! at servlet.xml/../servlet When servletName="
									+ name);
						}
					}
			}
		}

	}
}
