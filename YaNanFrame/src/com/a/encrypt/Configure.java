package com.a.encrypt;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.YaNan.frame.plugin.ConfigContext;
import com.YaNan.frame.plugin.PlugsFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
public class Configure {
	public static void main(String[] args) {
		PlugsFactory.getInstance();
		Config config = ConfigFactory.load("plugin.conf");
		Config config2 = ConfigFactory.load("plugin2.conf");
		Config conf = ConfigContext.getConfig("SecurityFilter");
		conf.disableTypeVerify();
		System.out.println(conf.getConfig("xss-wrapper").simpleObjectEntrySet());
		System.out.println(ConfigContext.getBoolean("security3"));
		System.out.println(config);
		System.out.println(config2);
		Config config3 = config.resolveWith(config2);
		Config config4 = config.resolve();
		System.out.println(config3);
		System.out.println(config4);
		Config list = config.getConfig("conf");
		System.out.println(list.configEntrySet());
		System.out.println(list.simpleObjectEntrySet());
		
	}
}
