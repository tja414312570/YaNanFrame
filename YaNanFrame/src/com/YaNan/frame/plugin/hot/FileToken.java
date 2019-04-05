package com.YaNan.frame.plugin.hot;

public class FileToken {
	private String fileName;
	private Long hash;
	private long lastModif;
	public FileToken(String fileName, Long hash, long lastModif) {
		super();
		this.fileName = fileName;
		this.hash = hash;
		this.lastModif = lastModif;
	}
	public FileToken(Long hash, long lastModif) {
		super();
		this.hash = hash;
		this.lastModif = lastModif;
	}
	public FileToken() {
		super();
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getHash() {
		return hash;
	}
	public void setHash(Long hash) {
		this.hash = hash;
	}
	public long getLastModif() {
		return lastModif;
	}
	public void setLastModif(long lastModif) {
		this.lastModif = lastModif;
	}
}
