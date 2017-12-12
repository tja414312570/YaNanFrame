package com.YaNan.frame.RPC.TokenSupport;

import com.YaNan.frame.RPC.TokenSupport.Implements.ActionType;
import com.YaNan.frame.core.session.servletSupport.TokenServlet;

public class TokenAction extends TokenServlet{
	private int action;
	private String key;
	private String value;
	private String serviceName;
	private String SUID;
	private String excuteClass;
	private String serviceAddr;
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getExcuteClass() {
		return excuteClass;
	}
	public void setExcuteClass(String excuteClass) {
		this.excuteClass = excuteClass;
	}
	@Override
	public String execute(){
		if(this.key==null||this.key.equals(""))
			return Response.Failed("TOKEN KEY IS NULL ");
		if(this.value==null||this.value.equals(""))
			return Response.Failed("TOKEN VALUE IS NULL ");
		System.out.println(this.toString());
		StringBuffer url = this.RequestContext.getRequestURL();
		switch(this.action){
		case ActionType.BIND:
			this.bind();
			return "<script>location.href ='"+serviceAddr+"?'+encodeURI(\""+Response.Success(url.toString())+"\");</script>";
		default:
			return "<script>location.href ='"+serviceAddr+"?'+encodeURI("+Response.Failed(url.toString())+");</script>";
		}
	}
	private void bind() {
		TokenContext.set(key, value);
		RPCTokenServicer.bind(this);
	}
	public String getServiceAddr() {
		return serviceAddr;
	}
	public void setServiceAddr(String serviceAddr) {
		this.serviceAddr = serviceAddr;
	}
	@Override
	public String toString() {
		return "TokenAction [action=" + action + ", key=" + key + ", value=" + value + ", serviceName=" + serviceName
				+ ", SUID=" + SUID + ", excuteClass=" + excuteClass + ", serviceAddr=" + serviceAddr + "]";
	}
	
}
