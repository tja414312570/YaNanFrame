package com.a.encrypt;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class testRuntime {
	public static void main(String[] args) {
		 MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		    MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage(); //椎内存使用情况

		    long totalMemorySize = memoryUsage.getInit(); //初始的总内存

		    long maxMemorySize = memoryUsage.getMax(); //最大可用内存

		    long usedMemorySize = memoryUsage.getUsed(); //已使用的内存

		   System.out.println("TotalMemory "+totalMemorySize/(1024*1024)+"M");
		   System.out.println("FreeMemory "+ (totalMemorySize-usedMemorySize)/(1024*1024)+"M");
		   System.out.println("MaxMemory "+ maxMemorySize/(1024*1024)+"M");
		   System.out.println("UsedMemory "+ usedMemorySize/(1024*1024)+"M");
		
	}
	public static long getMBMemory(long size){
		while(size>1024)
			size=size/1024;
		return size;
	}
	
}
