package com.YaNan.frame.core.aspect.interfaces;

import com.YaNan.frame.core.aspect.JoinPoint;

public interface AspectInterface {
	public void after(JoinPoint joinPoint);
	public void before(JoinPoint joinPoint);
}
