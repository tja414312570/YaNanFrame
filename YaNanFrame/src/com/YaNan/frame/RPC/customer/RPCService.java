package com.YaNan.frame.RPC.customer;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.YaNan.frame.RPC.Implements.RPCListener;
import com.YaNan.frame.RPC.Implements.SecretKey;
import com.YaNan.frame.RPC.TokenSupport.RPCToken;
import com.YaNan.frame.RPC.exception.ServiceInitException;
import com.YaNan.frame.RPC.exception.ServiceIsRunning;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
import com.YaNan.frame.RPC.utils.RPCConfigXML;
import com.YaNan.frame.RPC.utils.RPCConfigure;
import com.YaNan.frame.RPC.utils.RPCRequest;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.util.beans.XMLBean;

public class RPCService {
	private static RPCService manager;
	//rpc配置
	private RPCConfigure rpcConf;
	//rpc配置文件
	private File rpcXmlFile;
	
	//rpc配置xmlBean
	private RPCConfigXML rpcConfigXML;
	//rpc运行类
	private RPCCustomerServiceRuntime runtime;
	//rpc运行线程
	private Thread RPCthread;
	//rpc监听
	private RPCListener listener;
	//重连次数
	private int reconnectionTimes=10;
	private Thread reconnectThread;
	private Reconnection reconnectThreadObj;
	public RPCConfigXML getRpcConfigXML() {
		return rpcConfigXML;
	}
	public void setRpcConfigXML(RPCConfigXML rpcConfigXML) {
		this.rpcConfigXML = rpcConfigXML;
	}
	public RPCConfigure getRpcConf() {
		return manager.rpcConf;
	}
	public void setRpcConf(RPCConfigure rpcConf) {
		manager.rpcConf = rpcConf;
	}
	public File getConfigureFile() {
		return manager.rpcXmlFile;
	}
	public void setConfigureFile(File rpcXmlFile) {
		manager.rpcXmlFile = rpcXmlFile;
	}
	public static RPCService getManager(){
		if(manager==null)
			manager=new RPCService();
		return manager;
	}

