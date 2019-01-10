package a.test.plugin;

import com.YaNan.frame.plugin.PluginAppincationContext;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.util.StringUtil;

public class plugin {
	public static void main(String[] args) {
		System.out.println(StringUtil.matchURI("/logind", "/login"));
		PluginAppincationContext.setWebContext(true);
		Factory factory = PlugsFactory.getBean("testFacotory");
		System.out.println(factory);
		factory.Out("hello");
		Factory factory1 = PlugsFactory.getBean("testFacotory2");
		System.out.println(factory1);
		factory1.Out("hello2");
		Factory factory2 = PlugsFactory.getBean("testFacotory3");
		System.out.println(factory2);
		factory2.Out("hello3");
	}
}
