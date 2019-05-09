package com.YaNan.frame.plugin.beans;

import java.util.HashMap;
import java.util.Map;

import com.YaNan.frame.plugin.PluginInitException;

public class BeanManagerLock {
	private static volatile Map<Class<?>,BeanManagerLock> lockMap = new HashMap<Class<?>,BeanManagerLock>();
	public static void tryLock(Class<?> key){
		BeanManagerLock locker = lockMap.get(key);
		if(locker==null){
			synchronized (lockMap) {
				locker = new BeanManagerLock();
				lockMap.put(key, locker);
			}
		}else{
			synchronized (locker) {
				try {
					locker.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new PluginInitException("failed to get bean manager lock!",e);
				}
			}
		}
		
	}
	public static void release(Class<?> key){
		BeanManagerLock lock = lockMap.get(key);
		synchronized (lock) {
			lock.notify();
			lockMap.remove(key);
		}
	}
}
