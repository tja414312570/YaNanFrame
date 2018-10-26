package com.YaNan.frame.hibernate.database.fragment;

import java.util.List;

import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.hibernate.database.entity.SelectorMapping;
import com.YaNan.frame.hibernate.database.entity.TagSupport;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.ProxyModel;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.PlugsHandler;
import com.YaNan.frame.util.StringUtil;

/**
 * 
 * @author yanan
 * @version 20181009
 */
@Register(attribute = "*.SelectorMapping.root", model = ProxyModel.CGLIB, signlTon = false)
public class SelectorFragment extends SqlFragment implements FragmentBuilder {
	private SelectorMapping selectMapping;

	@Override
	public PreparedFragment prepared(Object... objects) {
		super.prepared(objects);
		if (this.fragemntSet != null)
			return this.fragemntSet.prepared(objects);
		return null;
	}

	@Override
	public void build(Object wrapper) {
		super.build(wrapper);
		this.selectMapping = (SelectorMapping) wrapper;
		// create a root FragmentSet
		String sql = this.baseMapping.getXml();
		// 去掉外面的标签,并重组sql语句,不能删除空格
		int index = sql.indexOf(SPLITPREFIX);
		int endex = sql.lastIndexOf(SUFFIX);
		sql = sql.substring(index + 1, endex);
		// 简历一个临时的FragmentSet
		FragmentSet currentFragmentSet;
		FragmentSet preFragmentSet = null;
		// 分隔片段
		// 获取sql里的动态语句标签//并对最外层分隔
		List<TagSupport> tags = selectMapping.getTags();
		if (tags.size() == 0)// 如果没有动态标签
		{
			this.fragemntSet = currentFragmentSet = (FragmentSet) PlugsFactory
					.getPlugsInstanceByAttributeStrict(FragmentBuilder.class, "DEFAULT.fragment");
			currentFragmentSet.setXml(this.baseMapping.getXml());
			currentFragmentSet.setValue(this.baseMapping.getContent());
			currentFragmentSet.setSqlFragment(this);
			currentFragmentSet.build(null);
		} else {
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
					currentFragmentSet.setSqlFragment(this);
					currentFragmentSet.build(null);
					if (this.fragemntSet == null)
						this.fragemntSet = currentFragmentSet;
					if (preFragmentSet != null)
						preFragmentSet.setNextSet(currentFragmentSet);
					preFragmentSet = currentFragmentSet;
				}
				// 根据类型获取对应FragmentSet
				currentFragmentSet = (FragmentSet) PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
						tagClass.getName() + ".fragment");
				// 判断根FragmentSet是否为空
				if (this.fragemntSet == null)
					this.fragemntSet = currentFragmentSet;
				if (preFragmentSet != null)
					preFragmentSet.setNextSet(currentFragmentSet);
				preFragmentSet = currentFragmentSet;
				currentFragmentSet.setXml(tag.getXml());
				currentFragmentSet.setValue(tag.getValue());
				currentFragmentSet.setTagSupport(tag);
				currentFragmentSet.setSqlFragment(this);
				currentFragmentSet.build(null);
				sql = sql.substring(predex + len);
			}
			// 截取类容
			if (sql != null && !sql.trim().equals("")) {
				currentFragmentSet = (FragmentSet) PlugsFactory.getPlugsInstanceByAttributeStrict(FragmentBuilder.class,
						"DEFAULT.fragment");
				currentFragmentSet.setXml(sql);
				currentFragmentSet.setValue(sql);
				currentFragmentSet.setSqlFragment(this);
				currentFragmentSet.build(null);
				if (this.fragemntSet == null)
					this.fragemntSet = currentFragmentSet;
				if (preFragmentSet != null)
					preFragmentSet.setNextSet(currentFragmentSet);
				preFragmentSet = currentFragmentSet;
			}
		}
	}

	public PreparedSql getPreparedSql(Object... parameter) {
		PreparedFragment preparedFragment = this.prepared(parameter);
		preparedFragment.setSql(this.preparedParameterSql(preparedFragment.getSql(), parameter));
		List<Object> arguments = this.preparedParameter(preparedFragment.getArguments(), parameter);
		PreparedSql preparedSql = new PreparedSql(preparedFragment.getSql(), arguments, this);
		return preparedSql;
	}

	private String preparedParameterSql(String sql, Object... parameter) {
		List<String> variable = StringUtil.find(sql, "${", "}");
		if (variable != null && variable.size() > 0) {
			StringBuffer sb = new StringBuffer(sql);
			List<Object> arguments = this.preparedParameter(variable, parameter);
			for (int i = 0; i < variable.size(); i++) {
				String rep = "${" + variable.get(i) + "}";
				int index = sb.indexOf(rep);
				Object arg = arguments.get(i);
				while (index > -1) {
					sb = new StringBuffer(sb.substring(0, index)).append(arg)
							.append(sb.substring(index + rep.length()));
					index = sb.indexOf(rep);
				}
			}
			sql = sb.toString();
		}
		sql = sql.replaceAll("\n\t\t", " ").replaceAll("\t\t", " ").replaceAll("\t", " ").replaceAll("\n", " ").trim();
		return sql;
	}
}
