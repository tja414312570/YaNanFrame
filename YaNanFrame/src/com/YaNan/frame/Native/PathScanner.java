package com.YaNan.frame.Native;

import java.io.File;

public class PathScanner {
	public static File[] scan(String path) {
		File f = new File(path);
		return f.listFiles();
	}
}
