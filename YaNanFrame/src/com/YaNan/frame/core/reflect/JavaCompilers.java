package com.YaNan.frame.core.reflect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.YaNan.frame.Native.Path;
import com.YaNan.frame.Native.Path.PathInter;

interface CompilerInterface {
	public static final String TOOL_G = "-g";
	public static final String TOOL_G_NONE = "-g";
	public static final String TOOL_G_LINES = "-g:{lines,vars,source}";
	public static final String TOOL_NORWARN = "-norwarn";
	public static final String TOOL_VERBOSE = "-verbose";
	public static final String TOOL_DEPRECATION = "-deprecation";
	public static final String TOOL_CLASSPATH = "-classpath";
	public static final String TOOL_CP = "-cp";
	public static final String TOOL_SOURCEPATH = "-sourcepath";
	public static final String TOOL_BOOTCLASSPATH = "-bootclasspath";
	public static final String TOOL_EXTDIRS = "-extdirs";
	public static final String TOOL_ENDORSEDDIRS = "-endorseddirs";
	public static final String TOOL_PROC = "-proc";
	public static final String TOOL_PROCESSOR = "-processor";
	public static final String TOOL_PROCESSORPATH = "-processorpath";
	public static final String TOOL_PARAMETERS = "-parameters";
	public static final String TOOL_D = "-d";
	public static final String TOOL_S = "-s";
	public static final String TOOL_H = "-h";
	public static final String TOOL_IMPLICIT = " -implicit:";
	public static final String TOOL_ENCODING = "-encoding ";
	public static final String TOOL_SOURCE = " -source";
	public static final String TOOL_TARGET = "-target";
	public static final String TOOL_PROFILE = "-profile";
	public static final String TOOL_VERSION = "-version";
	public static final String TOOL_HELP = "-help";
	public static final String TOOL_A = "-A";
	public static final String TOOL_J = " -J";
	public static final String TOOL_X = "-X";
	public static final String TOOL_WERROR = "-Werror";
	public static final String TOOL_AT = "@";

}

public class JavaCompilers implements CompilerInterface {
	private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
	private String[] arguments = null;
	private String classDir;
	private String javaDir;
	private OutputStream out;
	private InputStream in;
	private OutputStream err;

	public void JavaComilers(InputStream in, OutputStream out,
			OutputStream err, String... arguments) {
		this.in = in;
		this.out = out;
		this.err = err;
	}

	public String[] getArguments() {

		return arguments;
	}

	public int run() {
		int result = javaCompiler.run(this.in, this.out, this.err, arguments);
		return result;
	}

	public void setClassDir(String dir) {
		this.classDir = dir;
	}

	public String getClassDir() {
		return classDir;
	}

	public String getJavaDir() {
		return this.javaDir;
	}

	public void setJavaDir(String dir) {
		this.javaDir = dir;
	}

	public void compileAll() {
		Path path = new Path(this.javaDir);
		path.scanner(new PathInter() {

			@Override
			public void find(File file) {
				if (file.isFile() && file.getName().contains(".java")) {

					int i = compile(file.getPath());
					if (out != null) {
						try {
							if (i == 0) {
								out.write((file.getName() + ":编译成功").getBytes());
							} else {
								out.write((file.getName() + ":编译失败").getBytes());
							}
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();

						}
					} else {
						if (i == 0) {
							System.out.println(file.getName() + ":编译成功");
						} else {
							System.out.println(file.getName() + ":编译失败");
						}
					}

				}
			}

		});
	}

	@SuppressWarnings("unused")
	private int compile() {
		int result = javaCompiler.run(this.in, this.out, this.err, arguments);
		return result;
	}

	public int compile(String classDir, String java) {
		this.arguments = new String[4];
		this.arguments[0] = TOOL_VERBOSE;
		this.arguments[1] = TOOL_D;
		this.arguments[2] = classDir;
		this.arguments[3] = java;
		int result = javaCompiler.run(this.in, this.out, this.err, arguments);
		return result;
	}

	public int compile(String java) {
		if (this.classDir == null) {
			this.arguments = new String[2];
			this.arguments[0] = TOOL_VERBOSE;
			this.arguments[1] = java;
		} else {
			this.arguments = new String[4];
			this.arguments[0] = TOOL_VERBOSE;
			this.arguments[1] = TOOL_D;
			this.arguments[2] = classDir;
			this.arguments[3] = java;
		}
		int result = javaCompiler.run(this.in, this.out, this.err, arguments);
		return result;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public OutputStream getErr() {
		return err;
	}

	public void setErr(OutputStream err) {
		this.err = err;
	}
}
