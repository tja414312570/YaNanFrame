package com.YaNan.frame.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import com.YaNan.frame.Native.Files;
import com.YaNan.frame.hibernate.WebPath;

/**
 * @version 1.0.1
 * @author Administrator
 * @since jdk1.7
 */
public class Log {
	final public static int version = 101;
	private LogBuffer logBuffer=new LogBuffer();
	private static Log systemLog;
	private String logPath;
	private String logHeader = "";
	private File fileName;
	private Files file = new Files();

	public Log(String logPath, String logHeader) {
		this.logPath = logPath;
		this.logHeader = logHeader;
	}

	public Log(String logPath) {
		this.logPath = logPath;
	}

	private Log() {

	}

	public void write(String log) {
		if (log != null) {
			System.out.println(date.currentDate() + ":" + log + "\r\n");
			file.file = this.fileName;
			try {
				this.write();
				file.write(date.currentDate() + ":" + log + "\r\n");
				this.logBuffer=null;
			} catch (Exception e) {
				System.out
						.println("log write Failed,this message only show in the console");
				logBuffer.append(log);
			}
		}
	}

	public void error(String log) {
		if (log != null) {
			System.out
					.println(date.currentDate() + ": ERROR:: " + log + "\r\n");
			file.file = this.fileName;
			try {
				this.write();
				file.write(date.currentDate() + ": ERROR:: " + log + "\r\n");
				this.logBuffer=null;
			} catch (Exception e) {
				System.out
						.println("log write Failed,this message only show in the console");
				logBuffer.append(log);
			}
		}
	}

	public void exception(Exception exc) {
		exc.printStackTrace();
		if (exc != null) {
			String str = exc.toString() + "\r\n";
			for (StackTraceElement s : exc.getStackTrace())
				str += "		at " + s + "\r\n";
			System.out
					.println(date.currentDate() + ": ERROR:: " + str + "\r\n");
			file.file = this.fileName;
			try {
				this.write();
				file.write(date.currentDate() + ": ERROR:: " + str + "\r\n");
				this.logBuffer=null;
			} catch (Exception e) {
				System.out
						.println("log write Failed,this message only show in the console");
				logBuffer.append(str);
			}
		}
	}

	public void warn(String log) {
		if (log != null) {
			System.out.println(date.currentDate() + ": WARN:: " + log + "\r\n");
			file.file = this.fileName;
			try {
				this.write();
				file.write(date.currentDate() + ": WARN:: " + log + "\r\n");
				this.logBuffer=null;
			} catch (Exception e) {
				System.out
						.println("log write Failed,this message only show in the console");
				logBuffer.append(log);
			}
		}
	}

	public void info(String log) {
		if (log != null) {
			System.out.println(date.currentDate() + ": INFO:: " + log + "\r\n");
			file.file = this.fileName;
			try {
				this.write();
				file.write(date.currentDate() + ": INFO :: " + log + "\r\n");
				this.logBuffer=null;
			} catch (Exception e) {
				System.out
						.println("log write Failed,this message only show in the console");
				logBuffer.append(log);
			}
		}
	}

	public PrintStream getPrintStream() {
		PrintStream ps = System.out;
		try {
			ps = new PrintStream(this.file.getOutputStream());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ps;
	}

	public LogBuffer getBuffer() {
		this.logBuffer = (this.logBuffer == null ? new LogBuffer()
				: this.logBuffer);
		return this.logBuffer;
	}

	public void delBuffer() {
		if (this.logBuffer != null)
			this.logBuffer.close();
	}

	public void write() {
		if(logBuffer==null||WebPath.getWebPath().getLogPath()==null)return;
		if (this.logPath == null || this.logPath.equals(""))
			this.logPath = WebPath.getWebPath().getLogPath().realPath;
		this.fileName = new File(this.logPath + this.logHeader
				+ date.currentDate().replaceAll("(\\s|\\t\\r|:)", "-") + ".txt");
		try {
			this.fileName.createNewFile();
		} catch (IOException e) {
			System.out
					.println("log init error,this message only show in the console");

		}
		file.file = this.fileName;
		try {
			file.write(this.logBuffer.toString());
		} catch (Exception e) {
			System.out
					.println("log write Failed,this message only show in the console");
		}
	}

	public static Log getSystemLog() {
		systemLog = (systemLog == null ? new Log() : systemLog);
		return systemLog;
	}

	public static class LogBuffer {
		private static final StringBuffer stringBuffer = new StringBuffer();
		private static Log log;
		private static LogBuffer buffer;
		public static String str = null;

		public void append(String str) {
			stringBuffer.append(date.currentDate() + ": " + str + "\r\n");
			System.out.println(date.currentDate() + ": " + str + "\r\n");
		}

		public void warn(String str) {
			stringBuffer
					.append(date.currentDate() + ": WARN:: " + str + "\r\n");
			System.out.println(date.currentDate() + ": WARN:: " + str + "\r\n");
		}

		public void error(String str) {
			stringBuffer.append(date.currentDate() + ": ERROR:: " + str
					+ "\r\n");
			System.out
					.println(date.currentDate() + ": ERROR:: " + str + "\r\n");
		}

		public void info(String str) {
			stringBuffer
					.append(date.currentDate() + ": INFO:: " + str + "\r\n");
			System.out.println(date.currentDate() + ": INFO:: " + str + "\r\n");
		}

		public void exception(Exception exc) {
			String str = exc.toString() + "\r\n";
			for (StackTraceElement s : exc.getStackTrace())
				str += "		at " + s + "\r\n";
			stringBuffer
					.append((date.currentDate() + ": ERROR:: " + str + "\r\n"));
			System.out
					.println(date.currentDate() + ": ERROR:: " + str + "\r\n");
		}

		public void close() {
			stringBuffer.delete(0, stringBuffer.length());
		}

		@Override
		public String toString() {
			return stringBuffer.toString();
		}

		public static LogBuffer getLogBuffer() {
			if (log == null || buffer == null) {
				log = Log.getSystemLog();
				buffer = log.getBuffer();
			}
			return buffer;
		}
	}

	public String getLogPath() {
		return this.fileName.toString();
	}
}

class date {
	@SuppressWarnings("deprecation")
	public static String currentDate() {
		return new Date().toLocaleString();
	}
}