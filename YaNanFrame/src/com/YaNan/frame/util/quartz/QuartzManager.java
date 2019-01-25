package com.YaNan.frame.util.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.reflect.cache.ClassHelper;
import com.YaNan.frame.reflect.cache.MethodHelper;
import com.YaNan.frame.reflect.ClassLoader;

@Register
public class QuartzManager implements ServletContextListener {
	private volatile static Scheduler scheduler ;
	public static Scheduler getScheduler() {
		return scheduler;
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}
	private void initScheduler(){
		try {
			StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
			scheduler = stdSchedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException("failed to init quarzt ", e);
		}
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Iterator<RegisterDescription> iterator = PlugsFactory.getInstance().getAllRegister().values().iterator();
		while (iterator.hasNext()) {
			ClassHelper helper = ClassHelper.getClassHelper(iterator.next().getRegisterClass());
			Cron cron;
			if (ClassLoader.implementsOf(helper.getCacheClass(), org.quartz.Job.class)
					&& (cron = helper.getAnnotation(Cron.class)) != null) {
				jobClass(helper.getCacheClass(), cron);
			} else {
				Iterator<MethodHelper> methodHelperIterator = helper.getMethodHelperMap().values().iterator();
				while (methodHelperIterator.hasNext()) {
					MethodHelper methodHelper = methodHelperIterator.next();
					if ((cron = methodHelper.getAnnotation(Cron.class)) != null)
						jobMethod(helper.getCacheClass(), methodHelper.getMethod(), cron);
				}
			}
		
		}
	}

	private void jobMethod(Class<?> cacheClass, Method method, Cron cron) {
		JobBuilder jobBuilder = JobBuilder.newJob(AbstractJobImpl.class);
		JobDataMap jdm = new JobDataMap();
		jdm.put("class", cacheClass);
		jdm.put("method",method);
		jobBuilder.usingJobData(jdm);
		String group = cron.group().equals("") ? cacheClass.getSimpleName() : cron.group();
		String name = cron.name().equals("") ? method.getName() : cron.name();
		schedule(cacheClass, jobBuilder, name, group, cron);
	}

	private void schedule(Class<?> jobClass, JobBuilder jobBuilder, String name, String group, Cron cron) {
		try {
			JobDetail jobDetail = jobBuilder.withIdentity(name, group).withDescription(cron.descripton()).build();
			CronTrigger cronTrigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(cron.value())).build();
			if(scheduler==null){
				synchronized (this) {
					if(scheduler==null){
						initScheduler();
					}
				}
			}
			scheduler.scheduleJob(jobDetail, cronTrigger);
		} catch (Throwable e) {
			throw new RuntimeException("failed to execute quarzt job at class " + jobClass.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void jobClass(Class<?> jobClass, Cron cron) {
		JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) jobClass);
		String group = cron.group().equals("") ? jobClass.getSimpleName() : cron.group();
		String name = cron.name().equals("") ? jobClass.getSimpleName() : cron.name();
		schedule(jobClass, jobBuilder, name, group, cron);
	}

	public static class AbstractJobImpl implements Job {

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap jdm = context.getJobDetail().getJobDataMap();
			Class<?> clz = (Class<?>) jdm.get("class");
			Method met = (Method) jdm.get("method");
			try {
				Object jobInstance = PlugsFactory.getPlugsInstance(clz);
				Class<?>[] types = met.getParameterTypes();
				Object[] params = new Object[types.length];
				for(int i = 0;i<types.length;i++ ){
					if(ClassLoader.isNotNullType(types[i])){
						if(types[i].equals(int.class)||types[i].equals(short.class)||types[i].equals(long.class)){
							params[i] = 0;
						}else if(types[i].equals(boolean.class)){
							params[i] = false;
						}else if(types[i].equals(float.class)){
							params[i] = 0f;
						}else if(types[i].equals(double.class)){
							params[i] = 0d;
						}
					}
				}
				met.setAccessible(true);
				met.invoke(jobInstance,params);
				met.setAccessible(false);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("failed to execute quarzt job at class "+clz+" at method"+met,e);
			}
			
		}
	}

}
