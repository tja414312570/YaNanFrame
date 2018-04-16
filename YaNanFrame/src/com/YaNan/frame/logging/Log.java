package com.YaNan.frame.logging;

import com.YaNan.frame.plugs.annotations.Service;

@Service
public interface Log {

	  boolean isDebugEnabled();

	  boolean isTraceEnabled();

	  void error(String s, Throwable e);

	  void error(String s);
	  
	  void error(Throwable e);

	  void debug(String s);

	  void trace(String s);

	  void warn(String s);

	}
