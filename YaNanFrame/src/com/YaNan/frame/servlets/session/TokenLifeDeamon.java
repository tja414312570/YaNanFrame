package com.YaNan.frame.servlets.session;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Token生命周期管理守护线程
 * @author yanan
 *
 */
class TokenLifeDeamon implements Runnable{
	private volatile boolean available;
	private int Intervals = 1000;
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
				if((int) (System.currentTimeMillis()/1000-token.getLastuse()/1000)>token.getTimeOut()){
					tokenEntryIterator.remove();
					token.destory();
				}
			}
			try {
				Thread.sleep(Intervals);
			} catch (InterruptedException e) {
			}
		}
	}
	public void shutdown(){
		available = false;
	}
}
