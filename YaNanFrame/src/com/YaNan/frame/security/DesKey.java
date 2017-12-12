package com.YaNan.frame.security;

import javax.crypto.SecretKey;

public class DesKey {
	public SecretKey getPrivate_key() {
		return private_key;
	}
	public void setPrivate_key(SecretKey private_key) {
		this.private_key = private_key;
	}
	public SecretKey getPublic_key() {
		return public_key;
	}
	public void setPublic_key(SecretKey public_key) {
		this.public_key = public_key;
	}
	private SecretKey private_key;
	private SecretKey public_key;
	public DesKey(){};
	public DesKey( SecretKey private_key, SecretKey public_key){
		this.public_key=public_key;
		this.private_key=private_key;
	}
}
