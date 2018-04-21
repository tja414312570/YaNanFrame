package com.YaNan.frame.path;

/**
 * javaEnv static class
 * 
 * @author Administrator
 *
 */
public class JavaEnv {
	public static String javaVersion = System.getProperty("java.version");
	public static String javaVendor = System.getProperty("java.vendor");
	public static String javaVendorUrl = System.getProperty("java.vendor.url");
	public static String javaHome = System.getProperty("java.home");
	public static String javaSpecifitionVersion = System
			.getProperty("java.specification.version");
	public static String javaSpecifitionVendor = System
			.getProperty("java.specification.vendor");
	public static String javaSpecifitionName = System
			.getProperty("java.specifition.name");
	public static String vmSpecificationVersion = System
			.getProperty("java.vm.specification.version");
	public static String vmSpecificationVendor = System
			.getProperty("java.vm.specification.vendor");
	public static String vmSpecificationName = System
			.getProperty("java.vm.specification.name");
	public static String vmVersion = System.getProperty("java.vm.version");
	public static String vmVendor = System.getProperty("java.vm.vendor");
	public static String vmName = System.getProperty("java.vm.name");
	public static String classVersion = System
			.getProperty("java.class.version");
	public static String classPath = System.getProperty("java.class.path");
	public static String libraryPat = System.getProperty("java.library.path");
	public static String ioTmpdir = System.getProperty("java.io.tmpdir");
	public static String compiler = System.getProperty("java.compiler");
	public static String extDirs = System.getProperty("java.ext.dirs");

	public static String getJavaVersion() {
		return javaVersion;
	}

	public static String getJavaVendor() {
		return javaVendor;
	}

	public static String getJavaVendorUrl() {
		return javaVendorUrl;
	}

	public static String getJavaHome() {
		return javaHome;
	}

	public static String getJavaSpecifitionVersion() {
		return javaSpecifitionVersion;
	}

	public static String getJavaSpecifitionVendor() {
		return javaSpecifitionVendor;
	}

	public static String getJavaSpecifitionName() {
		return javaSpecifitionName;
	}

	public static String getVmSpecificationVersion() {
		return vmSpecificationVersion;
	}

	public static String getVmSpecificationVendor() {
		return vmSpecificationVendor;
	}

	public static String getVmSpecificationName() {
		return vmSpecificationName;
	}

	public static String getVmVersion() {
		return vmVersion;
	}

	public static String getVmVendor() {
		return vmVendor;
	}

	public static String getVmName() {
		return vmName;
	}

	public static String getClassVersion() {
		return classVersion;
	}

	public static String getClassPath() {
		return classPath;
	}

	public static String getLibraryPat() {
		return libraryPat;
	}

	public static String getIoTmpdir() {
		return ioTmpdir;
	}

	public static String getCompiler() {
		return compiler;
	}

	public static String getExtDirs() {
		return extDirs;
	}
}
