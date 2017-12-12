package com.YaNan.frame.security.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.YaNan.frame.security.SystemEncryption;


public class debug {
	public static void main(String[] args) {
		SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(sf.format(new Date()));
		String data = "Account";
		String md2Encode = md2Jdk(data);
		System.out.println("md2加密算法："+md2Encode);
		System.out.println("md2加密长度："+md2Encode.length());
		
		String md5Encde = md5Jdk(data);
		System.out.println("md5加密算法："+md5Encde);
		System.out.println("md5加密长度："+md5Encde.length());
		
		System.out.println("md5加密算法："+EncryptionUtil.md5Hex(data));
		System.out.println("sha256加密算法："+EncryptionUtil.sha256Hex(data));
		System.out.println("sha1加密算法："+EncryptionUtil.sha1Hex(data));
		System.out.println("sha256与md5混合加密算法："+EncryptionUtil.sha256Hex(EncryptionUtil.md5Hex(data)));
		System.out.println("系统专用加密："+SystemEncryption.encrypUserInfo("tja950233140710w"));
		//YaNanFrameWork0824
		//YaNan
		//System.out.println("服务器专用加密："+EncryptIrreversible.sha256AndMd5("123456789"));
	}
	public static String converByteToHexString(byte[] bytes){
		String result = "";
		for(int i=0;i<bytes.length;i++){
			int temp = bytes[i]&0xff;
			String tempHex = Integer.toHexString(temp);
			if(tempHex.length()<2){
				result+="0"+tempHex;
			}else{
				result +=tempHex;
			}
		}
		return result;
	}
	public static String md2Jdk(String message){
		String temp = "";
		MessageDigest md2Digest;
		try {
			md2Digest = MessageDigest.getInstance("MD2");
			byte[] encodeMd2Digest = md2Digest.digest(message.getBytes());
			temp = converByteToHexString(encodeMd2Digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return temp;
	}
	public static String md5Jdk(String message){
		String temp = "";
		MessageDigest md2Digest;
		try {
			md2Digest = MessageDigest.getInstance("MD5");
			byte[] encodeMd5Digest = md2Digest.digest(message.getBytes());
			temp = converByteToHexString(encodeMd5Digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return temp;
	}
}
