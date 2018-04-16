package com.YaNan.frame.core.servlet.debug;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.YaNan.frame.core.servlet.DefaultServletMapping;
import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.ServletBuilder;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;

public class TestInit {
	Log log = PlugsFactory.getPlugsInstance(Log.class,TestInit.class);
	@Test
	public  void main() {
		ServletBuilder.getInstance().initByScanner();
		Map<String, ServletBean> servletMapping = DefaultServletMapping.getInstance()
				.getServletMapping();
		Iterator<String> iterator = servletMapping.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			ServletBean bean = servletMapping.get(key);
			log.debug("---------------------------------------------------------");
			log.debug("url mapping:" + bean.getPathRegex() + ",servlet method:" + bean.getMethod()
					+ ",servlet type:" +bean.getType());
			log.debug("---------------------------------------------------------");
		}
		ServletBean bean = DefaultServletMapping.getInstance().getServlet("/getServletNum.do");
		log.debug(bean.getMethod().toGenericString());
		long t = System.currentTimeMillis();
		for(int i =0;i<1000000;i++){
		//	DefaultServletMapping.getInstance().getServlet("/user/ddd");
		}
		System.out.println(System.currentTimeMillis()-t);
	}
}
