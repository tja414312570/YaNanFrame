 package com.YaNan.frame.RTDT.context;
 
 import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
 import javax.websocket.server.HandshakeRequest;
 import javax.websocket.server.ServerEndpointConfig;

import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.TokenManager;
 
 public class SocketConfigurator extends ServerEndpointConfig.Configurator
 {
   public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response)
   {
	 String tokenId = null;
     List<String> header = request.getHeaders().get("cookie");
     if(header!=null){
    	 Iterator<String> iterator = header.iterator();
         while(iterator.hasNext()){
        	 String content = iterator.next();
        	 if(content.indexOf(TokenManager.getTokenMark())!=-1){
        		 tokenId= content.substring(content.indexOf(TokenManager.getTokenMark())+TokenManager.getTokenMark().length()+1);
        		 continue;
        	 }
         }
     }
     if(tokenId==null)
    	 tokenId = (String) ((HttpSession)request.getHttpSession()).getAttribute(TokenManager.getTokenMark());
     if(tokenId!=null)
    	 config.getUserProperties().put("TOKENID", tokenId);
   }
 }


