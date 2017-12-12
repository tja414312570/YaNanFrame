package com.YaNan.frame.Native;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CopyUnit implements Runnable{
	private int buffSize = 1024;
	//总文件
	private List<File> TotalFiles = new ArrayList<File>();
	//出错的文件
	private List<File> errorFiles = new ArrayList<File>();
	//完成的文件
	private List<File> CompletedFiles = new ArrayList<File>();
	public List<File> getTotalFiles() {
		return TotalFiles;
	}
	public void setTotalFiles(List<File> totalFiles) {
		TotalFiles = totalFiles;
	}
	public List<File> getCompletedFiles() {
		return CompletedFiles;
	}
	public void setCompletedFiles(List<File> completedFiles) {
		CompletedFiles = completedFiles;
	}
	
	@Override
	public void run() {
		if(this.TotalFiles.size()>=1){
			Iterator<File> file = this.TotalFiles.iterator();
			while(file.hasNext()){
				
				File sou = file.next();
				if (sou.exists()) {
					try {
					FileInputStream fis;
					fis = new FileInputStream(file.next());
					FileOutputStream fos = new FileOutputStream(sou);
					byte[] fileBuffer = new byte[this.buffSize];
					int line;
					while ((line = fis.read(fileBuffer)) != -1)
						fos.write(fileBuffer, 0, line);
					fos.close();
					fis.close();
				} catch ( IOException e) {
					this.errorFiles.add(sou);
				}
				}else{
					this.errorFiles.add(sou);
				}
				
				
			}
			
		}
		
	}
	public void excute(){
		Thread thread = new Thread(this);
		thread.start();
	}
	public List<File> getErrorFiles() {
		return errorFiles;
	}
	public void setErrorFiles(List<File> errorFiles) {
		this.errorFiles = errorFiles;
	}

}