	public void start() throws ServiceNotInit, ServiceInitException, ServiceNotRunningException, UnknownHostException, IOException, ServiceIsRunning{
		if(runtime==null)
			throw new ServiceNotInit();
		if(RPCthread!=null&&RPCthread.isAlive())
			throw new ServiceIsRunning();
		runtime.init();
		RPCthread = new Thread(runtime);
		try {
			if(runtime.regist()){
				RPCthread.start();
				if(this.listener!=null)
					this.listener.OnStarted(this);
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceInitException{
		if(this.rpcXmlFile!=null){
			this.init(rpcXmlFile);
			return;
		}
		if(this.rpcConfigXML!=null){
			this.init(rpcConfigXML);
			return;
		}
		if(this.rpcConf!=null){
			this.init(rpcConf);
			return;
		}
		this.rpcXmlFile = WebPath.getWebPath().get("RPCXml").toFile();
		this.init(this.rpcXmlFile);
	}
	public void init(File rpcXmlFile) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceInitException {
		this.rpcXmlFile=rpcXmlFile;
		XMLBean bean = new XMLBean();
		bean.addXMLFile(rpcXmlFile);
		bean.setBeanClass(RPCConfigXML.class);
		bean.setNodeName("rpc");
		bean.addElementPath("/");
		rpcConfigXML = (RPCConfigXML) bean.execute().get(0);
		this.init(rpcConfigXML);
		
	}
	public void init(RPCConfigXML rpcConfigXML) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ServiceInitException {
		this.rpcConfigXML = rpcConfigXML;
		Class<?>  listenerClass = Class.forName(rpcConfigXML.getListener());
		if(ClassLoader.implementOf(listenerClass, RPCListener.class)){
			this.listener=(RPCListener) listenerClass.newInstance();
		}else
			throw new ServiceInitException("listener class must implements RPCListener interface at "+listenerClass);
		rpcConf = new RPCConfigure();
		Class<?>  secretClass = Class.forName(rpcConfigXML.getSecret());
		if(ClassLoader.implementOf(secretClass, SecretKey.class)){
			SecretKey sk = (SecretKey) secretClass.newInstance();
			rpcConf.setKey(sk.getKey());
		}else
			throw new ServiceInitException("secret key class must implements SecretKey interface at "+secretClass);
		this.setReconnectionTimes(rpcConfigXML.getReconnectionTimes());
		rpcConf.setHost(rpcConfigXML.getHost());
		rpcConf.setName(rpcConfigXML.getName());
		rpcConf.setPort(rpcConfigXML.getPort());
		rpcConf.setTimeout(rpcConfigXML.getTimeout());
		this.init(rpcConf);
	}
	public void init(RPCConfigure rpcConf) {
		this.rpcConf=rpcConf;
		runtime = new RPCCustomerServiceRuntime(rpcConf,this);
		if(this.listener!=null)
			this.listener.InitCompleted(this);
	}
	/**
	 * get rpc runtime object
	 * @return
	 * @throws ServiceNotInitException
	 */
	public RPCCustomerServiceRuntime getRPCServiceRuntime() throws ServiceNotInit{
		if(this.runtime==null)
			throw new ServiceNotInit();
		return this.runtime;
	}
	/**
	 * get a rpc request object
	 * @return
	 * @throws ServiceNotRuningException
	 * @throws ServiceNotInitException
	 */
	public static RPCRequest getRPCRequest() throws ServiceNotRunningException, ServiceNotInit{
		if(manager==null)
			throw new ServiceNotInit();
		if(manager.RPCthread==null)
			throw new ServiceNotRunningException();
		return manager.getRPCServiceRuntime().getRequest();
	}
	public void shutdown(){
		if(manager.RPCthread!=null&&manager.RPCthread.isAlive())
			manager.runtime.close();
		if(this.listener!=null)
			this.listener.OnShutdown(this);
			
	}
	public void exception(Exception e) {
		e.printStackTrace();
		if(this.listener!=null)
			this.listener.OnException(this,e);
	}
	public RPCListener getListener() {
		return listener;
	}
	public void setListener(RPCListener listener) {
		this.listener = listener;
	}
	public int getReconnectionTimes() {
		return reconnectionTimes;
	}
	public void setReconnectionTimes(int reconnectionTimes) {
		this.reconnectionTimes = reconnectionTimes;
	}
	public void lostConnection() {
		if(this.listener!=null)
			this.listener.OnLostConnection(this);
		this.shutdown();
		this.reStart();
	}
	public synchronized void reStart(){
		if(reconnectThread!=null&&reconnectThread.isAlive())
			reconnectThreadObj.close();
		try {
			this.init();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ServiceInitException e) {
			e.printStackTrace();
		}
		reconnectThreadObj = new Reconnection(reconnectionTimes, runtime, manager);
		reconnectThreadObj.enable();
		reconnectThread =new Thread(reconnectThreadObj);
		reconnectThread.start();
	}
	public void registFailed() {
		if(this.listener!=null){
			this.listener.onRegistSuccess(this);
		}
		this.shutdown();
		
	}
	public void registSuccess() {
		if(this.listener!=null){
			this.listener.onRegistFailed(this);
		}
	}
	public static RPCToken getRPCToken(String serviceName,Class<?> requestBeanClass,String tokenId,int RPCType,int RPCRequestType) {
		return new RPCToken(serviceName, requestBeanClass,tokenId, RPCRequestType, RPCRequestType);
	}
	public static RPCToken getRPCToken(Token token, String serviceName, Class<?> cls, int RPCType,
			int RequestType) {
		return new RPCToken(token, serviceName, cls, RPCType,RequestType);
	}
	
}