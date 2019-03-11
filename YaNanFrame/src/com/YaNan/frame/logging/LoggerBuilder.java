package com.YaNan.frame.logging;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.autowired.property.Property;

/**
 * log构建服务
 * @author yanan
 *
 */
@Register
public class LoggerBuilder {
	@Property(value="",defaultValue="/logs/${appname}/log-yyyy-MM-dd-HH-mm-ss")
	private String location;
}
