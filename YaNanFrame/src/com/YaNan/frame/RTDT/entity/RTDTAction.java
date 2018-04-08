 package com.YaNan.frame.RTDT.entity;
 
 public class RTDTAction {
   private String name;
   private String method;
   private String CLASS="";
   private String type;
   private String mark="";
   private String pkg;
   
   public String getName() { return this.name; }
   
   public void setName(String name) {
     this.name = name;
   }
   
   public String getMethod() { return this.method; }
   
   public void setMethod(String method) {
     this.method = method;
   }
   
   public String getCLASS() { return this.CLASS; }
   
   public void setCLASS(String cLASS) {
     this.CLASS = cLASS;
   }
   
   public String getType() { return this.type; }
   
   public void setType(String type) {
     this.type = type;
   }
   
   public String getMark() { return this.mark; }
   
   public void setMark(String mark) {
     this.mark = mark;
   }
   
public String getPkg() {
	return pkg;
}

public void setPkg(String pkg) {
	this.pkg = pkg;
}

@Override
public String toString() {
	return "RTDTAction [name=" + name + ", method=" + method + ", CLASS=" + CLASS + ", type=" + type + ", mark=" + mark
			+ ", pkg=" + pkg + "]";
}
 }


