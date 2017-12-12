package com.YaNan.frame.service;

import java.io.File;
import java.util.ArrayList;

public class verCodeSingle {
	private static verCodeSingle verCodeSingle;
	private ArrayList<String> createPath = new ArrayList<String>();
	private ArrayList<String> delPath = new ArrayList<String>();
	private int total = 0;
	private int rest = 0;
	@SuppressWarnings("unused")
	private int errorSum;
	@SuppressWarnings("unused")
	private ArrayList<String> scanPath = new ArrayList<String>();

	// private date createDate;
	private verCodeSingle() {
	}

	public static verCodeSingle getVerCodeSingle() {
		verCodeSingle = (verCodeSingle == null ? new verCodeSingle()
				: verCodeSingle);
		return verCodeSingle;
	}

	public boolean addPath(String path) {
		this.createPath.add(path);
		return true;
	}

	public boolean delPath(String path) {
		File filePath = new File(path);
		if (filePath.exists()) {
			filePath.delete();
			this.delPath.add(path);
			this.total += 1;
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<String> getCreatePath() {
		return this.createPath;
	}

	public ArrayList<String> getDelPath() {
		return this.delPath;
	}

	public int getCreatePathSize() {
		return this.total;
	}

	public int getDelPathSize() {
		return this.rest;
	}

}
