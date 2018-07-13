package com.YaNan.frame.servlets.session.entity;

import java.util.Date;

public class TokenCell {
	private String tokenId;
	private Date createDate;
	private String other;
	private int timeOut;
	public TokenCell(String tokenId, Date date, String other) {
		this.tokenId=tokenId;
		this.createDate=date;
		this.other=other;
	}
	public TokenCell() {
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	@Override
	public String toString() {
		return "TokenCell [tokenId=" + tokenId + ", createDate=" + createDate + ", other=" + other + "]";
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
}
