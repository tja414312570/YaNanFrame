 package com.YaNan.frame.RTDT.actionSupport;
 
import com.YaNan.frame.RTDT.entity.ActionEntity;
 import com.YaNan.frame.RTDT.entity.RequestAction;
import com.YaNan.frame.RTDT.entity.ResponseAction;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.session.Token;

import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 
 public class TokenAction implements RTDTActionInterface
 {
   protected transient Token TokenContext;
   protected transient ActionEntity ActionContext;
   protected transient RequestAction RequestContext;
   protected transient ResponseAction ResponseContext;
   private final Log log = PlugsFactory.getPlugsInstance(Log.class, TokenAction.class);
   
   public void doContext(RequestAction request, ResponseAction response,ClassLoader loader)
   {
     this.RequestContext = request;
     this.TokenContext = request.getToken();
     this.ActionContext = request.getActionEntity();
     this.ResponseContext = response;
     Field[] fields = loader.getDeclaredFields();
     Field[] arrayOfField1; int j = (arrayOfField1 = fields).length; for (int i = 0; i < j; i++) { Field field = arrayOfField1[i];
       if (field.getAnnotation(com.YaNan.frame.servlets.session.annotation.TokenObject.class) != null) {
         Class<?> cls = field.getType();
         String method = ClassLoader.createFieldSetMethod(field);
         if ((loader.hasMethod(method, new Class[] { cls })) && (this.TokenContext.get(cls) != null)) {
           try {
             loader.set(field.getName(), new Object[] { this.TokenContext.get(cls) });
           }
           catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
            log.error(e);
           }
         }
       }
     }
   }
 }


