package com.YaNan.frame.servlets;

public interface REQUEST_METHOD {
		public final static int DELETE = 0 ;
		public final static int GET = 1;
		public final static int POST = 2;
		public final static int PUT = 3;
		public final static int HEAD = 4;
		public final static int OPTIONS = 5;
		public final static int TRACE = 6;
		public static String getRequest(String url){
			int index = url.lastIndexOf("@");
			if(index<0||index==url.length())
				return url;
			String mark = url.substring(index+1);
			return url.substring(0,index+1)+getRequestMethod(Integer.valueOf(mark));
		}
		public static String getRequestMethod(int type) {
			switch (type) {
			case 0:
				return "DELETE";
			case 1:
				return "GET";
			case 2:
				return "POST";
			case 3:
				return "PUT";
			case 4:
				return "HEAD";
			case 5:
				return "OPTIONS";
			case 6:
				return "TRACE";
			default:
				return "unknow";
			}
		}
}
