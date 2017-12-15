package com.YaNan.frame.core.servlet;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.YaNan.frame.Native.PackageScanner;
import com.YaNan.frame.Native.PackageScanner.ClassInter;
import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.ActionResults;
import com.YaNan.frame.core.servlet.annotations.NameSpaces;
import com.YaNan.frame.core.servlet.annotations.RESPONSE_METHOD;
import com.YaNan.frame.core.servlet.annotations.ActionResults.Result;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.service.Log;

/**
 * 该类用来读取servlet文件或servlet配置文件
 * 
 * @author Administrator
 *
 */
public class InitServlet {
	private static InitServlet servletInstance;
	private defaultServletMapping servletMannager;
	private List<Document> servletPaths = new ArrayList<Document>();
	private Log log = Log.getSystemLog();
	private File classPath;

	private InitServlet() {
		if(WebPath.getWebPath()!=null&&WebPath.getWebPath().getClassPath()!=null){
			String servletCfg = WebPath.getWebPath().getClassPath().realPath
					+ "servlet.xml";
			if (new File(servletCfg).exists()) 
				addServletXml(servletCfg);
		}
		this.servletMannager = defaultServletMapping.getInstance();
		this.init();
	}

	/**
	 * 获取InitServelt类实例
	 * 
	 * @return
	 */
	public static InitServlet getInstance() {
		servletInstance = (servletInstance == null ? new InitServlet()
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
				log.exception(e);
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
		if(this.classPath==null){
			this.classPath = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "));
		}
		PackageScanner scanner = new PackageScanner();
		scanner.setClassPath(classPath.getPath());
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				NameSpaces nsa = cls.getAnnotation(NameSpaces.class);
				String nameSpace = "*";
				if(nsa!=null)
					nameSpace=nsa.value();
				Method[] methods = cls.getMethods();
				for(Method method :methods){
					Action action = method.getAnnotation(Action.class);
					if(action!=null){
						ServletBean bean = new ServletBean();
						if (method.getParameterCount()==0) {
							bean.setMethod(method);
						} else {
							try {
								throw new Exception("the Parameters at action method ["
										+ method
										+ "] should be null,please check class:["+cls.getName()+"]");
							} catch (Exception e) {
								e.printStackTrace();
								e.printStackTrace();
								log.write("the Parameters at action method ["
										+ method
										+ "] should be null,please check class:["+cls.getName()+"]");
							}
							continue;
							//log.error();
						}
						String namespace  = action.namespace();
						while(nameSpace.substring(0, 1).equals("/"))
							nameSpace=nameSpace.substring(1);
						while(nameSpace.substring(nameSpace.length()-1,nameSpace.length()).equals("/"))
							nameSpace=nameSpace.substring(0,nameSpace.length()-1);
						while(namespace.substring(0, 1).equals("/"))
							namespace=namespace.substring(1);
						while(namespace.substring(namespace.length()-1,namespace.length()).equals("/"))
							namespace=namespace.substring(0,namespace.length()-1);
						namespace =(nameSpace.equals("*")&&namespace!=null?"":nameSpace)
								+(action.namespace().equals("*") ? ""
								: (nameSpace.trim().equals("*")&&namespace!=null?"":"/") + namespace);
						if(namespace.trim().contentEquals(""))
							namespace="*";
						if(!namespace.equals("*")&&namespace.length()>1){
							namespace=(namespace.substring(0,1).equals("/")?"":"/")+namespace+(namespace.substring(namespace.length()-1,namespace.length()).equals("/")?"":"/");
						}
						String actionName = action.value().equals("")?method.getName():action.value();
						if (servletMannager.asExist(namespace ,actionName)) {
							try {
								throw new Exception("servelt [" + actionName
										+ "] is exists at namespace [" +namespace
										+ "] please check class:'"+cls.getName());
							} catch (Exception e) {
								e.printStackTrace();
								log.write("servelt [" + actionName
								+ "] is exists at namespace [" + namespace
								+ "] please check class:'"+cls.getName());
							}
							continue;
							}
						bean.setArgs(action.args());
						bean.setClassName(cls);
						bean.setNameSpace(namespace);
						bean.setOutputStream(action.output());
						bean.setDecode(action.decode());
						bean.setCorssOrgin(action.CorssOrgin());
						bean.setType(action.method());
						Result[] results =method.getAnnotationsByType(Result.class);
						if(results.length==0){
							ActionResults actionResults = method.getAnnotation(ActionResults.class);
							if(actionResults!=null)results = actionResults.value();
						}
						for (Result result : results) {
							ServletResult resultObj =new ServletResult();
							resultObj.setName(result.name());
							resultObj.setValue(result.value());
							resultObj.setMethod(result.method());
							bean.addResult(resultObj);
						}
						servletMannager.add(actionName,bean);
					}
				}
			}
		});
	}
	/**
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
						if (this.servletMannager.asExist(namespace,name )) {
							log.write("servelt [" + name
									+ "] is exists at namespace [" + namespace
									+ "]");
							continue scan_elements;
						}
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
										log.write("servlet ["
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
							bean.setClassName(cls);
							if (namespace != null)
								bean.setNameSpace(namespace);
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
										resultObj.setName(result.attributeValue("name"));
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
							this.servletMannager.add(name, bean);
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
