package a.test.hibernate;

import com.YaNan.frame.hibernate.database.annotation.Tab;

@Tab(DB="YaNan_Demo")
public class Parents {
	private String parentPrivate;
	public String parentPublic;
	public String sex;
	public String getParentPrivate() {
		return parentPrivate;
	}
	public void setParentPrivate(String parentPrivate) {
		this.parentPrivate = parentPrivate;
	}
}
