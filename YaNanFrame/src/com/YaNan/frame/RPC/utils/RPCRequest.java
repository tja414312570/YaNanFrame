package com.YaNan.frame.RPC.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.YaNan.frame.RPC.Implements.MessagePrototypeInterface;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.exception.InvoketionException;
import com.YaNan.frame.RPC.exception.ServiceClosed;
import com.YaNan.frame.RPC.exception.ServiceNotFound;
import com.YaNan.frame.RPC.exception.ServiceNotResponse;
import com.YaNan.frame.RPC.exception.UnKnowException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRegistException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;

public class RPCRequest implements Serializable,MessagePrototypeInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String serviceName;
	private String requestClass;
	private String requestMethod;
	private Object[] requestParmeters;
	private String RUID;
	private String SUID;
	
	public void setRequestClass(String requestClass) {
		this.requestClass = requestClass;
	}
	public RPCRequest() {
		this.RUID = UUID.randomUUID().toString();
	}
	@Override
	public String getRUID() {
		return RUID;
	}
	@Override
	public void setRUID(String rUID) {
		RUID = rUID;
	}
	@Override
	public String getServiceName() {
		return serviceName;
	}
	@Override
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getRequestClass() {
		return requestClass;
	}
	public void setRequestClass(Class<?> requestClass) {
		this.requestClass = requestClass.getName();
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public Object[] getRequestParmeters() {
		return requestParmeters;
	}
	public void setRequestParmeters(Object[] requestParmeters) {
		this.requestParmeters = requestParmeters;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	@Override
	public String toString() {
		return "Request [serviceName=" + serviceName + ", requestClass="
				+ requestClass + ", requestMethod=" + requestMethod
				+ ", requestParmeters=" + Arrays.toString(requestParmeters)
				+ ", RUID=" + RUID + ", SUID=" + SUID + "]";
	}
	public String getSUID() {
		return SUID;
	}
	public void setSUID(String sUID) {
		SUID = sUID;
	}
	public Object request() throws TimeoutException, ServiceClosed, ServiceNotFound, ServiceNotResponse, UnKnowException, InterruptedException, ExecutionException, InvoketionException, ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceNotRunningException, ServiceNotRegistException, ServiceNotInit{
		return RPCService.getManager().getRPCServiceRuntime().request(this);
	}
	public Object request(String serviceName, Class<?> requestClass, String requestMethod,
			Object...requestParmeters) throws TimeoutException, ServiceClosed, ServiceNotFound, ServiceNotResponse, UnKnowException, InterruptedException, ExecutionException, InvoketionException, ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceNotRunningException, ServiceNotRegistException, ServiceNotInit {
		this.serviceName = serviceName;
		this.requestClass = requestClass.getName();
		this.requestMethod = requestMethod;
		this.requestParmeters = requestParmeters;
		return this.request();
	}
	public Object request(String serviceName, String requestClass, String requestMethod, Object...requestParmeters) throws TimeoutException, ServiceClosed, ServiceNotFound, ServiceNotResponse, UnKnowException, InterruptedException, ExecutionException, InvoketionException,  ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceNotRunningException, ServiceNotRegistException, ServiceNotInit {
		this.serviceName = serviceName;
		this.requestClass = requestClass;
		this.requestMethod = requestMethod;
		this.requestParmeters = requestParmeters;
		return this.request();
	}

}
