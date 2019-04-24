package com.YaNan.frame.RTDT.context.init;

import com.YaNan.frame.RTDT.WebSocketListener;
import com.YaNan.frame.RTDT.context.ActionManager;
import com.YaNan.frame.RTDT.context.NotifyManager;
import com.YaNan.frame.RTDT.context.SocketConfigurator;
import com.YaNan.frame.RTDT.entity.ActionEntity;
import com.YaNan.frame.RTDT.entity.NotifyEntity;
import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

@Register
public class RTDTContextInit implements javax.servlet.ServletContextListener {
	final String path = "/";
	ActionManager manager = ActionManager.getActionManager();
	Thread thread = null;
	private Log log = PlugsFactory.getPlugsInstanceWithDefault(Log.class,DefaultLog.class, RTDTContextInit.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		log.debug("Destory the RTDT services");
		if (this.thread != null) {
			thread.interrupt();
			this.thread = null;
		}
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			log.debug("application inited,try init RTDT");
			// get tomcat websocket container from servletContextEvent
			ServerContainer sc = (ServerContainer) servletContextEvent.getServletContext()
					.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
			Configurator configurator = SocketConfigurator.class.newInstance();
			ArrayList<Class<? extends Decoder>> decoders = new ArrayList<Class<? extends Decoder>>();
			List<Class<? extends Encoder>> encoders = new ArrayList<Class<? extends Encoder>>();
			log.debug("path:"+path+";listener:"+WebSocketListener.class.getName()+";decoders:"+decoders+";encoders:"+encoders);
			// create a ServerEndpointConfig
			ServerEndpointConfig sec = ServerEndpointConfig.Builder.create(WebSocketListener.class, path)
					.decoders(decoders).encoders(encoders).subprotocols(new ArrayList<String>())
					.configurator(configurator).build();
			sc.addEndpoint(sec);
			this.manager.init();
			Map<String, ActionEntity> map = this.manager.getActionPools();
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				log.debug("action:" + name);
				log.debug("info:" + (map.get(name)).toString());
			}
			Map<String, NotifyEntity> nm = NotifyManager.getManager().getNotifyEntitys();
			iterator = nm.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				log.debug("notify:" + name);
				log.debug("info:" + nm.get(name).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
