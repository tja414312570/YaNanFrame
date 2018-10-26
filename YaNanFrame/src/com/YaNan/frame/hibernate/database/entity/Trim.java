package com.YaNan.frame.hibernate.database.entity;


import com.YaNan.frame.util.beans.xml.Attribute;

/**
 * mapper文件中的trim标签的映射
 * @author yanan
 *
 */
public class Trim extends TagSupport{
	@Attribute
	private String suffix;
	@Attribute
	private String suffixoverride;
	@Attribute
	private String prefix;
	@Attribute
	private String prefixoverride;
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getSuffixoverride() {
		return suffixoverride;
	}
	public void setSuffixoverride(String suffixoverride) {
		this.suffixoverride = suffixoverride;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getPrefixoverride() {
		return prefixoverride;
	}
	public void setPrefixoverride(String prefixoverride) {
		this.prefixoverride = prefixoverride;
	}
	@Override
	public String toString() {
		return "Trim [suffix=" + suffix + ", suffixoverride=" + suffixoverride + ", prefix=" + prefix
				+ ", prefixoverride=" + prefixoverride + "]";
	}
}
