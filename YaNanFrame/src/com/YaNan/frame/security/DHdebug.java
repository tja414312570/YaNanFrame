package com.YaNan.frame.security;

import java.io.File;

import javax.crypto.SecretKey;

public class DHdebug {
	public static void main(String[] args) throws Exception {
		//try to get key files
//		try {
//			DesKey dk = DH.createKey();
//			System.out.println("获得dk"+dk);
//			System.out.println("保存dk");
//			DH.saveKey(dk);
//			System.out.println("获取dk从文件"+DH.getKey());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// 初始话发送秘钥
		String esrc = "123456";
			String src = "YaNan";
			//获取秘钥
			System.out.println(new File("private_key.dhk").exists());
			DH.setKeyFile(new File("public_key.dhk"), DH.TYPE.public_key);
			DH.setKeyFile(new File("private_key.dhk"), DH.TYPE.private_key);
			SecretKey senderDesKey = DH.getKey(DH.TYPE.private_key);
			SecretKey receiverDesKey = DH.getKey(DH.TYPE.public_key);
			//设置公钥与私钥
			DH.setReceiverDesKey(receiverDesKey);
			DH.setSenderDesKey(senderDesKey);
			//加密
			 String str =DH.encryptAsString(src);
			System.out.println("DH 加密："+str);
			
			//解密
			str = DH.decryptAsString(src);
			System.out.println("DH 解密："+str);
		// date storeid uuid 
//		Date date = new Date();
//		SimpleDateFormat sf= new SimpleDateFormat("yyMMddHHmm");
//		System.out.println(sf.format(new Date()));
//		int id = 124;
//		int uuid= (int) (Math.random()*100);
//		System.out.println(sf.format(date)+id+uuid);
		
	}
}
