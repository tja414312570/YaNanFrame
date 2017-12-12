package com.YaNan.frame.hibernate.database.entity;

public class Tab {
	private String CLASS;
	private String db;
	private boolean checked;
	private boolean  enable;
	private boolean session;

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isSession() {
		return session;
	}

	public void setSession(boolean session) {
		this.session = session;
	}

	public String getCLASS() {
		return CLASS;
	}


	@Override
	public String toString() {
		return "Tab [CLASS=" + CLASS + ", db=" + db + ", checked=" + checked + ", enable=" + enable + ", session="
				+ session + "]";
	}

	public void setCLASS(String cLASS) {
		CLASS = cLASS;
	}
}
