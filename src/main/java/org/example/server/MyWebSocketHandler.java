package org.example.server;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private static MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();

    private MyWebSocketHandler(){

    }

    public static MyWebSocketHandler getInstance(){
        return myWebSocketHandler;
    }

    public HashMap<String, Set<WebSocketSession>> sessionMap = new HashMap<>();
    public HashMap<Integer, String> sessionHashValue = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String hashValue = message.getPayload();
        sessionHashValue.put(session.hashCode(), hashValue);

        if(!sessionMap.containsKey(hashValue)){
            sessionMap.put(hashValue, new HashSet<>());
        }
        sessionMap.get(hashValue).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String hashValue = sessionHashValue.get(session.hashCode());
        sessionMap.get(hashValue).remove(session);

        sessionHashValue.remove(session.hashCode());
    }
}
