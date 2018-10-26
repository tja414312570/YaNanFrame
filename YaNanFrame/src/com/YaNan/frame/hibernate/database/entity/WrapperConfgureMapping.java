package com.YaNan.frame.hibernate.database.entity;

import java.util.Arrays;

import com.YaNan.frame.hibernate.database.DataBaseConfigure;
import com.YaNan.frame.util.beans.xml.Element;

@Element(name="/Hibernate")
public class WrapperConfgureMapping {
	private String[] wrapper;
	@Element(name="dataBase")
	private DataBaseConfigure[] dataBases;
	@Override
	public String toString() {
		return "WrapperConfgureMapping [wrapper=" + Arrays.toString(wrapper) + ", database=" + Arrays.toString(dataBases) + "]";
	}
	public String[] getWrapper() {
		return wrapper;
	}
	public void setWrapper(String[] wrapper) {
		this.wrapper = wrapper;
	}
	public DataBaseConfigure[] getDataBases() {
		return dataBases;
	}
	public void setDataBases(DataBaseConfigure[] dataBases) {
		this.dataBases = dataBases;
	}
	
}
