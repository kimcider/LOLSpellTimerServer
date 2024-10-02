package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MyController {
    private Map<String, Liner> linerList = new HashMap<String, Liner>();
    private ObjectMapper mapper = new ObjectMapper();

    public String getJsonLineList() throws JsonProcessingException {
        return mapper.writeValueAsString(linerList.values().stream().toList());
    }

    public MyController() {
        linerList.put("top", new Liner("top"));
        linerList.put("jg", new Liner("jg"));
        linerList.put("mid", new Liner("mid"));
        linerList.put("bot", new Liner("bot"));
        linerList.put("sup", new Liner("sup"));
    }

    @PostMapping("/useFlash")
    public void useFlash(@RequestBody String json) {
        try {
            Liner liner = mapper.readValue(json, Liner.class);
            System.out.println(liner);
            System.out.println(json);
            linerList.get(liner.name).flash.on = liner.flash.on;

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessions.stream().filter(WebSocketSession::isOpen).forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(getJsonLineList()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
