 package com.YaNan.frame.RTDT.context;
 

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
 import javax.websocket.server.HandshakeRequest;
 import javax.websocket.server.ServerEndpointConfig;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.servlets.session.TokenManager;
 
 public class SocketConfigurator extends ServerEndpointConfig.Configurator
 {
   Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class, DefaultLog.class, SocketConfigurator.class);
   public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response)
   {
    String tokenId = (String) ((HttpSession)request.getHttpSession()).getAttribute(TokenManager.getTokenMark());
     if(tokenId!=null){
    	 config.getUserProperties().put("TOKENID", tokenId);
     }else{
    	 log.debug("could not get token , please check wether has TokenFilter!");
     }
   }
 }


