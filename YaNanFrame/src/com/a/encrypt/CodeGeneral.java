package com.a.encrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.YaNan.frame.path.Path;
import com.YaNan.frame.path.Path.PathInter;

public class CodeGeneral {
	public static int sumFile = 0;// 总文件数
	public static int sumLine = 0;// 有效行数
	public static int allLine = 0;// 总行数
	public static int sumUnit = 0;// 总字数
	public static int maxLine = 0;//最大行数

	public static void main(String[] args) {
		try {
			Path path = new Path("/Volumes/GENERAL/Web Projects/WX");/// Users/yanan/Workspaces/MyEclipse
																			/// 2017
																			/// CI/springFramework
			path.filter("**.java");
			path.scanner(new PathInter() {
				@Override
				public void find(File file) {
					try {
						if(file.getAbsolutePath().indexOf("frame")==-1)
							return;
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
							allLine++;
						}
						System.out.println(file.getAbsolutePath().replace("/Volumes/GENERAL/git/Frame/YaNanFrame", "")
								+ "==>" + lineCount);
						sumFile++;
						if(lineCount>=maxLine)
							maxLine = lineCount;
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			System.out.println("一共：" + sumFile + " 个文件\t\t" + allLine + " 行代码！");
			System.out.println( sumLine + " 行有效代码\t\t" + sumUnit + " 个有效字符");
			System.out.println( "其中最多行数: "+maxLine);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}