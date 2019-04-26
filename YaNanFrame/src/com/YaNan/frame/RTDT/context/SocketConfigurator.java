 package com.YaNan.frame.RTDT.context;
 

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
 import javax.websocket.server.HandshakeRequest;
 import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.servlets.session.TokenManager;
 
 public class SocketConfigurator extends ServerEndpointConfig.Configurator
 {
   Logger log =LoggerFactory.getLogger(SocketConfigurator.class);
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


