package com.YaNan.frame.plugs;

import java.util.ArrayList;
import java.util.List;

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
	private List<RegisterDescription> registerList = new ArrayList<RegisterDescription>();
	public Plug(PlugsDescription descrption){
		this.description = descrption;
	}
	public void addRegister(RegisterDescription registerDescription) {
		//设置默认注册组件
		if(this.defaultRegisterDescription==null)
			this.defaultRegisterDescription = registerDescription;
		else if(this.defaultRegisterDescription.getPriority()<registerDescription.getPriority())
			this.setDefaultRegisterDescription(registerDescription);
		//为了保持与默认注册组件有相同的优先级，采用倒叙对比法进行优先级运算 比如 原始数据 0  0  2  3 现在需要插入 1  则插入后应该为 0 0 1 2 3
		if(this.registerList.size()==0){
			this.registerList.add(registerDescription);
		}else{
			for(int i=this.registerList.size()-1;i>=0;i--){
				if(registerDescription.getPriority()>=this.registerList.get(i).getPriority()){
					this.registerList.add(registerDescription);
					break;
				}else{
					if(i==0){
						this.registerList.add(0,registerDescription);
					}else{
						if(registerDescription.getPriority()>=this.registerList.get(i-1).getPriority()&&
								registerDescription.getPriority()<this.registerList.get(i).getPriority()){
							this.registerList.add(i,registerDescription);
							break;
						}
					}
				}
			}
		}
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
