package com.YaNan.frame.security;

import com.YaNan.frame.security.encryption.EncryptionUtil;

public class SystemEncryption {
	public static String encrypUserInfo(String arg){
		return EncryptionUtil.md5Hex(EncryptionUtil.sha256Hex(arg));
	}
}
