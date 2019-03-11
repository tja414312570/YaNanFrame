package com.YaNan.frame.plugin;

import java.util.ArrayList;
import java.util.List;

public class InvokeStack {
	private static InheritableThreadLocal<InvokeStack> stackLocal = new InheritableThreadLocal<InvokeStack>();
	private List<Object> info = new ArrayList<Object>();
	public static InvokeStack getStack(){
		InvokeStack stack = stackLocal.get();
		if(stack==null){
			stack = new InvokeStack();
			stackLocal.set(stack);
		}
		return stack;
	}
	public static void addStack(Object object){
		getStack().info.add(object);
	}
	public static List<Object> getInvokeStack() {
		return getStack().info;
	}
}
