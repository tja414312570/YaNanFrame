package com.YaNan.frame.IO;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class IOSupport {
	public OutputStream out = System.out;
	public PrintStream pri = System.out;
	public PrintStream err = System.err;
	public InputStream in = System.in;
	public final static IOSupport ioSupport = new IOSupport();

	private IOSupport() {
		this.out = System.out;
		this.pri = System.out;
		this.err = System.err;
		this.in = System.in;
	}

	public static IOSupport getIO() {
		return ioSupport;
	}

	public static void backup() {
		ioSupport.out = System.out;
		ioSupport.pri = System.out;
		ioSupport.err = System.err;
		ioSupport.in = System.in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public void setOutputStream(OutputStream outputStream) {
		out = outputStream;
	}

	public InputStream getInputStream() {
		return in;
	}

	public void setInputStream(InputStream inputStream) {
		in = inputStream;
	}

	public PrintStream getErrorStream() {
		return err;
	}

	public void setErrorStream(PrintStream errorStream) {
		err = errorStream;
	}

	public PrintStream getPrintStream() {
		return pri;
	}

	public void setPrintStream(PrintStream printStream) {
		pri = printStream;
	}

	public static void recovery() {
		System.setOut(ioSupport.pri);
		System.setErr(ioSupport.err);
		System.setIn(ioSupport.in);
	}
}
