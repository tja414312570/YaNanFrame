package com.YaNan.frame.core.session.debug;

import java.util.regex.Pattern;

public class URLPattern {

	public static void main(String[] args) {
		Pattern pattern = Pattern  
	            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		String str = "http://localhost:8083/UFO_USER/login.html";
		System.out.println(pattern.matcher(str).matches());
	}

}
