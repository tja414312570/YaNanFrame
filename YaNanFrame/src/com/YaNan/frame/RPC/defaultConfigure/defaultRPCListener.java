package com.YaNan.frame.RPC.defaultConfigure;

import com.YaNan.frame.RPC.Implements.RPCListener;
import com.YaNan.frame.RPC.customer.RPCCustomerServiceRuntime;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.customer.Reconnection;
import com.YaNan.frame.service.Log;

public class defaultRPCListener implements RPCListener{

	@Override
	public void InitCompleted(RPCService rpcService) {
		Log.getSystemLog().write("inited ! configure info："+rpcService.getRpcConfigXML().toString());
	}

	@Override
	public void OnStarted(RPCService rpcService) {
		Log.getSystemLog().write("rpc service is started");
	}

	@Override
	public void OnException(RPCService rpcService, Exception e) {
		Log.getSystemLog().write("catch exception : "+e.toString());
	e.printStackTrace();
		
	}

	@Override
	public void OnShutdown(RPCService rpcService) {
		Log.getSystemLog().write("rpc service is stoped");
	}

	@Override
	public void OnLostConnection(RPCService rpcService) {
		Log.getSystemLog().write("rpc service is lost connect");
	}

	@Override
	public void onRegistSuccess(RPCService rpcService) {
		Log.getSystemLog().write("rpc service regist failed");
	}

	@Override
	public void onRegistFailed(RPCService rpcService) {
		Log.getSystemLog().write("rpc service regist successed");
	}

	@Override
	public void onReconnected(RPCService manager, Reconnection reconnection, int i, RPCCustomerServiceRuntime runtime) {
		Log.getSystemLog().write("try "+i+" connection，the result is:"+runtime.isAlive());
	}

	@Override
	public void OnReconnectFailed(RPCService manager) {
		Log.getSystemLog().write("rpc service reconnection failed,rpc service stoped!");
	}

}
