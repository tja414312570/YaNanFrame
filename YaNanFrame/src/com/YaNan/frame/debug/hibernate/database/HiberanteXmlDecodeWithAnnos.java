package com.YaNan.frame.debug.hibernate.database;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.YaNan.frame.hibernate.WebPath;
import com.YaNan.frame.hibernate.beanSupport.BeanFactory;
import com.YaNan.frame.hibernate.beanSupport.XMLBean;
import com.YaNan.frame.hibernate.database.entity.Package;
import com.YaNan.frame.path.PackageScanner;
import com.YaNan.frame.path.PackageScanner.ClassInter;

public class HiberanteXmlDecodeWithAnnos {
	
	public static void main(String[] args) {
		File file = new File("src/Hibernate.xml");
		XMLBean xmlBean = BeanFactory.getXMLBean();
		xmlBean.addXMLFile(file);
		xmlBean.addElementPath("//Hibernate");
		xmlBean.setNodeName("package");
		xmlBean.setBeanClass(Package.class);
		xmlBean.addNameMaping("package","PACKAGE");
		List<Object> lists = xmlBean.execute();
		Iterator<Object> iterator = lists.iterator();
		while(iterator.hasNext()){
			Package pkg = (Package) iterator.next();
			System.out.println(pkg.toString());
			initTables(pkg);
		}
	}
	private static void initTables(Package pkg){
		String packageName = pkg.getPACKAGE();
		if(packageName!=null&&!packageName.equals("")){
			String classPath = WebPath.getWebPath().getClassPath().getRealPath();
			PackageScanner scanner = new PackageScanner();
			scanner.setClassPath(classPath);
			scanner.setPackageName(packageName);
			scanner.doScanner(new ClassInter(){
				@Override
				public void find(Class<?> cls) {
					System.out.println("scann class:"+cls);
				}
			});
		}
	}
}
