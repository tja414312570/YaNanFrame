package com.YaNan.frame.RPC.debug;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.exception.InvoketionException;
import com.YaNan.frame.RPC.exception.ServiceClosed;
import com.YaNan.frame.RPC.exception.ServiceInitException;
import com.YaNan.frame.RPC.exception.ServiceNoFound;
import com.YaNan.frame.RPC.exception.ServiceNoResponse;
import com.YaNan.frame.RPC.exception.UnKnowException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotInit;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRegistException;
import com.YaNan.frame.RPC.exception.customer.ServiceNotRunningException;
import com.google.gson.Gson;

public class socket {
public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	RPCService manager = RPCService.getManager();
	try {
		manager.init(new File("src/RPCCustomer.xml"));
	} catch (ServiceInitException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		manager.start();
		System.out.println(new Gson().toJson(manager.getRpcConf()));
	} catch (Exception e) {
		e.printStackTrace();
	}
	Object obj;
	try {
		Thread.sleep(2000);
		Long t1 = System.currentTimeMillis();
		obj = RPCService.getRPCRequest().request("USER","com.UFO.action.user.TokenUti", "getObject","IB14QGNBJC634A151B6FE4030AC192A509EA57513");
		System.out.println("excute result:"+obj);
		Long t2 = System.currentTimeMillis();
		System.out.println("ִexcute time:"+(t2-t1));
	} catch (ServiceNotRegistException | ServiceClosed | ServiceNoFound | ServiceNoResponse | UnKnowException | InvoketionException | ServiceNotRunningException | ServiceNotInit | TimeoutException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		manager.shutdown();
//
//	request.request("Test B",socket.class,"add",2,2);
//	//ssr.request(request);
//	System.out.println ("get response result:"+ssr.request(request));
}


}
