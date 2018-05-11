package com.YaNan.Demo.pojo;

import java.util.Arrays;

public class Array {
	private String[] types;
	@Override
	public String toString() {
		return "Array [types=" + Arrays.toString(types) + ", net=" + Arrays.toString(net) + "]";
	}
	private String[] net;
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String[] getNet() {
		return net;
	}
	public void setNet(String[] net) {
		this.net = net;
	}
}
