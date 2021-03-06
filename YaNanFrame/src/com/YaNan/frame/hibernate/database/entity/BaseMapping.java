package com.YaNan.frame.hibernate.database.entity;

import java.util.List;

import com.YaNan.frame.utils.beans.xml.AsXml;
import com.YaNan.frame.utils.beans.xml.Attribute;
import com.YaNan.frame.utils.beans.xml.Mapping;
import com.YaNan.frame.utils.beans.xml.NodeName;
import com.YaNan.frame.utils.beans.xml.Value;
import com.YaNan.frame.utils.beans.xml.XmlFile;

public abstract class BaseMapping {
	@XmlFile
	protected String xmlFile;
	@NodeName
	protected String node;
	@Attribute
	protected String id;
	@Attribute
	protected String resultType;
	@Attribute
	protected String parameterType;
	@Value
	protected String content;
	protected WrapperMapping wrapperMapping;
	@AsXml
	protected String xml;
	@Mapping(node = "trim", target = Trim.class)
	@Mapping(node = "if", target = IF.class)
	@Mapping(node = "foreach", target = ForEach.class)
	@Mapping(node = "include", target = Include.class)
	protected List<TagSupport> tags;
	public List<TagSupport> getTags() {
		return tags;
	}
	public void setTags(List<TagSupport> tags) {
		this.tags = tags;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml.replace("<![CDATA[", "").replace("]]>", "");
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}
	public WrapperMapping getWrapperMapping() {
		return wrapperMapping;
	}
	public void setWrapperMapping(WrapperMapping wrapperMapping) {
		this.wrapperMapping = wrapperMapping;
	}
}
