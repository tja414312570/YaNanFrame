package com.YaNan.frame.plugs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.stringSupport.StringUtil;

/**
 * 用于描述组件信息
 * 一个接口对应一个组件
 * @author yanan
 *
 */
public class Plug {
	//组件的描述类型 0==>注解   1==>配置
	private PlugsDescription description;
	//默认的实现类，用于提高默认获取组件的效率
	private RegisterDescription defaultRegisterDescription;
	//组件的实现类 mark.hash() ==>cls 用于根据标示查询实现类
	private int currentPriority=0;
	private Map<String,Class<?>> registermap = new LinkedHashMap<String,Class<?>>();
	private List<RegisterDescription> registerList = new ArrayList<RegisterDescription>();
	public Plug(PlugsDescription descrption){
		this.description = descrption;
	}
	public void addRegister(RegisterDescription registerDescription) {
		//处理优先级
		if(currentPriority<registerDescription.getPriority()){
			this.currentPriority = registerDescription.getPriority();
			this.setDefaultRegisterDescription(registerDescription);
		}else if(this.defaultRegisterDescription==null){
			this.defaultRegisterDescription = registerDescription;
		}
		this.registerList.add(registerDescription.getPriority()>this.registerList.size()?this.registerList.size():registerDescription.getPriority(), registerDescription);
	}
	public RegisterDescription getRegisterDescriptionByAttribute(String attribute) {
		for(int i = 0;i<registerList.size();i++){
			if(StringUtil.match(attribute, registerList.get(i).getAttribute()))
				return registerList.get(i);
		}
		return defaultRegisterDescription;
	}
	public PlugsDescription getDescription() {
		return description;
	}
	public void setDescription(PlugsDescription description) {
		this.description = description;
	}
	public int getCurrentPriority() {
		return currentPriority;
	}
	public void setCurrentPriority(int currentPriority) {
		this.currentPriority = currentPriority;
	}
	public Map<String, Class<?>> getRegistermap() {
		return registermap;
	}
	public void setRegistermap(Map<String, Class<?>> registermap) {
		this.registermap = registermap;
	}
	public List<RegisterDescription> getRegisterList() {
		return registerList;
	}
	public void setRegisterList(List<RegisterDescription> registerList) {
		this.registerList = registerList;
	}
	public RegisterDescription getDefaultRegisterDescription() {
		return defaultRegisterDescription;
	}
	public void setDefaultRegisterDescription(RegisterDescription defaultRegisterDescription) {
		this.defaultRegisterDescription = defaultRegisterDescription;
	}
	public List<RegisterDescription> getRegisterDescriptionList() {
		return this.registerList;
	}
	
	
}
