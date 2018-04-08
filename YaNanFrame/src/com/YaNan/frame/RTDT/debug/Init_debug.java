 package com.YaNan.frame.RTDT.debug;
 
 import com.YaNan.frame.RTDT.context.ActionManager;
import com.YaNan.frame.RTDT.entity.ActionEntity;

import java.io.PrintStream;
 import java.util.Iterator;
 import java.util.Map;
 
 public class Init_debug
 {
   public static void main(String[] args)
   {
     java.io.File file = new java.io.File("src/RDT.xml");
     System.out.println("rdtd file exists:" + file.exists());
     ActionManager manager = ActionManager.getActionManager();
     manager.init(file);
     System.out.println("manager is available:" + manager.isAvailable());
     Map<String, ActionEntity> map = manager.getActionPools();
     Iterator<String> iterator = map.keySet().iterator();
     System.out.println("===============================rdtd action mapping=========");
     while (iterator.hasNext()) {
       String name = (String)iterator.next();
       System.out.println("action:" + name);
       System.out.println("data:" + map.get(name));
     }
     
     System.out.println("action [test] is exists:" + manager.hasAction("test"));
   }
 }


