package com.YaNan.frame.plugin.autowired.resource;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.RegisterDescription.FieldDesc;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.FieldHandler;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.util.beans.xml.Resource;

@Support(Resource.class)
@Register
public class ResourceWiredHandler implements FieldHandler{
	final static String CLASSPATH = "classpath:";
	@Override
	public void preparedField(RegisterDescription registerDescription, Object proxy, Object target, FieldDesc desc,
			Object[] args) {
		String path = desc.getValue();
		if(path==null&&desc.getAnnotation()!=null)
			path = ((Resource)desc.getAnnotation()).value();
		if(path==null)
			throw new RuntimeException("Resource value is null !\r\nat class : "+target.getClass().getName()
					+"\r\nat field : "+desc.getField().getName());
		int cpIndex = path.indexOf(CLASSPATH);
		File file =null;
		try {
			if(cpIndex==-1){
				file= new File(path);
			}else{
				file = new File(this.getClass().getClassLoader().getResource("").getPath().replace("%20"," "),path.substring(cpIndex+CLASSPATH.length()));
			}
			new ClassLoader(target).set(desc.getField(), file);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Resource wired failed !\r\nat class : "+target.getClass().getName()
					+"\r\nat field : "+desc.getField().getName()
					+"\r\nat file : "+file);
		}
	}
	
}
