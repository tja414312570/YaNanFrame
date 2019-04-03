package com.YaNan.frame.plugin.hot;

import java.io.File;

public interface ClassUpdateListener {
	void updateClass(Class<?> originClass,Class<?> updateClass,Class<?> updateOrigin,File updateFile);
}
