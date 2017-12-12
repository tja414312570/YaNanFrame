package com.YaNan.frame.service;

import com.YaNan.frame.service.Log.LogBuffer;

public interface LogInterface {
	public void write(String log);

	public void error(String log);

	public void warn(String log);

	public void info(String log);

	public LogBuffer getBuffer();

	public void setLogBuffer(LogBuffer logBuffer);

	public void delBuffer();

	public void write();
}