package com.YaNan.frame.RPC.defaultConfigure;

import com.YaNan.frame.RPC.Implements.RPCListener;
import com.YaNan.frame.RPC.customer.RPCCustomerServiceRuntime;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.customer.Reconnection;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;

public class defaultRPCListener implements RPCListener{
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, defaultRPCListener.class);

	@Override
	public void InitCompleted(RPCService rpcService) {
		log.debug("inited ! configure info："+rpcService.getRpcConfigXML().toString());
	}

	@Override
	public void OnStarted(RPCService rpcService) {
		log.debug("rpc service is started");
	}

	@Override
	public void OnException(RPCService rpcService, Exception e) {
		log.error(e);
		e.printStackTrace();
	}

	@Override
	public void OnShutdown(RPCService rpcService) {
		log.debug("rpc service is stoped");
	}

	@Override
	public void OnLostConnection(RPCService rpcService) {
		log.debug("rpc service is lost connect");
	}

	@Override
	public void onRegistSuccess(RPCService rpcService) {
		log.debug("rpc service regist failed");
	}

	@Override
	public void onRegistFailed(RPCService rpcService) {
		log.debug("rpc service regist successed");
	}

	@Override
	public void onReconnected(RPCService manager, Reconnection reconnection, int i, RPCCustomerServiceRuntime runtime) {
		log.debug("try "+i+" connection，the result is:"+runtime.isAlive());
	}

	@Override
	public void OnReconnectFailed(RPCService manager) {
		log.debug("rpc service reconnection failed,rpc service stoped!");
	}

}
