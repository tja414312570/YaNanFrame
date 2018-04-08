package com.YaNan.frame.RTDT.actionSupport;

import com.YaNan.frame.RTDT.entity.RequestAction;
import com.YaNan.frame.RTDT.entity.ResponseAction;
import com.YaNan.frame.core.reflect.ClassLoader;

public interface RTDTActionInterface
{
  public abstract void doContext(RequestAction paramRequestAction, ResponseAction response, ClassLoader paramClassLoader);
}


/* Location:              /Users/yanan/Desktop/RTDT 1.1.2.jar!/com/YaNan/frame/RTDT/actionSupport/RTDTActionInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */