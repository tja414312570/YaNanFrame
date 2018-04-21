 package com.YaNan.frame.RTDT.context.init;
 
 import com.YaNan.frame.RTDT.WebSocketServer;
import com.YaNan.frame.RTDT.context.ActionManager;
import com.YaNan.frame.RTDT.context.NotifyManager;
import com.YaNan.frame.RTDT.context.SocketConfigurator;
import com.YaNan.frame.RTDT.entity.ActionEntity;
import com.YaNan.frame.RTDT.entity.NotifyEntity;
import com.YaNan.frame.plugin.annotations.Register;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
 
 @Register
 public class RTDTContextInit implements javax.servlet.ServletContextListener
 {
   ActionManager manager = ActionManager.getActionManager();
   Thread thread = null;
   
   public void contextDestroyed(ServletContextEvent arg0) {
     System.out.println("application context destory ,destory the RTDT services");
     if (this.thread != null) {
       this.thread = null;
     }
   }
   
   public void contextInitialized(ServletContextEvent servletContextEvent)
   {
     try {
    	 WebSocketContainer wsContainer = ContainerProvider.getWebSocketContainer();
    	 System.out.println(servletContextEvent.getServletContext().getContextPath());
 		System.out.println(wsContainer);
 		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String uri = "ws://localhost:80//YaNanFrame";
		 		try {
		 			WebSocketServer wsServer = new WebSocketServer();
		 			SocketConfigurator sc = new SocketConfigurator();
		 			Session session = wsContainer.connectToServer(WebSocketServer.class, new URI(uri));
		 			session.addMessageHandler(wsServer);
		 		} catch (DeploymentException | IOException | URISyntaxException e) {
		 			e.printStackTrace();
		 		}
			}
		}).start();
 		
       System.out.println("application inited,try init RTDT");
       this.manager.init();
       Map<String, ActionEntity> map = this.manager.getActionPools();
       Iterator<String> iterator = map.keySet().iterator();
       while (iterator.hasNext()) {
         String name = (String)iterator.next();
         System.out.println("***********************************");
         System.out.println("action:" + name);
         System.out.println("info:" + (map.get(name)).toString());
       }
       Map<String, NotifyEntity> nm = NotifyManager.getManager().getNotifyEntitys();
       iterator = nm.keySet().iterator();
       while (iterator.hasNext()) {
         String name = (String)iterator.next();
         System.out.println("***********************************");
         System.out.println("notify:" + name);
         System.out.println("info:" + nm.get(name).toString());
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }


