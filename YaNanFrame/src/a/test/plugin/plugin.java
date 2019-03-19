package a.test.plugin;

import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.util.StringUtil;

public class plugin {
	@Service(id="sacotory2")
	private Factory factory2;
	@Service(id="sacotory4")
	private Factory factory;
	public static void main(String[] args) {
//		System.out.println(ResourceManager.getPathExress("classpath:hibernxate"));
//		System.out.println(StringUtil.matchURI("/logind", "/login"));
//		PluginAppincationContext.setWebContext(true);
		Factory factory = PlugsFactory.getBean("sacotory5");
		System.out.println(factory);
		factory.Out("hello");
		plugin p = PlugsFactory.getPlugsInstance(plugin.class);
		System.out.println(p.factory2);
		System.out.println(p.factory);
//		Factory factory1 = PlugsFactory.getBean("testFacotory2");
//		System.out.println(factory1);
//		factory1.Out("hello2");
//		Factory factory2 = PlugsFactory.getBean("testFacotory3");
//		System.out.println(factory2);
//		factory2.Out("hello3");
	}
}
