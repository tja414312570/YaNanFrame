package com.a.encrypt;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.DBManager;
import com.YaNan.frame.hibernate.database.entity.BaseMapping;
import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.hibernate.database.entity.SelectorMapping;
import com.YaNan.frame.hibernate.database.entity.WrapperConfgureMapping;
import com.YaNan.frame.hibernate.database.entity.WrapperMapping;
import com.YaNan.frame.hibernate.database.fragment.FragmentBuilder;
import com.YaNan.frame.hibernate.database.fragment.SelectorFragment;
import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.autowired.property.Property;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.util.beans.xml.XMLHelper;
import com.google.gson.Gson;

public class Main {
	public Main(@Property(notNull=true,value="sms.api.password") String t) {
		System.out.println(t);
	}
	@Service
	private SqlSession session;
	
	public static void main(String[] args) throws Exception {
		DBFactory.getDBFactory().init();
		XMLHelper help = new XMLHelper();
		help.setFile(new File("/Volumes/GENERAL/git/Frame/YaNanFrame/conf/hibernate.xml"));
		help.setMapping(WrapperConfgureMapping.class);
		List<WrapperConfgureMapping> list = help.read();
		WrapperConfgureMapping wrapper = list.get(0);
		String[] wrappers = wrapper.getWrapper();
		//获取所有的xml文件
		List<File> files = ResourceManager.getResource(wrappers[0]);
		//获得所有的
		XMLHelper helper = new XMLHelper(files.get(0),WrapperMapping.class);
		List<WrapperMapping> wrapps = helper.read();
		List<BaseMapping> selectMapping = wrapps.get(0).getBaseMappings();
		Iterator<BaseMapping> selectMappingIterator = selectMapping.iterator();
		while(selectMappingIterator.hasNext()){
			BaseMapping mapping = selectMappingIterator.next();
			PlugsHandler handler = PlugsFactory.getPlugsHandler(mapping);
			FragmentBuilder fragmentBuilder = PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,handler.getProxyClass().getName()+".root");
			System.out.println(PlugsFactory.getPlugsHandler(mapping).getProxyClass());
			mapping.setWrapperMapping(wrapps.get(0));
			fragmentBuilder.build(mapping);
			SelectorFragment selectFragment = (SelectorFragment)fragmentBuilder;
     			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
     		Map<String,String> parameter = new HashMap<String,String>();
     		parameter.put("id", "10");
     		parameter.put("name", "java");
     		selectFragment.prepared(parameter);
     		PreparedSql preparedSql = selectFragment.getPreparedSql(parameter);
     		System.out.println(preparedSql);
			System.out.println(new Gson().toJson((List<Map<String,String>>)preparedSql.execute()));
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
		}
	}
}
