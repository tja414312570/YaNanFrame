 package com.YaNan.frame.RTDT.debug;
 
 import com.YaNan.frame.RTDT.actionSupport.RTDTActionInterface;
import com.YaNan.frame.RTDT.entity.RTDTAction;
import com.YaNan.frame.core.reflect.ClassLoader;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.HashMap;
 import java.util.Map;
 
 public class serilaze
 {
   public static void main(String[] args)
   {
     System.out.println(ClassLoader.implementOf(com.YaNan.frame.RTDT.actionSupport.TokenAction.class, RTDTActionInterface.class));
     System.out.println(ClassLoader.implementOf(RTDTAction.class, RTDTActionInterface.class));
     ClassLoader loader = new ClassLoader(RTDTAction.class);
     System.out.println(loader.hasMethod("doContext", new Class[] { com.YaNan.frame.RTDT.entity.RequestAction.class }));
     String str = "name=%E5%97%AF%E5%97%AF&age=&sex=&other=";
     System.out.println("orgin string" + str);
     String[] strs = str.split("&");
     Map<String, String> parameters = new HashMap<String, String>();
     try { String[] arrayOfString1;
       int j = (arrayOfString1 = strs).length; for (int i = 0; i < j; i++) { String block = arrayOfString1[i];
         String[] lines = block.split("=");
         if (lines.length > 1) {
           parameters.put(URLDecoder.decode(lines[0], "UTF-8"), URLDecoder.decode(lines[1], "UTF-8"));
         } else {
           parameters.put(URLDecoder.decode(lines[0], "UTF-8"), null);
         }
       }
     } catch (UnsupportedEncodingException e) {
       e.printStackTrace();
     }
     System.out.println(parameters.size());
     System.out.println(parameters.toString());
   }
 }


