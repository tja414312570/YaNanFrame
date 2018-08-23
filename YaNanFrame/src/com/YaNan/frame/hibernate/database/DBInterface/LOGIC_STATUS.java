package com.YaNan.frame.hibernate.database.DBInterface;

public interface LOGIC_STATUS {
	public static final int NORMAL=24;//逻辑正常
	public static final int DISABLE=8;//逻辑禁用
	public static final int REMOVE=-1;//逻辑删除
	public static final int FALSE = 0;//逻辑false
	public static final int TRUE = 1;//逻辑true
	public static final int ADD = 1;//逻辑加
	public static final int SUB = -1;//逻辑减
	public static final int AUTHED = 24;//逻辑已认证
	public static final int AUTHING = 8;//逻辑待认证
	public static final int NOTAUTH = 0;//逻辑未认证
	public static final int AUTHFAILED = -1;//逻辑认证失败
}
