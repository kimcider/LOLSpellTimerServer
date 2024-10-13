package org.example.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private ObjectMapper mapper = new ObjectMapper();
    private static MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
    public HashMap<String, Set<WebSocketSession>> sessionMap;
    public HashMap<String, Set<WebSocketSession>> uninitializedSessionMap;
    public HashMap<Integer, String> sessionHashValue;


    private MyWebSocketHandler(){
        sessionMap = new HashMap<>();
        sessionHashValue = new HashMap<>();
        uninitializedSessionMap = new HashMap<>();
    }

    public static MyWebSocketHandler getInstance(){
        return myWebSocketHandler;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try{
            JsonNode rootNode = mapper.readTree(message.getPayload());

            String method = rootNode.get("method").asText();
            String hash = rootNode.get("hash").asText();
            if("open".equals(method)){
                sessionHashValue.put(session.hashCode(), hash);

                if(!sessionMap.containsKey(hash)){
                    sessionMap.put(hash, new HashSet<>());
                    sessionMap.get(hash).add(session);
                }else{
                    if(!uninitializedSessionMap.containsKey(hash)){
                        uninitializedSessionMap.put(hash, new HashSet<>());
                    }
                    uninitializedSessionMap.get(hash).add(session);
                }
            }else if("getLinerStatus".equals(method)){
                sessionMap.get(hash).stream().filter(WebSocketSession::isOpen).forEach(sess -> {
                    try {
                        sess.sendMessage(new TextMessage(MyController.wrapMethod("getLinerStatus", "")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }else if("getLinerStatusResponse".equals(method)){
                JsonNode dataNode = rootNode.get("data");
                String dataJson = mapper.writeValueAsString(dataNode);
                if(uninitializedSessionMap.size() == 0
                        || uninitializedSessionMap.get(hash) == null
                        || uninitializedSessionMap.get(hash).size() == 0){
                    return;
                }

                uninitializedSessionMap.get(hash).stream().filter(WebSocketSession::isOpen).forEach(sess -> {
                    try {
                        sess.sendMessage(new TextMessage(MyController.wrapMethod("getLinerStatusResponse", dataJson)));
                        sessionMap.get(hash).add(sess);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                uninitializedSessionMap.get(hash).clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String hashValue = sessionHashValue.get(session.hashCode());
        sessionMap.get(hashValue).remove(session);
        if(uninitializedSessionMap.containsKey(hashValue)){
            if(uninitializedSessionMap.get(hashValue).contains(session)){
                uninitializedSessionMap.get(hashValue).remove(session);
            }
        }

        sessionHashValue.remove(session.hashCode());
    }
}
