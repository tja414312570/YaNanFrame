package com.YaNan.frame.RPC.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.core.AppContext;
import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.service.ClassInfo;
import com.YaNan.frame.service.Log;

@WebListener
@ClassInfo(version = 0)
public class InitRPCClient implements ServletContextListener {
	RPCService manager = RPCService.getManager();
	Thread thread = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Log.getSystemLog().write("application context destory ,destory the rpc service");
		if(manager!=null){
			manager.shutdown();
		}
		if(thread!=null){
			thread=null;
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
			Log.getSystemLog().write("waiting application context init...");
			thread = new Thread(){
				@Override
				public void run() {
					try {
						while(!AppContext.getContext().isInitCompleted()){
							System.out.println("rpc waiting context inited!");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						}
						Log.getSystemLog().write("application inited,try init RPC");
						
						manager.setConfigureFile(WebPath.getWebPath().get("RPCCustomer").toFile());
						manager.init();
						manager.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			};
			thread.start();
		
	}
	
}
