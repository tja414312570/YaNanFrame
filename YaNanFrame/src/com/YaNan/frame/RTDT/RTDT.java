 package com.YaNan.frame.RTDT;
 
 import com.YaNan.frame.RTDT.context.ActionManager;
import com.YaNan.frame.RTDT.context.NotifyManager;
 
 public class RTDT {
   public static ActionManager getActionManager() {
	   return ActionManager.getActionManager();
   }
   public static NotifyManager getNotifyManager() {
	   return NotifyManager.getManager(); 
   }
 }


