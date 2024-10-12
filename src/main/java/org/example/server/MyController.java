package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@Getter
public class MyController {
    private ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/sendLinerStatus")
    public void sendLinerStatus(@RequestBody String json) {
        //System.out.println(json);
        try {
            JsonNode rootNode = mapper.readTree(json);

            String hash = rootNode.get("hash").asText();
            JsonNode dataNode = rootNode.get("data");
            if (dataNode == null) {
                return;
            }
            String dataJson = mapper.writeValueAsString(dataNode);

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessionMap.get(hash).stream().filter(WebSocketSession::isOpen).forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(dataJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
