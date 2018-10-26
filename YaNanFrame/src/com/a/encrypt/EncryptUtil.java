package com.a.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.security.EncryptService;
import com.sun.org.apache.xml.internal.security.utils.Base64;

@Register(attribute="*")
public class EncryptUtil implements EncryptService {

	@Override
	public Object encrypt(Object parameter, String... arguments) throws Exception {
		ByteArrayOutputStream byt=new ByteArrayOutputStream();
//
//		ObjectOutputStream obj=new ObjectOutputStream(byt);
//
//		obj.writeObject(parameter);
//
//		byte[] bytes=byt.toByteArray();
//		for(byte bt : bytes)
//		System.out.println(bt);

		return Base64.encode(parameter.toString().getBytes());
	}

	@Override
	public Object descrypt(Object parameter, String... arguments) throws Exception {
		return new String((byte[])Base64.decode(parameter.toString().getBytes()));
	}
	
}
