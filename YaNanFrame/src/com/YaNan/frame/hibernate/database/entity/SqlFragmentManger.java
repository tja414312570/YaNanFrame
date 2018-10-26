package com.YaNan.frame.hibernate.database.entity;

import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.hibernate.database.fragment.SqlFragment;

public class SqlFragmentManger {
	private static Map<String,WrapMapping> wrapMapping = new HashMap<String,WrapMapping>();
	public static void addWarp(SqlFragment sqlFragment){
		String namespace = sqlFragment.getBaseMapping().getWrapperMapping().getNamespace();
		WrapMapping wrapperMapping = wrapMapping.get(namespace);
		if(wrapperMapping==null){
			wrapperMapping = new WrapMapping(namespace);
			wrapMapping.put(namespace,wrapperMapping);
		}
		wrapperMapping.addSqlFragemnt(sqlFragment);
	}
	public static SqlFragment getSqlFragment(String id){
		int symIndex = id.lastIndexOf(".");
		if(symIndex==-1)
			throw new RuntimeException("id \"" +id+"\" does not container namespace symbol \".\"");
		String namespace = id.substring(0,symIndex);
		WrapMapping wrapperMapping = wrapMapping.get(namespace);
		if(wrapperMapping==null)
			throw new RuntimeException("could not found wrapper at namespace \"" +namespace+"\"!");
		String idl = id.substring(symIndex+1);
		SqlFragment sqlFragment = wrapperMapping.getSqlFragment(idl);
		if(sqlFragment==null)
			throw new RuntimeException("could not found wrapper \""+idl+"\" at namespace \"" +namespace+"\"!");
		return sqlFragment;
	}
}
