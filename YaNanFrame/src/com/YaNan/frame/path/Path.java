package com.YaNan.frame.path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.YaNan.frame.utils.PathMatcher;

/*
 * encoding = "utf-8"
 * 用于路径的批量操作，目前只用于 删除，复制，移动
 */
public class Path {
	private File file;
	private List<String> filter = new ArrayList<String>();
	private int scanLevel=-1;
	private int currentScanLevel=0;
	public boolean createWhenNotExists = true;
	public boolean deleteWhenExists = true;
	private int num;
	public int getNum() {
		return num;
	}

	private File target;
	private int buffSize = 1024;


	public File getTarget() {
		return target;
	}

	public int getBuffSize() {
		return buffSize;
	}

	// 传入单个路径
	public Path(String source) {
		// 添加事件
		File file = new File(source);
		if (file.exists()) {
			this.file = file;
		} else {
			throw new RuntimeException("源路径：" + file.toString() + "无效");
		}
	}


	public Path(File file) {
		if (file.exists()) {
			this.file=file;
		} else {
			throw new RuntimeException("源路径：" + file.toString() + "无效");
		}
	}

	// 复制到（目标路径）
	public boolean copyTo(String target) throws IOException {
		this.target = new File(target);
		if (!this.target.exists()) {
			if (!this.createWhenNotExists)
				if (!this.dialog("目标路径不存在，是否需要新建(yes:no)"))
					return false;
			this.target.mkdirs();
		}
			if (!copyPath(file, this.target))
				return false;
		// 更新事件
		return true;
	}

	// 移动到（目标路径）
	public boolean moveTo(String target) throws IOException {
		if (this.copyTo(target)) {
			this.del();
			return true;
		} else {
			this.delete(new File(target));
			return false;
		}
	}

	// 删除源路径及以下所有项
	public boolean del() {
		delete(file);
		return true;
	}

	public void scanner(PathInter p) {
			doScanner(p, file);
	}
	public void scanner(PathInter p,int scanLevel) {
		this.scanLevel=scanLevel;
		if(this.scanLevel==0)
			doScanner(p, file);
		else
			doScanner(p,file,null);
	}
	public void scanner(PathInter p,boolean scanAllPath) {
		scanner(p,scanAllPath?0:1);
	}
	public void scannerOnCurrentPath(PathInter p) {
		this.scanLevel=1;
			doScanner(p, file,null);
	}
	
	private void doScanner(PathInter p, File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if(list!=null)
			for (File f : list)
				doScanner(p, f,file);
		}
		this.find(p,file);
	}
	
	private void find(PathInter p, File file) {
		if(filter.isEmpty())
			p.find(file);
		else for(String word : filter)
			if(PathMatcher.match(word, file.getAbsolutePath()).isMatch())
				p.find(file);
	}

	private void doScanner(PathInter p, File file,File currentPath) {
		if(this.file.getAbsolutePath().equals(file.getAbsolutePath()))
			this.currentScanLevel=0;
		else{
			String filePathSpace = file.getAbsolutePath().replace(File.separator+file.getName(), "");
			if(this.file.getAbsolutePath().equals(filePathSpace))
				this.currentScanLevel=0;
			else{
				String orginPathSpace = this.file.getAbsolutePath().substring(this.file.getAbsolutePath().length()-1).equals(File.separator)?this.file.getAbsolutePath():this.file.getAbsolutePath()+File.separator;
				String space = filePathSpace.replace(orginPathSpace,"");
				this.currentScanLevel = space.split(File.separator.equals("\\")?"\\\\":"/").length;
			}
		}
		if(scanLevel>0&&currentScanLevel>scanLevel)
			return;
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if(list!=null)
			for (File f : list)
				if(currentScanLevel<=scanLevel||scanLevel==-1)
				doScanner(p, f,file);
				else{
					this.find(p,file);
				}
		}
		this.find(p,file);
	}

	public int getCurrentScanLevel() {
		return currentScanLevel;
	}

	// 内部文件删除
	private void delete(File file) {
		// 更新事件
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if(list!=null)
			for (File f : list)
				delete(f);
		}
		file.delete();
	}

	// 路径的处理
	private boolean copyPath(File sou, File tar) throws IOException {
		this.num++;
		tar = new File(tar.toPath() + "/" + sou.getName());
		if (tar.exists()) {
			if (!this.deleteWhenExists)
				if (!dialog("目标路径:" + tar.toPath() + "\n已存在，是否删除？(yes:no)"))
					return false;
			this.delete(tar);
		}
		// 更新事件
		if (sou.isDirectory()) {
			tar.mkdirs();
			File[] list = sou.listFiles();
			if(list!=null)
			for (File f : list)
				this.copyPath(f, tar);
		} else {
			this.copyFile(sou, tar);
		}
		return true;
	}

	// 单个复制文件
	private boolean copyFile(File sou, File tar) throws IOException {
		if (sou.exists()) {
			FileInputStream fis = new FileInputStream(sou);
			FileOutputStream fos = new FileOutputStream(tar);
			byte[] fileBuffer = new byte[this.buffSize];
			int line;
			while ((line = fis.read(fileBuffer)) != -1)
				fos.write(fileBuffer, 0, line);
			fos.close();
			fis.close();
			return true;
		}
		return false;
	}

	// 对话框
	@SuppressWarnings("resource")
	public boolean dialog(String content) {
		Scanner s = new Scanner(System.in);
		System.out.println(content);
		String callBack = s.next().toLowerCase();
		if (!callBack.equals("yes") && !callBack.equals("no")) {
			System.out.println("输入错误:");
			return dialog(content);
		}
		s.close();
		return callBack.equals("yes");
	}

	public int getScanLevel() {
		return scanLevel;
	}

	public void setScanLevel(int scanLevel) {
		this.scanLevel = scanLevel;
	}

	public static interface PathInter {
		public void find(File file);
	}

	public void filter(String... strings) {
		for(String word : strings)
			this.filter.add(word);
	}
}
