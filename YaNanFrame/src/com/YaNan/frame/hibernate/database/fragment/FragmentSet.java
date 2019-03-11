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

import jdk.nashorn.api.scripting.ScriptObjectMirror;
/**
 * sql片断的默认集合实现，基于二叉树结构
 * 当前片断仅包含本节点信息以及子节点和下一节点信息
 * 条件判断使用jdk的Nashorn引擎（javascript引擎）
 * @author yanan
 *
 */
@Register(attribute = "*.fragment", priority = Integer.MAX_VALUE, model = ProxyModel.CGLIB, signlTon = false)
public class FragmentSet implements FragmentBuilder {
	// xml文档
	protected String xml;
	// sql语句
	protected String value;
	// 子片段的集合
	protected FragmentSet childSet;
	// 下一个片段的集合
	protected FragmentSet nextSet;
	protected TagSupport tagSupport;
	protected SqlFragment sqlFragment;
	protected List<String> parameters = new ArrayList<String>();
	ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
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
	
	/**
	 * 根据参数将SQL片段（FragmentSet）生成具有语义的预执行片段(PreparedFragment)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PreparedFragment prepared(Object... objects) {
		/**
		 * 通过调用PlugsFactory生成PreparedFragment的实例。
		 */
		PreparedFragment preparedFragment = PlugsFactory.getPlugsInstance(PreparedFragment.class);
		/**
		 * 将所有的变量都添加到预执行片段中
		 */
		preparedFragment.addAllVariable(this.preparedParameter(this.parameters, objects));
		/**
		 * if next node is not null and child node is not null
		 */
		if (this.nextSet != null && this.childSet != null) {
			/**
			 * invoke child node's prepared method to build child node's PreparedFragment object;
			 */
			PreparedFragment child = this.childSet.prepared(objects);
			/**
			 * invoke next node's prepared method to build next node's PreparedFragment object;
			 */
			PreparedFragment next = this.nextSet.prepared(objects);
			/**
			 * build child and next prparedFragmet SQL as this fragment SQL.
			 */
			preparedFragment.setSql(child.getSql()+" " + next.getSql());
			/**
			 * add child and next node all argument to this fragment
			 */
			preparedFragment.addParameter(child.getArguments(), next.getArguments());
			/**
			 * add child and next node all variable to this fragment
			 */
			preparedFragment.addAllVariable(child.getVariable());
			preparedFragment.addAllVariable(next.getVariable());
		} else if (this.childSet != null) {
			PreparedFragment child = this.childSet.prepared(objects);
			preparedFragment.setSql(child.getSql());
			preparedFragment.addParameter(child.getArguments());
			preparedFragment.addAllVariable(child.getVariable());
		} else if (this.nextSet != null) {
			PreparedFragment next = this.nextSet.prepared(objects);
			preparedFragment.setSql(this.preparedParameterSql(this.value, objects).trim()+ " "+next.getSql());
			preparedFragment.addParameter(this.parameters, next.getArguments());
			preparedFragment.addAllVariable(next.getVariable());
		} else {
			preparedFragment.setSql(this.preparedParameterSql(this.value, objects));
			preparedFragment.addParameter(this.parameters);
		}
		return preparedFragment;
	}
	/**
	 * 此方法用于将sql语句中的${|args}代替为具体的变量
	 * @param sql
	 * @param parameter
	 * @return
	 */
	public String preparedParameterSql(String sql, Object... parameter) {
		List<String> variable = StringUtil.find(sql, "${", "}");
		if (variable != null && variable.size() > 0) {
			StringBuffer sb = new StringBuffer(sql).append(" ");
			List<Object> arguments = this.preparedParameter(variable, parameter);
			for (int i = 0; i < variable.size(); i++) {
				String rep = "${" + variable.get(i) + "}";
				int index = sb.indexOf(rep);
				Object arg = arguments.get(i);
				while (index > -1) {
					//不提供变量自动加上引号
					sb = new StringBuffer(sb.substring(0, index)).append(arg)//.append("'").append(arg).append("'")
							.append(sb.substring(index + rep.length()));
					index = sb.indexOf(rep);
				}
			}
			sql = sb.toString();
		}
		//将制表符和换行符替换为空格。
		sql = sql.replaceAll("\n\t\t", " ").replaceAll("\t\t", " ").replaceAll("\t", " ").replaceAll("\n", " ").trim();
		return sql;
	}

	public void buildMapParameter(List<String> variables, List<Object> arguments, Map<?, ?> object) {
		Iterator<String> iterator = variables.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			arguments.add(object.get(key));
		}
	}
	/**
	 * 获取创建sql时所涉及到的参数列表
	 * @param variables 此片段锁涉及到的变量
	 * @param parameter 调用接口传入的参数
	 * @return
	 */
	public List<Object> preparedParameter(List<String> variables, Object... parameter) {
		List<Object> arguments = new ArrayList<Object>();
		if (parameter != null && variables.size() > 0) {
			// 需要参数的数量是否与传入参数的数量相同
			if (parameter.length > 1) {
				if (SqlFragment.removeDuplicate(variables).size() <= parameter.length)
					for (int i = 0; i < variables.size(); i++) {
						int pos = this.sqlFragment.getArguments().indexOf(variables.get(i));
						arguments.add(pos >= parameter.length ? null : parameter[pos]);
					}
				else
					throw new RuntimeException("failed to prepared parameter \"" + variables
							+ "\"because the need parameter \"" + variables.size() + "\" get the parameter \""
							+ parameter.length + "\"! at mapping file '" + this.sqlFragment.baseMapping.getXmlFile()
							+ "' at id '" + this.sqlFragment.baseMapping.getId() + "'");
			} else if (parameter.length == 1) {
				Object object = parameter[0];
				if (object == null) {
					arguments.add(object);
					//如果参数时脚本引擎可以直接使用Binder
				}else if(ClassLoader.extendsOf(object.getClass(), ScriptObjectMirror.class)){ 
					for(int i = 0;i<variables.size();i++){
						try {
							arguments.add(scriptEngine.eval(variables.get(i),(Bindings)object));
						} catch (ScriptException e) {
							throw new RuntimeException("failed to execute \"" + variables.get(i) + "\" expression! at mapping file '"
									+ this.sqlFragment.baseMapping.getXmlFile() + "' at id '" + this.sqlFragment.baseMapping.getId()
									+ "' at item data " + object, e);
						}
					}
					//如果是Map类型的参数
				}else if (ClassLoader.implementsOf(object.getClass(), Map.class)) {
					this.buildMapParameter(variables, arguments, (Map<?, ?>) object);
					//如果是List类型的参数
				} else if (ClassLoader.implementsOf(object.getClass(), List.class)) {
					this.buldListParameter(variables, arguments, (List<?>) object);
					//如果参数是一个基本类型
				} else if (ClassLoader.isBaseType(object.getClass())) {
					if (SqlFragment.removeDuplicate(variables).size() == 1)
						for (int i = 0; i < variables.size(); i++)
							arguments.add(object);
					else
						throw new RuntimeException("failed to prepared parameter \"" + variables
								+ "\"because the need parameter \"" + variables.size() + "\" get the parameter \""
								+ parameter.length + "\"! at mapping file '" + this.sqlFragment.baseMapping.getXmlFile()
								+ "' at id '" + this.sqlFragment.baseMapping.getId() + "'");
				} else {
					//如果参数是一个POJO类型
					ClassLoader loader = new ClassLoader(object);
					for (int i = 0; i < variables.size(); i++)
						try {
							arguments.add(loader.get(variables.get(i)));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException("failed to get need parameter \"" + variables.get(i)
									+ "\" at parameterType " + loader.getLoadedClass() + "at mapping file '"
									+ this.sqlFragment.baseMapping.getXmlFile() + "' at id '"
									+ this.sqlFragment.baseMapping.getId() + "'", e);
						}
				}
			}
		}
		return arguments;
	}

	public void buldListParameter(List<String> variables, List<Object> arguments2, List<?> object) {
		for (int i = 0; i < variables.size(); i++)
			arguments2.add(i < object.size() ? object.get(i) : null);
	}

	@Override
	public void build(Object wrapper) {
		String sql = this.xml;
		// 去掉外面的标签,并重组sql语句,不能删除空格
		int index = sql.indexOf(SPLITPREFIX);
		int endex = sql.lastIndexOf(SUFFIX);
		if (endex != -1 && index != -1 && this.tagSupport != null) {
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
					currentFragmentSet = (FragmentSet) PlugsFactory
							.getPlugsInstanceByAttributeStrict(FragmentBuilder.class, "DEFAULT.fragment");
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

		List<String> tempParams = new ArrayList<String>();
		String sqlTmp = this.value;
		if (this.childSet != null)
			sqlTmp = sqlTmp.replace(this.childSet.getXml(), "");
		List<String> vars = StringUtil.find(sqlTmp, "#{", "}", "?");
		String tempValue = this.value;
		if (vars.size() > 1) {
			this.value = vars.get(vars.size() - 1);
			for (int i = 0; i < vars.size() - 1; i++) {
				this.parameters.add(vars.get(i));
				tempParams.add(vars.get(i));
			}
		}
		vars = StringUtil.find(sqlTmp, "${", "}");
		if (vars.size() > 0) {
			for (int i = 0; i < vars.size(); i++) {
				tempParams.add(vars.get(i));
			}
		}
		// 重组Sql参数集合
		if (tempParams.size() != 0) {
			Map<Integer, String> treeMap = new TreeMap<Integer, String>();
			for (String var : tempParams) {
				if (!treeMap.values().contains(var)) {
					index = tempValue.indexOf(var);
					treeMap.put(index, var);
				}
			}
			Iterator<String> iterator = treeMap.values().iterator();
			while (iterator.hasNext()){
				String arg = iterator.next();
				if(arg.indexOf(".")==-1 && arg.indexOf("[") == -1){
					this.sqlFragment.addParameter(arg);
				}
			}
		}
	}

	public boolean eval(String express, List<String> argument, Object... objects) {
		Bindings binder = scriptEngine.createBindings();
		if (objects != null) {
			// 如果有多个参数
			if (objects.length > 1) {
				// 需要参数的数量是否与传入参数的数量相同
				for (int i = 0; i < argument.size(); i++) {
					int pos = this.sqlFragment.getArguments().indexOf(argument.get(i));
					binder.put(argument.get(i), pos >= objects.length ? null : objects[pos]);
				}

				// 单个参数
			} else if (objects.length == 1) {
				Object object = objects[0];
				if (object == null) {
					binder.put(argument.get(0), object);
				}else if(ClassLoader.extendsOf(object.getClass(), ScriptObjectMirror.class)){ 
					binder = (Bindings) object;
				}
				// 如果参数类型为Map
				else if (ClassLoader.implementsOf(object.getClass(), Map.class)) {
					this.buildMapBinder(binder, argument, (Map<?, ?>) object);
					// 如果参数为List
				} else if (ClassLoader.implementsOf(object.getClass(), List.class)) {
					this.buldListBinder(binder, argument, (List<?>) object);
					// 如果参数时基本类型
				} else if (ClassLoader.isBaseType(object.getClass())) {
					if (argument.size() == 1){
						int pos = this.sqlFragment.getArguments().indexOf(argument.get(0));
						binder.put(argument.get(0), pos >= objects.length ? null : objects[pos]);
					}
					else
						throw new RuntimeException(
								"failed to execute \"" + express + "\" expression because the need parameter \""
										+ argument.size() + "\" get the parameter \"" + objects.length
										+ "\"! at mapping file '" + this.sqlFragment.baseMapping.getXmlFile()
										+ "' at id '" + this.sqlFragment.baseMapping.getId() + "'");
				} else {
					ClassLoader loader = new ClassLoader(object);
					for (int i = 0; i < argument.size(); i++)
						try {
							binder.put(argument.get(i), loader.get(argument.get(i)));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException("failed to get need parameter \"" + argument.get(i)
									+ "\" at express \"" + express + "\" at parameterType " + loader.getLoadedClass(),
									e);
						}
				}
			} else {
				for (int i = 0; i < argument.size(); i++)
					binder.put(argument.get(i), null);
			}
		}
		Object result = null;
		try {
			result = scriptEngine.eval(express, binder);
			if (result.getClass().equals(Boolean.class))
				return (boolean) result;
			else
				throw new RuntimeException("failed to execute \"" + express
						+ "\" expression,because the result type is not boolean! at mapping file '"
						+ this.sqlFragment.baseMapping.getXmlFile() + "' at id '" + this.sqlFragment.baseMapping.getId()
						+ "'");
		} catch (ScriptException e) {
			throw new RuntimeException("failed to execute \"" + express + "\" expression! at mapping file '"
					+ this.sqlFragment.baseMapping.getXmlFile() + "' at id '" + this.sqlFragment.baseMapping.getId()
					+ "'", e);
		}
	}

//	public Object eval(Map<String, Object> binds, String variable) {
//		ScriptEngineManager manager = new ScriptEngineManager();
//		ScriptEngine engine = manager.getEngineByName("JavaScript");
//		Bindings bind = engine.createBindings();
//		bind.putAll(binds);
//		try {
//			return engine.eval("stu.getAid()", bind);
//		} catch (ScriptException e) {
//			throw new RuntimeException("failed to execute \"" + variable + "\" expression! at mapping file '"
//					+ this.sqlFragment.baseMapping.getXmlFile() + "' at id '" + this.sqlFragment.baseMapping.getId()
//					+ "' at item data " + binds, e);
//		}
//	}

	private void buldListBinder(Bindings binder, List<String> argument, List<?> object) {
		for (int i = 0; i < argument.size(); i++)
			binder.put(argument.get(i), i < object.size() ? object.get(i) : null);
	}
	
	/**
	 * 将表达式中的and or not 转化为JS可以执行的逻辑符
	 * @param test
	 * @return
	 */
	public String switchExpress(String test) {
		test = test.replace(" and ", " && ").replace(" or ", " || ").replace(" not ", " ! ");
		return test;
	}

	private void buildMapBinder(Bindings binder, List<String> argument, Map<?, ?> parameter) {
		Iterator<String> iterator = argument.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			binder.put(key, parameter.get(key));
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
