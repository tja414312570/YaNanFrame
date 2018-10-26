package com.YaNan.frame.hibernate.database.fragment;

import java.util.ArrayList;
import java.util.List;

import com.YaNan.frame.hibernate.database.entity.IF;
import com.YaNan.frame.hibernate.database.fragment.Symbol.JAVASCRIPT;
import com.YaNan.frame.plugin.ProxyModel;
import com.YaNan.frame.plugin.annotations.Register;

@Register(attribute = "*.IF.fragment", model = ProxyModel.CGLIB, signlTon = false)
public class IFFragment extends FragmentSet implements FragmentBuilder {
	// 逻辑表达式
	private String test;
	private List<String> testArgument = new ArrayList<String>();
	//	参数列表
	@Override
	public PreparedFragment prepared(Object... objects) {
		if((boolean) this.eval(test,testArgument, objects))
			return super.prepared(objects);
		if(this.nextSet!=null)
			return this.nextSet.prepared(objects);
		return new PreparedFragment();
	}
	//构建sql片段
	@Override
	public void build(Object wrapper) {
		IF ifTagSupport = (IF) this.tagSupport;
		this.test = ifTagSupport.getTest();
		this.test = switchExpress(test);
		String condition = this.test;
		while(JAVASCRIPT.match(condition)!=null){
			condition = condition.replaceAll(JAVASCRIPT.match(condition).value, " ");
		}
		super.build(wrapper);
		String[] strs = condition.split(" ");
		for(String str  : strs){
			if(str.matches("[a-zA-Z_$][a-zA-Z0-9_$]*") && !str.trim().equals("null")){
				if(!testArgument.contains(str))
					testArgument.add(str);
				this.sqlFragment.addParameter(str);
			}
		}
	}

}
