package com.YaNan.frame.hibernate.database.fragment;

import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.entity.BaseMapping;
import com.YaNan.frame.hibernate.database.entity.Include;
import com.YaNan.frame.hibernate.database.entity.SqlFragmentManger;
import com.YaNan.frame.hibernate.database.exception.HibernateInitException;
import com.YaNan.frame.plugin.ProxyModel;
import com.YaNan.frame.plugin.annotations.Register;

@Register(attribute = "*.Include.fragment", model = ProxyModel.CGLIB, signlTon = false)
public class IncludeFragment extends FragmentSet implements FragmentBuilder {
	// 逻辑表达式
	private String id;
	private SqlFragment sql;
	//	参数列表
	@Override
	public PreparedFragment prepared(Object... objects) {
		return super.prepared(objects);
	}
	//构建sql片段
	@Override
	public void build(Object wrapper) {
		Include include = (Include) this.tagSupport;
		if(include.getId()!=null&&!include.getId().trim().equals("")){
			this.id = include.getId();
		}else if(include.getValue()!=null&&!include.getValue().trim().equals("")){
			this.id = include.getValue();
		}else{
			throw new HibernateInitException("mapper \""+this.sqlFragment.getId()+"\" not id attr at file "+this.sqlFragment.getBaseMapping().getXmlFile());
		}
		if(id.indexOf(".")==-1)
			id = this.sqlFragment.getBaseMapping().getWrapperMapping().getNamespace()+"."+id;
		try{
			this.sql = SqlFragmentManger.getSqlFragment(this.id);
		}catch (Exception e) {
		}
		if(sql==null){
			BaseMapping mapping = DBFactory.getDBFactory().getWrapMap(id);
			if(mapping==null)
				throw new HibernateInitException("mapper \""+id+"\" could not be found at wrap id \""+this.sqlFragment.getId()+"\" at file "+this.sqlFragment.getBaseMapping().getXmlFile());
			this.sql = DBFactory.getDBFactory().buildFragment(mapping);
		}
		for(String args : sql.getArguments()){
			this.sqlFragment.addParameter(args);
		}
		this.childSet = sql.fragemntSet;
		super.build(wrapper);
	}

}
