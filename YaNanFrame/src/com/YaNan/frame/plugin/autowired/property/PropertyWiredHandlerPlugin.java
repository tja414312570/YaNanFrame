package com.YaNan.frame.plugin.autowired.property;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.interfacer.PlugsListener;

@Register(priority=Integer.MIN_VALUE)
public class PropertyWiredHandlerPlugin implements PlugsListener{

	@Override
	public void excute(PlugsFactory plugsFactory) {
		PropertyManager pm = PropertyManager.getInstance();
		pm.scanAllProperty();
	}

}
