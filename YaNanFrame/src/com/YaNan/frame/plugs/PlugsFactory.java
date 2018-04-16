package com.YaNan.frame.plugs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.Native.PackageScanner;
import com.YaNan.frame.Native.Path;
import com.YaNan.frame.Native.PackageScanner.ClassInter;
import com.YaNan.frame.Native.Path.PathInter;
import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.plugs.annotations.Register;
import com.YaNan.frame.plugs.annotations.Service;
import com.YaNan.frame.plugs.proxy.PlugsProxy;
import com.mysql.jdbc.log.Log;


public class PlugsFactory {
	private static PlugsFactory instance;
	private boolean available=false;
	/**
	 * 组件的容器
	 */
	private Map<Class<?>,Plug> plugsList = new HashMap<Class<?>,Plug>();
	
	//register >> registerInfo
	private  Map<Class<?>,RegisterDescription> RegisterContatiner = new HashMap<Class<?>,RegisterDescription>();
	//registerClass >> registerInstance
	// Register >> Service array
	static{
		if(instance==null)
			instance = new PlugsFactory();
		instance.init();
	}
	public static PlugsFactory getInstance(){
		return instance;
	}
	public void init(){
		PackageScanner scanner = new PackageScanner();
		scanner.doScanner(new ClassInter(){
			@Override
			public void find(Class<?> cls) {
				addPlugs(cls);
			}
		});
		Path path = new Path(this.getClass().getClassLoader().getResource("").getPath().replace("%20", " "));
		path.filter(".plugs",".comps");
		path.scanner(new PathInter() {
			@Override
			public void find(File file) {
				addPlugs(file);
			}
		});
		this.associate();
		available=true;
	}
	/**
	 * 建立个组件和注册器之间的关联
	 */
	public void associate(){
		Iterator<RegisterDescription> iterator = RegisterContatiner.values().iterator();
		while(iterator.hasNext()){
			RegisterDescription registerDescription = iterator.next();
			Class<?>[] plugs = registerDescription.getPlugs();
			Class<?> registerClass = registerDescription.getRegisterClass();
				for(Class<?> plugInterface : plugs){
					Plug plug = this.plugsList.get(plugInterface);
					if(plug==null)
						try {
							throw new Exception("register "+registerClass.getName()+" implements "+plugInterface.getName()+" not exists ");
						} catch (Exception e) {
							e.printStackTrace();
						}
					plug.addRegister(registerDescription);
				}
		}
	}
	public Plug getPlug(Class<?> plugClass){
		return this.plugsList.get(plugClass);
	}
	public void addPlugs(File file){
		String fileName = file.getName();
		String type = fileName.substring(fileName.lastIndexOf("."));
		try {
			if(type.equals(".plugs")){
				PlugsDescription plugsDescription = new PlugsDescription(file);
				Plug plug = new Plug(plugsDescription);
				this.plugsList.put(plugsDescription.getPlugClass(), plug);
			}
			if(type.equals(".comps")){
				RegisterDescription registerDescription = new RegisterDescription(file);
				RegisterContatiner.put(registerDescription.getRegisterClass(),registerDescription);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addPlugs(Class<?> cls){
		Service service = cls.getAnnotation(Service.class);
		Register register = cls.getAnnotation(Register.class);
		if(service!=null){//如果是Service
			PlugsDescription plugsDescrption = new PlugsDescription(service,cls);
			Plug plug = new Plug(plugsDescrption);
			this.plugsList.put(cls, plug);
		}
		if(register!=null){
			try {
				RegisterDescription registerDescription = new RegisterDescription(register,cls);
				RegisterContatiner.put(cls,registerDescription);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void addPlugsByDefault(Class<?> plugClass) {
		PlugsDescription plugsDescrption =  new PlugsDescription(plugClass);
		Plug plug = new Plug(plugsDescrption);
		this.plugsList.put(plugClass, plug);
	}
	
	public static <T> T getPlugsInstanceWithDefault(Class<T> impl,Class<? extends T> defaultClass,Object...args){
			try {
				if(instance==null)
					throw new Exception("YaNan.plugs service not initd");
				if(!instance.isAvailable()){
					Object object = defaultClass.newInstance();
					return (T) PlugsProxy.newMapperProxy(impl,object); 
				}
				Plug plug = instance.getPlug(impl);
				if(plug==null)
					throw new Exception("service interface "+impl.getName() +" could not found or not be regist");
				RegisterDescription registerDescription = plug.getDefaultRegisterDescription();
				if(registerDescription==null)
					throw new Exception("service interface "+impl.getName() +" could not found any registrar");
				Object object = registerDescription.getRegisterInstance(args);//instance.getRegisterInstance(impl,registerDescription,args);
				return (T) PlugsProxy.newMapperProxy(impl,object); 
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}
	
	
	public static <T> T getPlugsInstance(Class<T> impl,Object...args) {
		try {
			if(instance==null)
				throw new Exception("YaNan.plugs service not initd");
			if(!instance.isAvailable())
				throw new Exception("this error may arise because a static field uses the PlugsFactory's proxy");
			Plug plug = instance.getPlug(impl);
			if(plug==null)
				throw new Exception("service interface "+impl.getName() +" could not found or not be regist");
			RegisterDescription registerDescription = plug.getDefaultRegisterDescription();
			if(registerDescription==null)
				throw new Exception("service interface "+impl.getName() +" could not found any registrar");
			Object object = registerDescription.getRegisterInstance(args);//instance.getRegisterInstance(impl,registerDescription,args);
			return (T) PlugsProxy.newMapperProxy(impl,object); 
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T> T getPlugsInstanceByAttribute(Class<T> impl,String attribute,Object...args) {
		try {
			if(instance==null)
				throw new Exception("YaNan.plugs service not initd");
			if(!instance.isAvailable())
				throw new Exception("this error may arise because a static field uses the PlugsFactory's proxy");
			Plug plug = instance.getPlug(impl);
			if(plug==null)
				throw new Exception("service interface "+impl.getName() +" could not found or not be regist");
			RegisterDescription registerDescription = plug.getRegisterDescriptionByAttribute(attribute);
			if(registerDescription==null)
				throw new Exception("service interface "+impl.getName() +" could not found any registrar");
			Object object = registerDescription.getRegisterInstance(args);//instance.getRegisterInstance(impl,registerDescription,args);
			return (T) PlugsProxy.newMapperProxy(impl,object); 
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T> List<T> getPlugsInstanceList(Class<T> impl,Object...args) {
		try {
			if(instance==null)
				throw new Exception("YaNan.plugs service not initd");
			if(!instance.isAvailable())
				throw new Exception("this error may arise because a static field uses the PlugsFactory's proxy");
			Plug plug = instance.getPlug(impl);
			if(plug==null)
				throw new Exception("service interface "+impl.getName() +" could not found or not be regist");
			List<T> objectList = new ArrayList<T>();
			List<RegisterDescription> registerDescriptionList = plug.getRegisterDescriptionList();
			if(registerDescriptionList.size()==0)
				throw new Exception("service interface "+impl.getName() +" could not found any registrar");
			Iterator<RegisterDescription> iterator = registerDescriptionList.iterator();
			while(iterator.hasNext()){
				Object object = iterator.next().getRegisterInstance(args);
				objectList.add(PlugsProxy.newMapperProxy(impl,object));
			}
			return objectList;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public RegisterDescription getRegisterDescription(Class<?> targetCls) {
		return this.RegisterContatiner.get(targetCls);
	}
	public void clear(){
		this.RegisterContatiner.clear();
		this.plugsList.clear();
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	
	
}
