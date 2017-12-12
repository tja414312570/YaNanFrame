package com.YaNan.frame.core.aspect;

public class debug {
	public static void main(String[] args) {
		AspectContainer  aspect = AspectContainer.getAspectContainer();
		aspect.init();
		System.out.println(aspect.getAspectPools().toString());
		System.out.println(aspect.match("com.JiuDing.action.user.User.signup"));
	}
}
