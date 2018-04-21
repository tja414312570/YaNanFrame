package com.YaNan.Demo.rtdt;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.YaNan.frame.RTDT.requestListener;

public class WebSocketDebug {
	public static void main(String[] args) {
		WebSocketContainer wsContainer = ContainerProvider.getWebSocketContainer();
		System.out.println(wsContainer);
		String uri = "ws://localhost:8080/log/log";
		try {
			Session session = wsContainer.connectToServer(requestListener.class, new URI(uri));
		} catch (DeploymentException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
