package com.YaNan.frame.core.debug;

import org.apache.catalina.startup.Bootstrap;

public class Main {
	public static void main(String[] args) {
		System.out.println(System.getProperty("catalina.home"));
		Bootstrap bs = new Bootstrap();
		bs.main(args);
	}
}
