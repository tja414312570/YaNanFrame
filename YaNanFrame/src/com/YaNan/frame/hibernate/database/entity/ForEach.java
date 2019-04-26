package com.YaNan.frame.hibernate.database.entity;


import com.YaNan.frame.utils.beans.xml.Attribute;

public class ForEach extends TagSupport{
	@Attribute
	private String collection;
	@Attribute
	private String index;
	@Attribute
	private String item;
	@Attribute
	private String open;
	@Attribute
	private String separator;
	@Attribute
	private String close;
	@Override
	public String toString() {
		return "ForEach [collection=" + collection + ", index=" + index + ", item=" + item + ", open=" + open
				+ ", separator=" + separator + ", close=" + close + "]";
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
}
