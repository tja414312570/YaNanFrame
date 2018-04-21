package com.YaNan.frame.servlets.session.defaultConfigure;

import java.text.SimpleDateFormat;

import com.YaNan.frame.hibernate.database.annotation.Column;
import com.YaNan.frame.hibernate.database.annotation.Tab;
import com.YaNan.frame.servlets.session.entity.TokenCell;

@Tab(DB="YaNan_SYSTEM",name="Token")
public class TokenCellEntity {
	@Column(type="varchar",length=256,Primary_Key=true)
	private String tokenId;
	@Column(type="datetime")
	private String date;
	@Column(type="varchar",length=256)
	private String other;
	public TokenCellEntity(){};
	public TokenCellEntity(TokenCell tokenCell) {
	this.tokenId=tokenCell.getTokenId();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String sd = sdf.format(tokenCell.getCreateDate());
	this.date=sd;
	this.other=tokenCell.getOther();
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

}
