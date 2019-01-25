package com.YaNan.frame.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.annotations.Register;

/**
 * 默认日志工具，未完善
 * @author yanan
 *
 */
@Register(priority=-1)
public class DefaultSl4jImpl implements Log{
	Logger logger;
	public DefaultSl4jImpl(){
		logger = LoggerFactory.getLogger("");
	}
	public DefaultSl4jImpl(Class<?> clzz){
		logger = LoggerFactory.getLogger(clzz.getName());
	}
	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public void error(String s, Throwable e) {
		logger.error(s, e);
	}

	@Override
	public void debug(String s) {
		logger.debug(s);
	}

	@Override
	public void trace(String s) {
		logger.trace(s);
	}

	@Override
	public void error(String s) {
		logger.error(s);
	}

	@Override
	public void warn(String s) {
		logger.warn(s);
	}
	@Override
	public void error(Throwable e) {
		logger.error(e.getMessage(),e);
	}
}
