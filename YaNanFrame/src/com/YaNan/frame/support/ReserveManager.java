package com.YaNan.frame.support;

import com.YaNan.frame.service.ClassInfo;

@ClassInfo(version = 0)
public class ReserveManager {
	private boolean enable = false;
	private static ReserveManager manager;

	private ReserveManager() {
	}

	public static ReserveManager getReserve() {
		if (manager == null)
			manager = new ReserveManager();
		return manager;
	}

	public void enable(boolean e) {
		System.out.println("启用备用系统");
		this.enable = e;
	}

	public boolean enable() {
		return this.enable;
	}
}
