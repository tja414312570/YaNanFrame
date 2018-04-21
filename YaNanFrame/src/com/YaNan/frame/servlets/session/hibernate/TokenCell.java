package com.YaNan.frame.servlets.session.hibernate;

import com.YaNan.frame.hibernate.database.annotation.Column;

public class TokenCell {
	@Column(type="varchar",Primary_Key=true,length=126)
	private String tokenId;
	@Column(type="date")
	private String date;
	@Column(type="date")
	private String character;
}
