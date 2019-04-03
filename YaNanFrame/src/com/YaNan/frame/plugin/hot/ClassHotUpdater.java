package com.YaNan.frame.plugin.hot;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.path.FileUtils;
import com.YaNan.frame.path.Path;
import com.YaNan.frame.path.Path.PathInter;
import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.reflect.ClassLoader;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.commons.Remapper;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
/**
 * 类更新监控
 * @author yanan
 *
 */
public class ClassHotUpdater implements Runnable, ServletContextListener {
	public Log log = PlugsFactory.getPlugsInstance(Log.class, ClassHotUpdater.class);
	private boolean cache;// 是否已经扫描
	private List<Object> scanner = new ArrayList<Object>(100);//用于存储已经扫描到的文件
	static Map<Object, Long> map = new HashMap<Object, Long>(100);//用于存储所有扫描到的类
	static Map<Object,Object> proxy = new HashMap<Object,Object>();//用于存储映射关系

	/**
	 * 用于获取代理类
	 * @param origin
	 * @return
	 */
	public static Class<?> getProxyClass(Class<?> origin){
		return (Class<?>) proxy.get(origin);
	}
	/**
	 * 文件hash
	 * @param file
	 * @return
	 */
	public static long hash(File file) {
		return file.length() + file.hashCode() + file.lastModified();
	}

	@Override
	public void run() {
		while (true) {
			Path path = new Path(ResourceManager.classPath());
			path.filter("**.class");
			path.scanner(new PathInter() {
				@Override
				public void find(File file) {
					long h = hash(file);
					if (!cache) {// 如果是第一次扫描
						map.put(file, h);
					} else {// 其它扫描
						scanner.add(file);
						String clzzName = ResourceManager.getClassPath(file.getAbsolutePath());
						clzzName = clzzName.substring(0, clzzName.length() - 6);
						try {
							Class<?> clzz = Class.forName(clzzName);
							java.lang.ClassLoader loader = clzz == null ? this.getClass().getClassLoader()
									: clzz.getClassLoader();
							byte[] content = FileUtils.getBytes(file);
							if (map.containsKey(file)) {// 如果文件已经存在
								long ha = map.get(file);
								if (ha != h) {
									ClassReader reader = new ClassReader(content, 0, content.length);
									ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
									String className = clzzName + "$Y" + ha;
									Map<String, String> mappings = new HashMap<String, String>();
									mappings.put(clzzName.replace(".", "/"), className.replace(".", "/"));
									Remapper mapper = new SimpleRemapper(mappings);
									ClassVisitor remapper = new ClassRemapper(cw, mapper);
									reader.accept(remapper, ClassReader.SKIP_DEBUG);// new
																					// ClassAdapter(Opcodes.ASM4,cw,className.replace(".",
																					// "/")),
																					// ClassReader.SKIP_DEBUG);
									byte[] bytes = cw.toByteArray();
									Class<?> nc = loadClass(loader, className, bytes);
									// loader.loadClass(clzzName,bytes);
									RegisterDescription registerDescription = PlugsFactory.getRegisterDescrption(clzz);
									if (registerDescription != null) {
										if (registerDescription.getLinkRegister() != null)
											clzz = registerDescription.getLinkRegister().getRegisterClass();
										registerDescription.updateRegister(nc);
									} else
										tryAddPlugs(nc);
									proxy.put(clzz, nc);
									notifyListener(clzz, nc, new ClassLoader().loadClass(clzzName, content), file);//通知修改
									log.debug(clzzName + "  update success!");
									map.put(file, h);
								}
							} else {// 如果文件不存在
									// 加载类
								Class<?> nc = Class.forName(clzzName);
								if(nc == null)
									nc = loadClass(loader, clzzName, content);
								tryAddPlugs(nc);
								notifyListener(null, nc , null , file);//通知添加
								map.put(file, h);
								log.debug(clzzName + "  add success!");
								scanner.add(file);
							}
						} catch (Exception e) {
							log.error("failed to update class \"" + clzzName + "\"", e);
						}
					}
				}
			});
			if(cache){
				Iterator<Object> iterator = map.keySet().iterator();
				while(iterator.hasNext()){
					File file = (File) iterator.next();
					if(!scanner.contains(file)){
						String clzzName = ResourceManager.getClassPath(file.getAbsolutePath());
						clzzName = clzzName.substring(0, clzzName.length() - 6);
						Class<?> clzz;
						try {
							iterator.remove();
							clzz = Class.forName(clzzName);
							Class<?> cp = (Class<?>) proxy.get(clzz);
							if(cp!=null)
								clzz = cp;
							log.debug(clzzName + "  remvoe success!");
							proxy.remove(clzz);
							notifyListener(clzz, null , null , file);//通知删除
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				scanner.clear();
			}
			try {
				cache = true;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 尝试添加组件
	 * @param nc
	 */
	protected void tryAddPlugs(Class<?> nc) {
		if(nc.getAnnotationsByType(Service.class)!=null||nc.getAnnotation(Register.class)!=null)
			PlugsFactory.getInstance().addPlugs(nc);
	}
	/**
	 * 通知类状态修改监听类
	 * @param clzz
	 * @param nc
	 * @param oc
	 * @param file
	 */
	protected void notifyListener(Class<?> clzz, Class<?> nc,Class<?> oc, File file) {
		List<ClassUpdateListener> updaterList = PlugsFactory
				.getPlugsInstanceList(ClassUpdateListener.class);
		for (ClassUpdateListener listener : updaterList)
			listener.updateClass(clzz, nc,oc,
					file);
	}

	/**
	 * 加载类
	 * @param loader
	 * @param className
	 * @param bytes
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	protected Class<?> loadClass(java.lang.ClassLoader loader,String className,byte[] bytes) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = java.lang.ClassLoader.class.getDeclaredMethod("defineClass",
				String.class, byte[].class, int.class, int.class);
		method.setAccessible(true);
		Class<?> nc = (Class<?>) method.invoke(loader, className, bytes, 0, bytes.length);
		method.setAccessible(false);
		return nc;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.debug("enable class hot update deployment service!");
		ClassHotUpdater test = PlugsFactory.getPlugsInstance(ClassHotUpdater.class);
		Thread thread = new Thread(test);
		thread.setName("Plugin class hot update monitor server");
		thread.setDaemon(true);
		thread.start();
	}
}
