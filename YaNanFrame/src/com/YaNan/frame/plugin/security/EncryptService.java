package com.YaNan.frame.plugin.security;

import com.YaNan.frame.plugin.annotations.Service;

@Service
public interface EncryptService {
	public Object encrypt(Object parameter,String...  arguments) throws Exception;
	public Object descrypt(Object parameter,String... arguments) throws Exception;
}
