package com.YaNan.frame.RPC.Implements;

import com.YaNan.frame.RPC.customer.RPCCustomerServiceRuntime;
import com.YaNan.frame.RPC.customer.RPCService;
import com.YaNan.frame.RPC.customer.Reconnection;

public interface RPCListener {


	void InitCompleted(RPCService rpcService);

	void OnStarted(RPCService rpcService);

	void OnException(RPCService rpcService, Exception e);

	void OnShutdown(RPCService rpcService);

	void OnLostConnection(RPCService rpcService);

	void onRegistSuccess(RPCService rpcService);

	void onRegistFailed(RPCService rpcService);

	void onReconnected(RPCService manager, Reconnection reconnection, int i, RPCCustomerServiceRuntime runtime);

	void OnReconnectFailed(RPCService manager);

}
