package com.YaNan.frame.core.debug;

import com.YaNan.frame.core.servlet.InitServlet;

public class ServletScanner {
	public static void main(String[] args) {
		InitServlet is = InitServlet.getInstance();
		is.initByScanner();
	}
}
