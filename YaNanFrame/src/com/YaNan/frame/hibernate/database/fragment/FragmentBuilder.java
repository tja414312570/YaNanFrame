package com.YaNan.frame.hibernate.database.fragment;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface FragmentBuilder {
	public final static String PREFIX = "<";
	public final static String SPLITSUFFIX = " ";
	public final static String SUFFIX = "</";
	public final static String SPLITPREFIX = ">";
	/**
	 * 构架FragmentSet
	 * @return
	 */
	void build(Object wrapper);
	/**
	 * 准备sql
	 * @return
	 */
	PreparedFragment prepared(Object...objects);
}
