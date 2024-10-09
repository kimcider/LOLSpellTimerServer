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
    private HashMap<String, Liner> linerList = new HashMap<String, Liner>();
    private ObjectMapper mapper = new ObjectMapper();

    public String getJsonLineList(Map<String, Liner> list) throws JsonProcessingException {
        return mapper.writeValueAsString(list.values().stream().toList());
    }

    public MyController() {
        linerList.put("top", new Liner("top"));
        linerList.put("jg", new Liner("jg"));
        linerList.put("mid", new Liner("mid"));
        linerList.put("bot", new Liner("bot"));
        linerList.put("sup", new Liner("sup"));
    }



    @PostMapping("/sendLinerStatus")
    public void sendLinerStatus(@RequestBody String json) {
        try {
            JsonNode rootNode = mapper.readTree(json);

            String hash = rootNode.get("hash").asText();
            JsonNode dataNode = rootNode.get("data");
            String dataJson = mapper.writeValueAsString(dataNode);

            Liner clientLiner = mapper.readValue(dataJson, Liner.class);
            Liner serverLiner = linerList.get(clientLiner.getName());

            serverLiner.setLiner(clientLiner);

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessionMap.get(hash).stream().filter(WebSocketSession::isOpen).forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(getJsonLineList(linerList)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
