package com.YaNan.frame.RPC.TokenSupport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.YaNan.frame.RPC.Implements.RequestType;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.exception.InvoketionException;
import com.YaNan.frame.RPC.exception.ServiceClosed;
import com.YaNan.frame.RPC.exception.ServiceNoFound;
import com.YaNan.frame.RPC.exception.ServiceNoResponse;
import com.YaNan.frame.RPC.exception.UnKnowException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRegistException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
import com.YaNan.frame.RPC.utils.RPCRequest;
import com.YaNan.frame.servlets.session.Token;

public class RPCToken {
	private String serviceName;
	private String beanClass;
	private Class<?> beanType;
	private String tokenId;
	private RPCRequest rpcRequest;
	private int RPCType;
	private int RPCRequestType;
	private String requestMethod;
	private Token token;
	private boolean ready=true;
	public RPCToken(String serviceName,Class<?> requestBeanClass,String tokenId,int RPCType,int RPCRequestType){
		if(serviceName==null||serviceName.equals("")){
			this.ready=false;
			return;
		}
		this.serviceName=serviceName;
		this.beanClass=requestBeanClass.getName();
		this.beanType=requestBeanClass;
		this.tokenId=tokenId;
		this.RPCType=RPCType;
		this.RPCRequestType = RPCRequestType;
		switch(RPCRequestType){
		case RequestType.GET:
			this.requestMethod = "request";
		}
		try {
			this.rpcRequest = RPCService.getRPCRequest();
		} catch (ServiceNotRunningException | ServiceNotInit e) {
			e.printStackTrace();
		}
	}
	public Class<?> getBeanType() {
		return beanType;
	}
	public void setBeanType(Class<?> beanType) {
		this.beanType = beanType;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public RPCToken (Token token,String serviceName,Class<?> requestBeanClass,int RPCType,int RPCRequestType){
		if(serviceName==null||serviceName.equals("")){
			this.ready=false;
			return;
		}
		this.serviceName=serviceName;
		this.beanClass=requestBeanClass.getName();
		this.beanType=requestBeanClass;
		this.token = token;
		this.tokenId = this.token.getTokenId();
		this.RPCType=RPCType;
		this.RPCRequestType = RPCRequestType;
		switch(RPCRequestType){
		case RequestType.GET:
			this.requestMethod = "request";
		}
		try {
			this.rpcRequest = RPCService.getRPCRequest();
		} catch (ServiceNotRunningException | ServiceNotInit e) {
			e.printStackTrace();
		}
	}
	public Object request(){
		if(!this.ready)return null;
		try {
			return this.rpcRequest.request(serviceName,RPCTokenDispatcher.class.getName(),this.requestMethod,this.tokenId,this.beanClass);
		} catch (ServiceClosed | ServiceNoFound | ServiceNoResponse | UnKnowException | InvoketionException
				| ClassNotFoundException | InstantiationException | IllegalAccessException | ServiceNotRunningException
				| ServiceNotRegistException | ServiceNotInit | TimeoutException | InterruptedException
				| ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getBeanClass() {
		return beanClass;
	}
	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public RPCRequest getRpcRequest() {
		return rpcRequest;
	}
	public void setRpcRequest(RPCRequest rpcRequest) {
		this.rpcRequest = rpcRequest;
	}
	public int getRPCType() {
		return RPCType;
	}
	public void setRPCType(int rPCType) {
		RPCType = rPCType;
	}
	public int getRPCRequestType() {
		return RPCRequestType;
	}
	public void setRPCRequestType(int rPCRequestType) {
		RPCRequestType = rPCRequestType;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
}
