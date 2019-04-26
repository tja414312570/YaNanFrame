package com.YaNan.frame.hibernate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Path Class,this class was used WebPath Class,it has three filed,path
 * realPath,originPath
 * 
 * @author YaNan
 *
 */
public class Path {
	public String path;
	public String realPath;
	public String originPath;
	public File file;
	private final Logger log = LoggerFactory.getLogger( Path.class);
	private Path rootPath = WebPath.getWebPath().getRoot();

	public void Colone(Path path) {
		this.path = path.path;
		this.realPath = path.realPath;
		this.originPath = path.originPath;
		this.file = path.file;
	}

	public Path(String path) {
		path = path.replace("/", File.separator);
		if (this.rootPath == null) {
			this.originPath = File.separator;
			this.path = File.separator;
			this.realPath = path;
		} else {
			this.originPath = path;
			this.path = this.rootPath.path + path;
			this.realPath = this.rootPath.realPath + path;
		}
		mkdir(this.realPath);
	}

	public Path(Path rootPath, String path) {
		this.rootPath = rootPath;
		path = path.replace("/", File.separator);
		if (this.rootPath == null) {
			this.originPath = File.separator;
			this.path = File.separator;
			this.realPath = path;
		} else {
			this.originPath = path;
			this.path = this.rootPath.path + path;
			this.realPath = this.rootPath.realPath + path;
		}
		mkdir(this.realPath);
	}

	public String getPath() {
		return this.path;
	}

	public String getRealPath() {
		return this.realPath;
	}

	public String getOriginPath() {
		return this.originPath;
	}

	@Override
	public String toString() {
		return "Path [path=" + path + ", realPath=" + realPath + ", rootPath="
				+ rootPath + ", originPath=" + originPath + "]";
	}

	private void mkdir(String path) {
		File mkPath = new File(path);
		if (!mkPath.exists())
			if (path.lastIndexOf(File.separator) == (path.length() - 1)) {
				mkPath.mkdirs();
			} else {
				try {
					mkPath.createNewFile();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}
	}

	public File toFile() {
		return new File(this.realPath);
	}
}