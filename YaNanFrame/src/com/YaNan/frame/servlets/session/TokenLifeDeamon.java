package com.YaNan.frame.servlets.session;

import java.util.Iterator;
import java.util.Map.Entry;

class TokenLifeDeamon implements Runnable{
	private volatile boolean available;
	private int Intervals = 60000;
	public TokenLifeDeamon(){
		available = true;
	}
	@Override
	public void run() {
		while(available){
			Iterator<Entry<String, Token>> tokenEntryIterator = TokenPool.getTokenMap().entrySet().iterator();
			Token token = null;
			while(tokenEntryIterator.hasNext()){
				token = tokenEntryIterator.next().getValue();
				if((int) (System.currentTimeMillis()/1000-token.getLastuse()/1000)>token.getTimeOut())
					token.destory();
			}
			try {
				Thread.sleep(Intervals);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void shutdown(){
		available = false;
	}
}
