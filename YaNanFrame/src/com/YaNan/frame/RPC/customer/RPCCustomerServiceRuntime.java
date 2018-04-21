package com.YaNan.frame.RPC.customer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import com.YaNan.frame.RPC.Implements.RPCExceptionType;
import com.YaNan.frame.RPC.exception.InvoketionException;
import com.YaNan.frame.RPC.exception.RPCException;
import com.YaNan.frame.RPC.exception.ServiceClosed;
import com.YaNan.frame.RPC.exception.ServiceNoFound;
import com.YaNan.frame.RPC.exception.ServiceNoResponse;
import com.YaNan.frame.RPC.exception.UnKnowException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRegistException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
import com.YaNan.frame.RPC.utils.RPCConfigure;
import com.YaNan.frame.RPC.utils.RPCRequest;
import com.YaNan.frame.RPC.utils.RPCServiceInfo;
import com.YaNan.frame.RPC.utils.RegistResult;
import com.YaNan.frame.RPC.utils.Response;
import com.YaNan.frame.RPC.utils.ResponseType;
import com.YaNan.frame.reflect.ClassLoader;

public class RPCCustomerServiceRuntime implements Runnable{
	private Socket socket;
	private boolean alive;
	
	private InputStream is;
	private OutputStream os;
	/**
	 * 三个缓存库
	 */
	private Map<String,Object> requestBuffer = new HashMap<String,Object>();
	private Map<String,Integer> requestRecorde = new HashMap<String,Integer>();
	private Map<String,RPCException> exceptionBuffer = new HashMap<String,RPCException>();
	private int ResponseTimeout=5000;
	private RPCService rpcManager;
	private boolean isRegist=false;
	private boolean ithreadAlived = true;
	private RPCServiceInfo rpcServiceInfo;
	private RegistResult resultResult;
	private RPCConfigure rpcConf;
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public boolean isRegist() {
		return isRegist;
	}
	public void setRegist(boolean isRegist) {
		this.isRegist = isRegist;
	}
	public RPCCustomerServiceRuntime(RPCConfigure rpcConf,RPCService rpcManager) {
		this.rpcManager=rpcManager;
		this.rpcConf = rpcConf;
	}
	public void init(){
		try {
			this.ResponseTimeout = rpcConf.getTimeout()==0?this.ResponseTimeout:rpcConf.getTimeout();
			this.socket = new Socket(rpcConf.getHost(),rpcConf.getPort());
			this.os=this.socket.getOutputStream();
			rpcServiceInfo = new RPCServiceInfo(rpcConf.getName(),rpcConf.getKey());
			this.isRegist=false;
			this.alive=false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Boolean regist() throws TimeoutException, InterruptedException, ExecutionException, ServiceNotRunningException{
		if(this.socket==null||this.socket.isClosed())
			throw new ServiceNotRunningException();
		this.write(rpcServiceInfo);
		final ExecutorService exec = Executors.newFixedThreadPool(1);  
        Callable<RegistResult> call = new Callable<RegistResult>() {  
            public RegistResult call() throws Exception {  
				try {
					while(isRegist==false&&ithreadAlived){
						is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						resultResult= (RegistResult) ois.readObject();
						break;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	return resultResult;
            }  
        };  
        try {  
            Future<RegistResult> future = exec.submit(call);  
            RegistResult ri = future.get(ResponseTimeout, TimeUnit.MILLISECONDS);
            if(ri.getStatus()==200){
            	this.isRegist=true;
            	alive = true;
            	rpcManager.registSuccess();
            }
            else{
            	rpcManager.registFailed();
            }
            exec.shutdown(); 
            return isRegist;
        } catch (TimeoutException ex) {  
        	ithreadAlived=false;
            throw ex;
        } catch (InterruptedException e) {
        	ithreadAlived=false;
			throw e;
		} catch (ExecutionException e) {
			ithreadAlived=false;
			throw e;
		} 
	}
	public void write(Object object){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.os);
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			rpcManager.exception(e);
		}
		
	}
	@Override
	public void run() {
		try {
			if(this.socket==null)
				alive=false;
			if(!this.isRegist)
				rpcManager.exception(new ServiceNotRegistException());
			while(alive&&isRegist){
				try {
					this.is = this.socket.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
					if(this.alive)
						rpcManager.lostConnection();
				}
				
				ObjectInputStream ois = new ObjectInputStream(is);
				Object obj = ois.readObject();
				this.excuteCommand(obj);
			}
		} catch (ClassNotFoundException e) {
			rpcManager.exception(e);
		} catch (IOException e) {
			e.printStackTrace();
			if(this.alive)
				rpcManager.lostConnection();
		}
	}
	private void excuteCommand(Object obj) throws ClassNotFoundException {
		Class<?> cmdCls = obj.getClass();
		if(cmdCls.equals(RPCRequest.class)){
			RPCRequest request = (RPCRequest) obj;
			Response response = new Response();
			response.setRUID(request.getRUID());
			response.setSUID(request.getSUID());
			response.setServiceName(request.getServiceName());
			try {
				ClassLoader loader = new ClassLoader(request.getRequestClass());
				Object[] requestParms = request.getRequestParmeters();
				Class<?>[] parmsType =new Class<?>[requestParms.length];
				for(int i =0;i<requestParms.length;i++){
					parmsType[i]=ClassLoader.patchBaseType(requestParms[i]);
				}
				Object rObj = loader.invokeMethod(request.getRequestMethod(),parmsType,requestParms);
				response.setResponseResult(rObj);
				this.write(response);
			} catch (Throwable e) {
				this.write(new InvoketionException(request.getServiceName(), e.toString()+(e.getCause()==null?"":"\nCase by:"+e.getCause()), request.getRUID(), request.getSUID()));
				if(rpcManager.getListener()!=null)
					rpcManager.getListener().OnException(rpcManager, (Exception) e);
				//	rpcManager.exception(e);
			}
			
		}
		if(cmdCls.equals(Response.class)){
			Response response = (Response) obj;
			if(this.requestBuffer.containsKey(response.getRUID())){
				this.requestBuffer.replace(response.getRUID(), response.getResponseResult());
				this.requestRecorde.replace(response.getRUID(),ResponseType.RESPONSE);
			}
		}
		if(cmdCls.getSuperclass().equals(RPCException.class)||cmdCls.equals(RPCException.class))
		{
			this.exception(obj);
		}
		
	}
	
	private void exception(Object obj) {
		RPCException rpcException = (RPCException) obj;
		String RUID=rpcException.getRUID();
		this.requestRecorde.replace(RUID, ResponseType.EXCEPTION);
		this.exceptionBuffer.put(RUID, rpcException);
	}
	public void setThread(Thread t) {
	}
	public RPCRequest getRequest() {
		return new RPCRequest();
	}
	/**
	 * 发起RPC请求
	 * @param request
	 * @return
	 * @throws TimeoutException
	 * @throws ServiceClosed
	 * @throws ServiceNoFound
	 * @throws ServiceNoResponse
	 * @throws UnKnowException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws InvoketionException
	 * @throws ServiceNotRunningException 
	 * @throws ServiceNotRegistException 
	 */
	public Object request(RPCRequest request) throws TimeoutException, ServiceClosed, ServiceNoFound, ServiceNoResponse, UnKnowException, InterruptedException, ExecutionException, InvoketionException, ServiceNotRunningException, ServiceNotRegistException {
		if(this.socket==null||this.socket.isClosed())
			throw new ServiceNotRunningException();
		if(!this.isRegist)
			throw new ServiceNotRegistException();
		this.write(request);
		final String RUID = request.getRUID();
		this.requestBuffer.put(RUID,null);
		this.requestRecorde.put(RUID, 0);
		final ExecutorService exec = Executors.newFixedThreadPool(1);  
        Callable<Object> call = new Callable<Object>() {  
            public Object call() throws Exception {  
            	if(requestRecorde.containsKey(RUID))
            		while(requestRecorde.get(RUID)==ResponseType.NO_RESPONSE)
            			Thread.sleep(1);
            	if(requestRecorde.get(RUID)==ResponseType.RESPONSE){
            		return requestBuffer.get(RUID);  
            	}
            	if(requestRecorde.get(RUID)==ResponseType.EXCEPTION){
            		return exceptionBuffer.get(RUID);
            	}
            	return null;
            }  
        };  
        try {  
            Future<Object> future = exec.submit(call);  
            Object ri = future.get(ResponseTimeout, TimeUnit.MILLISECONDS);
            exec.shutdown(); 
            if(requestRecorde.get(RUID)==ResponseType.EXCEPTION)
            	this.throwException((RPCException) ri);
            return ri;
        } catch (TimeoutException ex) {  
            throw ex;
        } catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			throw e;
		} finally{
			this.requestBuffer.remove(RUID);
            this.requestRecorde.remove(RUID);
            this.exceptionBuffer.remove(RUID);
		}
	}
	public void throwException(RPCException rpcException) throws ServiceClosed, ServiceNoFound, ServiceNoResponse, UnKnowException, InvoketionException{
		switch(rpcException.getCode()){
		case RPCExceptionType.SERVICE_CLOSED:
			ServiceClosed exception=(ServiceClosed) rpcException;
			throw new ServiceClosed(exception.getServiceName(),exception.getSUID(),exception.getRUID());
		case RPCExceptionType.SERVICE_NO_FOUND:
			ServiceNoFound snfException=(ServiceNoFound) rpcException;
			throw new ServiceNoFound(snfException.getServiceName(),snfException.getRUID());
		case RPCExceptionType.SERVICE_NOT_RESPONSE:
			ServiceNoResponse snrException=(ServiceNoResponse) rpcException;
			throw new ServiceNoResponse(snrException.getServiceName(),snrException.getSUID(),snrException.getRUID());
		case RPCExceptionType.INVOKETION_EXCEPTION:
			InvoketionException ivException=(InvoketionException) rpcException;
			throw new InvoketionException(ivException.getServiceName(),ivException.getException(),ivException.getSUID(),ivException.getRUID());
		default:
			UnKnowException ue = (UnKnowException) rpcException;
			throw new UnKnowException(ue.getServiceName(),rpcException.getSUID(),rpcException.getRUID());
		}
	}
	public void close(){
		this.alive=false;
		this.isRegist=false;
		try {
			if(is!=null)
				is.close();
			if(os!=null)
				os.close();
			if(socket!=null&&!socket.isClosed())
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void destory() throws IOException{
		if(is!=null)
			is.close();
		if(os!=null)
			os.close();
		if(socket!=null&&!socket.isClosed())
			socket.close();
		if(socket!=null)
			socket=null;
		requestBuffer.clear();
		requestRecorde.clear();
		exceptionBuffer.clear();
	}
	public void clean() {
	}
	public int getResponseTimeout() {
		return ResponseTimeout;
	}
	public void setResponseTimeout(int responseTimeout) {
		ResponseTimeout = responseTimeout;
	}
	public RPCServiceInfo getRpcServiceInfo() {
		return rpcServiceInfo;
	}
	public void setRpcServiceInfo(RPCServiceInfo rpcServiceInfo) {
		this.rpcServiceInfo = rpcServiceInfo;
	}
	public RPCConfigure getRpcConf() {
		return rpcConf;
	}
	public void setRpcConf(RPCConfigure rpcConf) {
		this.rpcConf = rpcConf;
	}
	
}
