package com.YaNan.frame.servlets.session.defaultConfigure;

import com.YaNan.frame.hibernate.database.annotation.Column;
import com.YaNan.frame.hibernate.database.annotation.Tab;

@Tab(DB="YaNan_SYSTEM")
public class TokenDataEntity {
	@Column(type="varchar")
	public String TokenId;
	@Column(type="varchar")
	public String tKey;
	@Column(type="varchar")
	public String tValue;
	@Column(type="datetime")
	public String date;
	public String getTKey() {
		return tKey;
	}
	public void setTKey(String tKey) {
		this.tKey = tKey;
	}
	public String getTValue() {
		return tValue;
	}
	public void setTValue(String tValue) {
		this.tValue = tValue;
	}
	public String getTokenId() {
		return TokenId;
	}
	public void setTokenId(String tokenId) {
		TokenId = tokenId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "TokenDataEntity [TokenId=" + TokenId + ", tKey=" + tKey + ", tValue=" + tValue + ", date=" + date + "]";
	}
}
