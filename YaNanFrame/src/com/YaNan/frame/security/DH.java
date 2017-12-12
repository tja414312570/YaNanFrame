package com.YaNan.frame.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.apache.commons.codec.binary.Base64;

import com.YaNan.frame.hibernate.WebPath;


public class DH{
	public static enum TYPE{
		private_key, public_key
		
	}
	private static final int BUFSIZE = 1024;
	private static SecretKey receiverDesKey;
	private static SecretKey senderDesKey;
	private static Cipher cipher;
	private static File private_key_file =null;
	private static File public_key_file =null;
	
	
	static{
		try {
			cipher= Cipher.getInstance("DES/ECB/NoPadding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	public static void init(){
		DH.public_key_file= WebPath.getWebPath().get("public_key").toFile();
		DH.private_key_file= WebPath.getWebPath().get("private_key").toFile();
		//鑾峰彇绉橀挜
		SecretKey senderDesKey = DH.getKey(DH.TYPE.private_key);
		SecretKey receiverDesKey = DH.getKey(DH.TYPE.public_key);
		//璁剧疆鍏挜涓庣閽�
		DH.setReceiverDesKey(receiverDesKey);
		DH.setSenderDesKey(senderDesKey);
	}
	public static void main(String[] args) {
		try {
			DesKey dk = createKey();
			saveKey(dk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * to save a deskey
	 * @param dk
	 * @throws Exception
	 */
	public static void saveKey(DesKey dk) throws Exception {
		FileOutputStream rfos = new FileOutputStream(private_key_file);
		FileOutputStream ufos = new FileOutputStream(public_key_file);
		ObjectOutputStream roos = new ObjectOutputStream(rfos);
		ObjectOutputStream uoos = new ObjectOutputStream(ufos);
		roos.writeObject(dk.getPrivate_key());
		uoos.writeObject(dk.getPublic_key());
		roos.close();
		uoos.close();
	}
	/**
	 * create a deskey
	 * @return
	 * @throws Exception
	 */
	public static DesKey createKey() throws Exception{
			KeyPairGenerator sender = KeyPairGenerator.getInstance("DH");
			sender.initialize(512);
			KeyPair senderKeyPair = sender.generateKeyPair();
			byte[] senderPublicKeyEnc = senderKeyPair.getPublic().getEncoded();
			KeyFactory receiveKeyFactory = KeyFactory.getInstance("DH");
			X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(senderPublicKeyEnc);
			PublicKey receiverPublicKey = receiveKeyFactory.generatePublic(x509EncodeKeySpec);
			DHParameterSpec dhParameterSpec = ((DHPublicKey)receiverPublicKey).getParams();
			KeyPairGenerator receiverKeyPairGenerator = KeyPairGenerator.getInstance("DH");
			receiverKeyPairGenerator.initialize(dhParameterSpec);
			KeyPair receiverKeyPair = receiverKeyPairGenerator.generateKeyPair();
			PrivateKey receiverPrivateKey = receiverKeyPair.getPrivate();
			byte[] receiverPublicKeyEnc = receiverKeyPair.getPublic().getEncoded();
			//3.绉橀挜鏋勫缓
			KeyAgreement receiverkeyAgreement = KeyAgreement.getInstance("DH");
			receiverkeyAgreement.init(receiverPrivateKey);
			receiverkeyAgreement.doPhase(receiverPublicKey, true);
			SecretKey receiverDesKey = receiverkeyAgreement.generateSecret("DES");
			KeyFactory senderKeyFactory = KeyFactory.getInstance("DH");
			x509EncodeKeySpec= new X509EncodedKeySpec(receiverPublicKeyEnc);
			PublicKey senderPublicKey = senderKeyFactory.generatePublic(x509EncodeKeySpec);
			KeyAgreement senderKeyAgreement = KeyAgreement.getInstance("DH");
			senderKeyAgreement.init(senderKeyPair.getPrivate());
			senderKeyAgreement.doPhase(senderPublicKey, true);
			SecretKey senderDesKey = senderKeyAgreement.generateSecret("DES");
			DesKey dk = new DesKey();
			dk.setPrivate_key(senderDesKey);
			dk.setPublic_key(receiverDesKey);
			return dk;
	}
	
	public static void setReceiverDesKey(SecretKey receiverDesKey) {
		DH.receiverDesKey=receiverDesKey;
	}

	public static void setSenderDesKey(SecretKey senderDesKey) {
		DH.senderDesKey=senderDesKey;
	}
	public static boolean checkKey(){
		if(receiverDesKey==null||senderDesKey==null)
			return false;
		return decryptAsString("lTzuJFVFebM=").equals("YaNan");
	}
	public static String encryptAsString(String src) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, senderDesKey);
			byte[] result = cipher.doFinal(src.getBytes());
			return new String(Base64.encodeBase64String(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String encryptAsString(String src,SecretKey publicKey) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE,publicKey);
			byte[] result = cipher.doFinal(src.getBytes());
			return new String(Base64.encodeBase64String(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String decryptAsString(String src) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, receiverDesKey);
			return new String(cipher.doFinal(Base64.decodeBase64(src)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String decryptAsString(String src,SecretKey privateKey) {
		try {
			cipher.init(Cipher.DECRYPT_MODE,privateKey);
			return new String(cipher.doFinal(Base64.decodeBase64(src)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static DesKey getKey(){
		try {
			DesKey dk = new DesKey();
			FileInputStream rfis = new FileInputStream(private_key_file);
			ObjectInputStream rois = new ObjectInputStream(rfis);
			dk.setPrivate_key((SecretKey) rois.readObject());
			FileInputStream ufis = new FileInputStream(public_key_file);
			ObjectInputStream uois = new ObjectInputStream(ufis);
			dk.setPrivate_key((SecretKey) uois.readObject());
			rois.close();
			uois.close();
			rfis.close();
			ufis.close();
			return dk;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static SecretKey getKey(File file){
		try {
			FileInputStream rfis = new FileInputStream(file);
			ObjectInputStream rois = new ObjectInputStream(rfis);
			SecretKey sk = (SecretKey) rois.readObject();
			rois.close();
			rfis.close();
			return sk;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static SecretKey getKey(TYPE keyType) {
		File file;
		if(keyType==TYPE.private_key){
			 file= private_key_file;
		}else{
			file=public_key_file;
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			SecretKey sk = (SecretKey) ois.readObject();
			ois.close();
			fis.close();
			return sk;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void setKeyFile(File file, TYPE keyType) {
		switch(keyType){
		case private_key:
			DH.private_key_file = file;
			break;
		case public_key:
			DH.public_key_file = file;
			break;
		}
		
			
	}
	public static void encryptFile0(final File orginFile,final File encryptFile, SecretKey publicKey) {
		try {
			byte[] buffer = new byte[BUFSIZE];
			final FileInputStream fis = new FileInputStream(encryptFile);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(encryptFile), cipher);
			int len;
			while ((len = fis.read(buffer)) != -1)
				cos.write(buffer, 0, len);
			cos.flush();
			fis.close();
			cos.close();
		} catch (IOException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	public static void encryptFile(final File orginFile,final File encryptFile, SecretKey publicKey) {
		try {
			byte[] buffer = new byte[BUFSIZE];
			final FileInputStream fis = new FileInputStream(orginFile);
			final FileOutputStream fos = new FileOutputStream(encryptFile);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int len;
			while ((len = fis.read(buffer)) != -1){
			      fos.write(cipher.doFinal(buffer), 0, len);  
			}
			fos.flush();
			fis.close();
			fos.close();
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 加密字节
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static byte[] encryptByte(byte[] bytes,SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	  cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	  return cipher.doFinal(bytes);  
	}
	/**
	 * 解密字节
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static byte[] decryptByte(byte[] bytes,SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	  cipher.init(Cipher.DECRYPT_MODE, secretKey);
	  return cipher.doFinal(bytes);  
	}
	/**
	 * 加密文件
	 * @param orginFile
	 * @param encryptFile
	 * @param secretKey
	 */
	public static void encryptFiles(final File orginFile,final File encryptFile, SecretKey secretKey) {
		try {
			   FileInputStream is = new FileInputStream(encryptFile);  
			   FileOutputStream out = new FileOutputStream(orginFile);  
			   cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			   CipherOutputStream cos = new CipherOutputStream(out, cipher);  
			   byte[] buffer = new byte[BUFSIZE];
			   int len;  
			   while ((len = is.read(buffer)) >= 0) {
			       cos.write(cipher.doFinal(buffer), 0, len);  
			   }
			   out.close();  
			   cos.close();
			   is.close();  
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	public static void decryptFile(final File encryptFile,final File decryptFile, SecretKey secretKey) {
		try {
			   FileInputStream is = new FileInputStream(encryptFile);  
			   FileOutputStream out = new FileOutputStream(decryptFile);  
			   cipher.init(Cipher.DECRYPT_MODE, secretKey);
			   byte[] buffer = new byte[BUFSIZE];
			   int len;  
			   while ((len = is.read(buffer)) >= 0) {
			       out.write(cipher.doFinal(buffer), 0, len);  
			   }
			   out.close();  
			   is.close();  
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	

}
