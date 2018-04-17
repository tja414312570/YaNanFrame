package com.YaNan.frame.core.servlet.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
	public void testGetPath() throws Exception{
		Map<Integer,String> pathVariable = new HashMap<Integer,String>();
//		pathVariable.put(2,"id");//表示在第二个/的后面第0个位置开始第三个/的前面/个位置
//		pathVariable.put(3,"fileName");
		Map<String,String> pathValues = new LinkedHashMap<String, String>();
		String reg = "/user/{id}/doc/{fileName}";
		
		int varIndex = reg.indexOf("{");
		while(varIndex>=0){
			int varEndex = reg.indexOf("}",varIndex+1);
			if(varEndex<0)
				throw new Exception("url mapping "+ reg
				+ " error,the Variable descriptors are not equal,please check :[ or use @Action parse to action style]");
			reg =reg .substring(0,varIndex)+"*"+reg .substring(varEndex+1);
			log.debug(reg);
			varIndex = reg.indexOf("{");
		}
		
		
		int Index = reg.indexOf("/");
		int count = 0;
		while(Index>=0){
			count ++;
			int Endex = reg.indexOf("/",Index+1);
			if(Endex<0){
				String regTmp = reg.substring(Index+1);
				if(regTmp.indexOf("{")>=0&&regTmp.indexOf("}")>=0)
					pathVariable.put(count, regTmp.substring(1,regTmp.length()-1));
				break;
			}
			String regTmp = reg.substring(Index+1,Endex);
			if(regTmp.indexOf("{")>=0&&regTmp.indexOf("}")>=0)
				pathVariable.put(count, regTmp.substring(1,regTmp.length()-1));
			Index =Endex;
		}
		System.out.println(pathVariable);
		String url ="/user/my/doc/a.text";
		Index = url.indexOf("/");
		count = 0;
		while(Index>=0){
			count ++;
			String pvi = pathVariable.get(count);
			int Endex = url.indexOf("/",Index+1);
			if(Endex<0){
				if(pvi!=null)
					pathValues.put(pvi,url.substring(Index+1));
				break;
			}
			if(pvi!=null)
				pathValues.put(pvi,url.substring(Index+1,Endex));
			Index =Endex;
		}
		System.out.println(pathValues);
		
	}
	
	public void main() {
		ServletBuilder.getInstance().initByScanner();
		Map<String, ServletBean> servletMapping = DefaultServletMapping.getInstance()
				.getServletMapping();
		Iterator<String> iterator = servletMapping.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			ServletBean bean = servletMapping.get(key);
			log.debug("---------------------------------------------------------");
			log.debug("url mapping:" + bean.getPathRegex() + ",servlet method:" + bean.getMethod()
					+ ",servlet type:" +bean.getType()+",request parameter:"+bean.getPathVariable());
			log.debug("---------------------------------------------------------");
		}
		ServletBean bean = DefaultServletMapping.getInstance().getServlet("/getServletNum.do");
		log.debug(bean.getMethod().toGenericString());
		long t = System.currentTimeMillis();
		for(int i =0;i<100000000;i++){
			DefaultServletMapping.getInstance().getServlet("/getServletNum.do");
		}
		System.out.println(System.currentTimeMillis()-t);
		System.out.println("class loader QPS:"+100000000/(System.currentTimeMillis()-t));
		System.out.println(DefaultServletMapping.getInstance().getServlet("/user/lll").getStyle());
	}
}
