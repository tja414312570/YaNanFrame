package com.YaNan.frame.RPC.init;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.exception.ServiceInitException;
import com.YaNan.frame.RPC.exception.ServiceIsRunning;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

@WebListener
public class InitRPCClient implements ServletContextListener {
	RPCService manager = RPCService.getManager();
	Thread thread = null;
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, InitRPCClient.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.debug("application context destory ,destory the rpc service");
		if(manager!=null){
			manager.shutdown();
		}
		if(thread!=null){
			thread=null;
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
			log.debug("application inited,try init RPC");
			File file = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20", " "),"RPCCustomer.xml");
			try {
				if(file.exists()){
					manager.setConfigureFile(file);
					manager.init();
					manager.start();
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| ServiceInitException | ServiceNotInit | ServiceNotRunningException | ServiceIsRunning
					| IOException e) {
				e.printStackTrace();
				log.error(e);
			}
		
	}
	
}
