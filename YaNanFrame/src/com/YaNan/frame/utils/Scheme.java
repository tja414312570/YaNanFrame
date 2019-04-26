package com.YaNan.frame.utils;

public class Scheme {
	private static Scheme instance;
	private String identify;
	private Job job;
	private int times=-1;
	private int execTimes = 0;
	private Class<? extends Job> JobClass;
	public Scheme(Class<? extends Job> jobClass) {
		this.JobClass = jobClass;
		try {
			this.job = jobClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
//	public static Scheme getScheme(){
//		if(instance==null)
//			synchronized (instance) {
//				instance=new Scheme();
//				return instance;
//			}
//		return instance;
//	}
	public void startNow() {
		this.trigger();
	}
	public void trigger(){
		if(times>-1)
			while(execTimes<times){
				this.job.excute();
				this.execTimes++;
			}
	}
	public void shutdown() {
		
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public void enableScheme() {
		SchemeManager.getManager().addScheme(this);
	}
}
