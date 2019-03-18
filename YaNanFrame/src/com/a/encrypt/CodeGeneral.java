package com.a.encrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.YaNan.frame.path.Path;
import com.YaNan.frame.path.Path.PathInter;

public class CodeGeneral {
	public static int sumFile = 0;// 总文件数
	public static int sumLine = 0;// 有效行数
	public static int allLine = 0;// 总行数
	public static int sumUnit = 0;// 总字数
	public static int maxLine = 0;//最大行数
	public static long size;
	public static long millSec = Long.MAX_VALUE;

	public static void main(String[] args) {
		try {
			Path path = new Path("/Volumes/GENERAL/svnworkspace/EM");/// Users/yanan/Workspaces/MyEclipse
																			/// 2017
																			/// CI/springFramework
			path.filter("**frame**.java");
			path.scanner(new PathInter() {
				@Override
				public void find(File file) {
					try {
						size+=file.length();
						int lineCount = 0;
						BufferedReader br = new BufferedReader(
								new InputStreamReader(new FileInputStream(file), "utf-8"));// 以utf-8
																							// 格式读入，若文件编码为gkb
																							// 则改为gbk
						String s = null;
						while ((s = br.readLine()) != null) {
							String line = s.replaceAll("\\s", "");// \\s表示
																	// 空格,回车,换行等空白符,
							if (!"".equals(line) && !line.startsWith("//") && !line.startsWith("/*")
									&& !line.startsWith("/**") && !line.startsWith("*")) {
								sumLine++;
								lineCount++;
								sumUnit += line.length();
							}
							if(line.indexOf("@Register")>-1)
								System.out.println(file.getAbsolutePath()+"  "+line);
							allLine++;
						}
						System.out.println(file.getAbsolutePath().replace("/Volumes/GENERAL/git/Frame/YaNanFrame", "")
								+ "==>" + lineCount);
						sumFile++;
						if(lineCount>=maxLine)
							maxLine = lineCount;
						br.close();
						if(file.lastModified()<millSec)
							millSec = file.lastModified();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			System.out.println("一共：" + sumFile + " 个文件\t\t" + allLine + " 行代码！");
			System.out.println( sumLine + " 行有效代码\t\t" + sumUnit + " 个有效字符");
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  Date date = new Date(millSec);
			System.out.println( "其中 最多行数: "+maxLine+"  "+size+" 最早日期:"+sdf.format(date));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
