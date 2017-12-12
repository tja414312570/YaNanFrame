package com.YaNan.frame.core.init;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.service.Log;

/**
 * @version 1.0.1
 * @since jdk1.8
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class InitXML {
	private static String xmlPath;
	private static Log log = Log.getSystemLog();
	private static Log.LogBuffer lb = log.getBuffer();
	final private static String nodePath = "//config/webPath/path";
	static Document document;
	static Document hDocument;
	public static void init(){
		try {
			xmlPath = WebPath.getWebPath().getClassPath().realPath
			+ "init.xml";
			SAXReader reader = new SAXReader();
			document = reader.read(xmlPath);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	public static List<?> getPath() {
		List<?> node = new ArrayList<Element>();
		if(document!=null){
		node = document.selectNodes(nodePath);
		log.write("node is null:"+(node==null));
		}
		return node;
	}

	public static Element getPathElementById(String Id) {
		Node node = document.selectSingleNode(nodePath + "[@id='" + Id + "']");
		return (Element) node;
	}

	public static Element getPathElementByName(String name) {
		Node node = document.selectSingleNode(nodePath + "[@name='" + name
				+ "']");
		return (Element) node;
	}

	public static Element getPathElementByAttr(String attr, String value) {
		Node node = document.selectSingleNode(nodePath + "[@" + attr + "='"
				+ value + "']");
		return (Element) node;
	}

	public static String getPathById(String id) {
		Node node = document.selectSingleNode(nodePath + "[@id='" + id + "']");
		String result = ((Element) node).getTextTrim();
		String[] varSign = result.split("\\$");
		if (varSign.length > 1) {
			result = "";
			for (int i = 1; i < varSign.length; i++) {
				String[] plusSign = varSign[i].split("\\+");
				String webPathBack = WebPath.getWebPath().get(plusSign[0]).originPath;
				webPathBack = (webPathBack == null ? "" : webPathBack);
				result += webPathBack
						+ (plusSign.length > 1 ? plusSign[1] : "");
			}
		}
		return result;
	}

}
