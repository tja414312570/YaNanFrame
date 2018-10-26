package com.YaNan.frame.hibernate.database.entity;

import java.util.List;

import com.YaNan.frame.util.beans.xml.Attribute;
import com.YaNan.frame.util.beans.xml.ENCODEING;
import com.YaNan.frame.util.beans.xml.Element;
import com.YaNan.frame.util.beans.xml.Encode;
import com.YaNan.frame.util.beans.xml.FieldType;
import com.YaNan.frame.util.beans.xml.FieldTypes;
import com.YaNan.frame.util.beans.xml.Mapping;

@Encode(ENCODEING.UTF16)
@Element(name="wrapper")
@FieldType(FieldTypes.ALL)
public class WrapperMapping{
	@Attribute
	private String namespace;
	@Attribute
	private String database;
	@Mapping(node = "select", target = SelectorMapping.class)
	@Mapping(node = "update", target = BaseMapping.class)
	private List<BaseMapping> baseMappings;
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	@Override
	public String toString() {
		return "WrapperMapping [namespace=" + namespace + ", database=" + database + ", baseMappings=" + baseMappings + "]";
	}
	public List<BaseMapping> getBaseMappings() {
		return baseMappings;
	}
	public void setBaseMappings(List<BaseMapping> baseMappings) {
		this.baseMappings = baseMappings;
	}
}
