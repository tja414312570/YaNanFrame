package com.YaNan.frame.utils;

import java.util.ArrayList;
import java.util.List;

public class SchemeManager implements Runnable{
	private static SchemeManager instance;
	private Thread damon;
	private int minThread = 3;//最小线程数
	private int maxThread = 6;//最大线程数
	private boolean available=false;
	private List<Thread> threadPools;//总线程池
	private List<Thread> busyPools;//使用的线程
	private List<Scheme> schemePools = new ArrayList<Scheme>();
	private SchemeManager(){
		damon = new Thread(this);
		threadPools =  new ArrayList<Thread>(minThread);//初始化线程
		for(int i = 0;i<minThread;i++)
			threadPools.add(new Thread(new SchemeExecuter(i+""),i+""));
		busyPools = new ArrayList<Thread>(0);
	}
	static SchemeManager getManager(){
		if(instance==null)
			synchronized (SchemeManager.class) {
				if(instance==null)
					instance=new SchemeManager();
			}
		return instance;
	}
	@Override
	public void run() {
		while(schemePools.size()>0&&available){
			Thread thread = this.getExecuteThreader();
			thread.start();
			System.out.println(thread);
		}//执行完毕后，需要释放线程
		this.available=false;
	}
	//获取线程
	public synchronized Thread getExecuteThreader(){
		if(this.threadPools.size()>0){//有可用线程时
			Thread executer =  this.threadPools.get(0);
			this.busyPools.add(executer);
			this.threadPools.remove(0);
			return executer;
		}
		if(this.busyPools.size()<this.maxThread){
			this.threadPools.add(new Thread(new SchemeExecuter(busyPools.size()+1+""),busyPools.size()+1+""));
		}
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.getExecuteThreader();
	}
	
	//释放线程
	public synchronized void release(String id){
		System.out.println("释放："+id);
		for(Thread thread : busyPools)
			if(thread.getName().equals(id)){
				this.busyPools.remove(thread);
				this.threadPools.add(thread);
				System.out.println(id+":"+thread.isAlive());
				break;
			}
		notify();
	}
	class SchemeExecuter implements Runnable{
		private String id;
		private int currentTask = 0;
		public SchemeExecuter(String id) {
			this.id = id;
		}
		@Override
		public void run() {
			while(currentTask<schemePools.size()&&available){
				Scheme scheme = schemePools.get(currentTask);
				scheme.trigger();
				currentTask++;
			}//执行完毕后，需要释放线程
			release(this.id);
		}
		public String getThreadId(){
			return this.id;
		}
	}
	public void addScheme(Scheme scheme) {
		this.schemePools.add(scheme);
		if(!this.available){
			this.available = true;
			System.out.println(this.damon.getState());
			this.damon.start();
		}
			
	}

}
