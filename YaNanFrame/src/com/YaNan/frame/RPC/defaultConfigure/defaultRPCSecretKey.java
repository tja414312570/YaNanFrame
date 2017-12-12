package com.YaNan.frame.RPC.defaultConfigure;

import com.YaNan.frame.RPC.Implements.SecretKey;

public class defaultRPCSecretKey implements SecretKey{

	@Override
	public Object getKey() {
		return "key";
	}

}
