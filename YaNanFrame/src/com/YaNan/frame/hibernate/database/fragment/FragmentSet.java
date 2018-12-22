package com.YaNan.frame.hibernate.database.fragment;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.YaNan.frame.hibernate.database.entity.TagSupport;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.ProxyModel;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.util.StringUtil;

@Register(attribute="*.fragment",priority=Integer.MAX_VALUE,model=ProxyModel.CGLIB,signlTon=false)
public class FragmentSet implements FragmentBuilder{
	//xml文档
	protected String xml;
	//sql语句
	protected String value;
	//子片段的集合
	protected FragmentSet childSet;
	//下一个片段的集合
	protected FragmentSet nextSet;
	protected TagSupport tagSupport;
	protected SqlFragment sqlFragment;
	protected List<String> parameters = new ArrayList<String>();
	public TagSupport getTagSupport() {
		return tagSupport;
	}
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public FragmentSet getChildSet() {
		return childSet;
	}
	public void setChildSet(FragmentSet childSet) {
		this.childSet = childSet;
	}
	@SuppressWarnings("unchecked")
	@Override
	public PreparedFragment prepared(Object... objects) {
		PreparedFragment preparedFragment = PlugsFactory.getPlugsInstance(PreparedFragment.class);
		if(this.nextSet!=null&&this.childSet!=null){
			PreparedFragment child = this.childSet.prepared(objects);
			PreparedFragment next = this.nextSet.prepared(objects);
			preparedFragment.setSql(child.getSql()+next.getSql());
			preparedFragment.addParameter(child.getArguments(),next.getArguments());
		}
		else if(this.childSet!=null){
			PreparedFragment child = this.childSet.prepared(objects);
			preparedFragment.setSql(child.getSql());
			preparedFragment.addParameter(child.getArguments());
		}else if(this.nextSet!=null){
			PreparedFragment next = this.nextSet.prepared(objects);
			preparedFragment.setSql(this.value+next.getSql());
			preparedFragment.addParameter(this.parameters,next.getArguments());
		}else{
			preparedFragment.setSql(this.value);
			preparedFragment.addParameter(this.parameters);
		}
		return preparedFragment;
	}
	@Override
	public void build(Object wrapper) {
		List<String> tempParams = new ArrayList<String>();
		List<String> vars = StringUtil.find(this.value, "#{", "}", "?");
		String tempValue = this.value;
		if(vars.size()>1){
			this.value = vars.get(vars.size()-1);
			for(int i=0 ; i<vars.size()-1;i++){
				this.parameters.add(vars.get(i));
				tempParams.add(vars.get(i));
			}
		}
		vars = StringUtil.find(this.value, "${", "}");
		if(vars.size()>1){
			for(int i=0 ; i<vars.size();i++){
				tempParams.add(vars.get(i));
			}
		}
		//重组Sql参数集合
		if(tempParams.size()!=0){
			Map<Integer,String> treeMap = new TreeMap<Integer, String>();
			for(String var : tempParams){
				if(!treeMap.values().contains(var)){
					int index = tempValue.indexOf(var);
					treeMap.put(index, var);
				}
			}
			Iterator<String> iterator = treeMap.values().iterator();
			while(iterator.hasNext())
				this.sqlFragment.addParameter(iterator.next());
		}
		String sql = this.xml;
		// 去掉外面的标签,并重组sql语句,不能删除空格
		int index = sql.indexOf(SPLITPREFIX);
		int endex = sql.lastIndexOf(SUFFIX);
		if(endex==-1||index==-1||this.tagSupport==null)
			return;
		sql = sql.substring(index + 1, endex);
		// 简历一个临时的FragmentSet
		FragmentSet currentFragmentSet;
		FragmentSet preFragmentSet = null;
		// 分隔片段
		// 获取sql里的动态语句标签//并对最外层分隔
		List<TagSupport> tags = this.tagSupport.getTags();
		for (TagSupport tag : tags) {
			// 获得TagSupport的类型
			PlugsHandler plugsHandler = PlugsFactory.getPlugsHandler(tag);
			Class<?> tagClass = plugsHandler.getProxyClass();
			// 截取类容
			int predex = sql.indexOf(tag.getXml());
			int len = tag.getXml().length();
			String preffix = sql.substring(0, predex);
			if (preffix != null && !preffix.trim().equals("")) {
				currentFragmentSet = (FragmentSet) PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
						"DEFAULT.fragment");
				currentFragmentSet.setXml(preffix);
				currentFragmentSet.setValue(preffix);
				currentFragmentSet.setSqlFragment(this.sqlFragment);
				currentFragmentSet.build(null);
				if (this.childSet == null)
					this.childSet = currentFragmentSet;
				if (preFragmentSet != null)
					preFragmentSet.setNextSet(currentFragmentSet);
				preFragmentSet = currentFragmentSet;
			}
			// 根据类型获取对应FragmentSet
			currentFragmentSet = (FragmentSet) PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
					tagClass.getName() + ".fragment");
			// 判断根FragmentSet是否为空
			if (this.childSet == null)
				this.childSet = currentFragmentSet;
			if (preFragmentSet != null)
				preFragmentSet.setNextSet(currentFragmentSet);
			preFragmentSet = currentFragmentSet;
			currentFragmentSet.setXml(tag.getXml());
			currentFragmentSet.setValue(tag.getValue());
			currentFragmentSet.setTagSupport(tag);
			currentFragmentSet.setSqlFragment(this.sqlFragment);
			currentFragmentSet.build(tag.getTags());
			sql = sql.substring(predex + len);
		}
		if (sql != null && !sql.trim().equals("")) {
			currentFragmentSet = (FragmentSet) PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
					"DEFAULT.fragment");
			currentFragmentSet.setXml(sql);
			currentFragmentSet.setValue(sql);
			currentFragmentSet.setSqlFragment(this.sqlFragment);
			currentFragmentSet.build(null);
			if (this.childSet == null)
				this.childSet = currentFragmentSet;
			if (preFragmentSet != null)
				preFragmentSet.setNextSet(currentFragmentSet);
			preFragmentSet = currentFragmentSet;
		}
	}
	public boolean eval(String express,List<String> argument,Object...objects){
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
		Bindings binder = scriptEngine.createBindings();
		if(objects!=null){
			//如果有多个参数
			if(objects.length>1){
				//需要参数的数量是否与传入参数的数量相同
				for(int i = 0 ;i<argument.size();i++){
					int pos = this.sqlFragment.getArguments().indexOf(argument.get(i));
					binder.put(argument.get(i),pos>=objects.length?null:objects[pos]);
				}
					
			//单个参数
			}else if(objects.length==1){
				Object object = objects[0];
				if(object==null){
					binder.put(argument.get(0), object);
				}
				//如果参数类型为Map
				else if(ClassLoader.implementsOf(object.getClass(), Map.class)){
					this.buildMapBinder(binder,argument,(Map<?, ?>)object);
					//如果参数为List
				}else if(ClassLoader.implementsOf(object.getClass(), List.class)){
					this.buldListBinder(binder,argument,(List<?>)object);
					//如果参数时基本类型
				}else if(ClassLoader.isBaseType(object.getClass())){
					if (argument.size() == 1)
							binder.put(argument.get(0), object);
					else 
						throw new RuntimeException("failed to execute \""+express+"\" expression because the need parameter \""
								+argument.size()+"\" get the parameter \""+objects.length+"\"! at mapping file '"+this.sqlFragment.baseMapping.getXmlFile()+"' at id '"+this.sqlFragment.baseMapping.getId()+"'");
				}else{
					ClassLoader loader = new ClassLoader(object);
					for(int i = 0 ;i<argument.size();i++)
						try {
							binder.put(argument.get(i),loader.get(argument.get(i)));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException("failed to get need parameter \""+argument.get(i)+"\" at express \""+express+"\" at parameterType "+loader.getLoadedClass(),e);
						}
				}
			}else{
				for(int i = 0 ;i<argument.size();i++)
					binder.put(argument.get(i),null);
			}
		}
		Object result = null;
		try {
			result = scriptEngine.eval(express, binder);
			if(result.getClass().equals(Boolean.class))
				return (boolean) result;
			else throw new RuntimeException("failed to execute \""+express+"\" expression,because the result type is not boolean! at mapping file '"+this.sqlFragment.baseMapping.getXmlFile()+"' at id '"+this.sqlFragment.baseMapping.getId()+"'");
		} catch (ScriptException e) {
			throw new RuntimeException("failed to execute \""+express+"\" expression! at mapping file '"+this.sqlFragment.baseMapping.getXmlFile()+"' at id '"+this.sqlFragment.baseMapping.getId()+"'",e);
		}
	}
	private void buldListBinder(Bindings binder,List<String> argument, List<?> object) {
		for(int i = 0 ;i<argument.size();i++)
			binder.put(argument.get(i),i<object.size()? object.get(i):null);
	}
	public String switchExpress(String test) {
		test = test.replaceAll(" and ", " && ").replaceAll(" or ", " || ").replaceAll(" not ", " ! ");
		return test;
	}
	private void buildMapBinder(Bindings binder, List<String> argument,Map<?,?> parameter) {
		Iterator<String> iterator = argument.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			binder.put(key,parameter.get(key));
		}
	}
	public FragmentSet getNextSet() {
		return nextSet;
	}
	public void setNextSet(FragmentSet nextSet) {
		this.nextSet = nextSet;
	}
	public SqlFragment getSqlFragment() {
		return sqlFragment;
	}
	public void setSqlFragment(SqlFragment sqlFragment) {
		this.sqlFragment = sqlFragment;
	}
	
}
