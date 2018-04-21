package com.YaNan.frame.debug.servlets.session;

import java.util.ArrayList;
import java.util.List;

public class roles {
	public static void main(String[] args) {
		Object kk = null;
		System.out.println(kk.toString());
		System.out.println(Math.pow(4, 0.5));
		System.out.println(getList(54));
	}
	public static List<Integer> getList(int num){
		List<Integer> ints = new ArrayList<Integer>();
		int sum = 0;
		int n = (int) Math.floor(Math.pow(num,0.5));
		for(int i=n;i>0;i++){
			int x = (int) Math.pow(i,2);
			sum+=x;
			if(sum==num){
				ints.add(x);
				break;
			}else if(sum>num){
				sum-=x;
			}else{
				ints.add(x);
			}
		}
		return ints;
	}
}
