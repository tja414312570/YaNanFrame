package com.YaNan.frame.hibernate.database.entity;

import java.util.List;

import com.YaNan.frame.util.beans.xml.AsXml;
import com.YaNan.frame.util.beans.xml.Mapping;
import com.YaNan.frame.util.beans.xml.Value;

public class TagSupport{
	@AsXml
	protected String xml;
	@Value
	protected String value;
	@Mapping(node = "trim", target = Trim.class)
	@Mapping(node = "if", target = IF.class)
	@Mapping(node = "foreach", target = ForEach.class)
	@Mapping(node = "include", target = Include.class)
	protected List<TagSupport> tags;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml.replace("<![CDATA[", "").replace("]]>", "");
	}
	public List<TagSupport> getTags() {
		return tags;
	}
	public void setTags(List<TagSupport> tags) {
		this.tags = tags;
	}
	@Override
	public String toString() {
		return "TagSupport [xml=" + xml + ", value=" + value + ", tags=" + tags + "]";
	}
}
