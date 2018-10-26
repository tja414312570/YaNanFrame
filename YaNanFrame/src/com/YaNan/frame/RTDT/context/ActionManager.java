package com.YaNan.frame.RTDT.context;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.RTDT.actionSupport.RTDTActionInterface;
import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;
import com.YaNan.frame.RTDT.entity.ActionEntity;
import com.YaNan.frame.RTDT.entity.NotifyEntity;
import com.YaNan.frame.RTDT.entity.RTDTAction;
import com.YaNan.frame.RTDT.entity.annotations.RAction;
import com.YaNan.frame.RTDT.entity.annotations.RNotify;
import com.YaNan.frame.path.PackageScanner;
import com.YaNan.frame.path.PackageScanner.ClassInter;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.util.beans.BeanFactory;
import com.YaNan.frame.util.beans.XMLBean;

/**
 * RTDT框架 通过websocket进行服务器与客户端数据交互
 * 版本：
 * v0.1支持数据交互 js支持Req请求
 * v0.2支持广播机制，可以对广播进行分类，通过虚拟地址实现不同广播
 * @author Administrator
 *
 */
public class ActionManager {
	private static ActionManager manager;
	private NotifyManager notifyManager;
	private File rtdtFile;
	private Map<String,ActionEntity> actionPools = new HashMap<String,ActionEntity>();
	private File classPath;
	private boolean available=false;
	private ActionManager(){
		this.notifyManager = NotifyManager.getManager();
	}
	public static ActionManager getActionManager(){
		if(manager==null)
			manager = new ActionManager();
		return manager;
	}
	public void init(File rtdtFile){
		this.rtdtFile = rtdtFile;
		this.init();
	}
	/**
	 * 无参数的初始化方法
	 */
	public void init(){
		
		this.classPath = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "));
		PackageScanner scanner = new PackageScanner();
		scanner.setClassPath(classPath.getAbsolutePath());
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				RNotify notify = cls.getAnnotation(RNotify.class);
				if(notify!=null)
					if(ClassLoader.implementOf(cls,RTDTNotification.class)){
						NotifyEntity entity = new NotifyEntity();
						entity.setCLASS(cls);
						entity.setMark(entity.getMark());
						entity.setName(notify.value().equals("")?cls.getSimpleName():notify.value());
						entity.setValue(notify.token());
						notifyManager.addEntity(entity);
					}else{
						try {
							throw new Exception("【RTDT Notify】name ["+notify.value()+"] isn't implements inteface RTDTNotification at class:"+cls.getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				Method[] methods = cls.getMethods();
				for(Method method :methods){
					RAction raction = method.getAnnotation(RAction.class);
					if(raction!=null){
						ActionEntity entity = new ActionEntity();
						entity.setCLASS(cls);
						entity.setMethod(method);
						entity.setName(raction.value().equals("")?method.getName():raction.value());
						actionPools.put(entity.getName(),entity);
					}
				}
			}
		});
		this.available = true;
		if(this.rtdtFile==null)
			this.rtdtFile = new File(new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," ")),"RDT.xml");
		try {
			if(!this.rtdtFile.exists())return;
			//action 最初配置
			XMLBean xmlBean = BeanFactory.getXMLBean();
			xmlBean.addXMLFile(this.rtdtFile);
			xmlBean.addElementPath("//rtdt-mapping");
			xmlBean.setNodeName("action");
			xmlBean.setBeanClass(RTDTAction.class);
			xmlBean.addNameMaping("class","CLASS");
			List<?> alists =xmlBean.execute();
			Iterator<?> iterator = alists.iterator();
			while(iterator.hasNext()){
				RTDTAction action = (RTDTAction) iterator.next();
				if(action.getType()!=null&&action.getType().equals("Notify")){
					NotifyEntity entity = new NotifyEntity();
					entity.setName(action.getName());
					entity.setMark(action.getMark().hashCode());
					Class<?> cls;
					try {
						cls = Class.forName(action.getCLASS());
					} catch (ClassNotFoundException e) {
						cls= RTDTActionInterface.class;
					}
					entity.setCLASS(cls);
					notifyManager.addEntity(entity);
				}else{
					try {
						ActionEntity entity = new ActionEntity();
						Class<?> cls = Class.forName(action.getCLASS());
						Method method = cls.getDeclaredMethod(action.getMethod());
						entity.setName(action.getName());
						entity.setCLASS(cls);
						entity.setMethod(method);
						this.actionPools.put(entity.getName(),entity);	
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public Map<String, ActionEntity> getActionPools(){
		return this.actionPools;
	}
	public boolean isAvailable(){
		return available;
	}
	public boolean hasAction(String name) {
		return this.actionPools.containsKey(name);
	}
	public ActionEntity getAction(String name){
		return this.actionPools.get(name);
	}
}
