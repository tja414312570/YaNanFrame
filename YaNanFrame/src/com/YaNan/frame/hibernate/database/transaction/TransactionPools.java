package com.YaNan.frame.hibernate.database.transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionPools {
	private static TransactionPools pools;
	private TransactionPools(){};
	public static TransactionPools getInstance(){
		if(pools ==null)
			pools = new TransactionPools();
		return pools;
	}
	private Map<Integer,String> map = new HashMap<Integer,String>();
	
}
