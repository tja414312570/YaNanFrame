package com.YaNan.frame.plugin;

import java.io.File;

import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.reflect.ClassLoader;

/**
 * 组件描述类
 * 用于创建组件时的组件信息
 * @author yanan
 *
 */
public class PlugsDescription {
	public PlugsDescription(Service service, Class<?> cls) {
		this.clzz = cls;
		this.service = service;
	}

	public PlugsDescription(File file) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String fileName = file.getName();
		String clzzStr = fileName.substring(0,fileName.lastIndexOf("."));
		this.clzz= new ClassLoader(clzzStr,false).getLoadedClass();
	}

	public PlugsDescription(Class<?> plugClass) {
		this.clzz = plugClass;
	}

	/**
	 * 组件类
	 */
	private Class<?> clzz;
	private PlugsConfigureWrapper plugsConfgureWrapper;
	public Class<?> getPlugClass() {
		return clzz;
	}
	public void setClzz(Class<?> clzz) {
		this.clzz = clzz;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}

	public PlugsConfigureWrapper getPlugsConfgureWrapper() {
		return plugsConfgureWrapper;
	}

	public void setPlugsConfgureWrapper(PlugsConfigureWrapper plugsConfgureWrapper) {
		this.plugsConfgureWrapper = plugsConfgureWrapper;
	}

	/**
	 * Service 注解
	 */
	private Service service;
}
