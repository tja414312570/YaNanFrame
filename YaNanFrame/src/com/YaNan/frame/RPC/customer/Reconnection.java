package com.YaNan.frame.RPC.customer;

import java.io.IOException;

import com.YaNan.frame.RPC.exception.ServiceInitException;
import com.YaNan.frame.RPC.exception.ServiceIsRunning;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
public class Reconnection implements Runnable{
	private boolean alive;
	private int reconnectionTimes;
	private RPCCustomerServiceRuntime runtime;
	private RPCService manager;
	public Reconnection(int reconnectionTimes, RPCCustomerServiceRuntime runtime,  RPCService manager2) {
		this.reconnectionTimes = reconnectionTimes;
		this.runtime = runtime;
		this.manager = manager2;
	}
	public void enable(){
		this.alive=true;
	}
	@Override
	public void run() {
		int i = 0;
			while(i<reconnectionTimes&&alive){
				i++;
				try {
					manager.start();
				} catch (ServiceNotInit | ServiceInitException | ServiceNotRunningException | IOException e1) {
					e1.printStackTrace();
				} catch (ServiceIsRunning e) {
					e.printStackTrace();
				}finally{
					int j =0;
					while(!runtime.isRegist()&&j<runtime.getResponseTimeout()){
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						j++;
					}
					System.out.println("第"+i+"次链接结果："+runtime.isAlive());
					if(runtime.isAlive()){
						if(manager.getListener()!=null)
							manager.getListener().onReconnected(manager,this,i,runtime);
						break;
					}
					else
						runtime.close();
				}
			}
			if(!runtime.isAlive()&&manager.getListener()!=null)
				manager.getListener().OnReconnectFailed(manager);
				
		}
	public void close(){
		this.alive=false;
	}
}
