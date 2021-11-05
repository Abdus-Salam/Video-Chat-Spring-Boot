package com.videoCall;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
 
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
	

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/socket")
          .setAllowedOrigins("*");
    }
    
	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	 
	@OnOpen
	public void whenOpening(Session session) throws IOException, EncodeException {
		System.out.println("Open!");
		// Add websocket session to a global set to use in OnMessage.
		sessions.add(session);
	}
 
	@OnMessage
	public void process(String data, Session session) throws IOException {
		System.out.println("Got signal - " + data);
		/*
		 * When signal is received, send it to other participants other than self. In
		 * real world, signal should be sent to only participant's who belong to
		 * particular video conference.
		 */
		for (Session sess : sessions) {
			if (!sess.equals(session)) {
				sess.getBasicRemote().sendText(data);
			}
		}
	}
 
	@OnClose
	public void whenClosing(Session session) {
		System.out.println("Close!");
		sessions.remove(session);
	}
}