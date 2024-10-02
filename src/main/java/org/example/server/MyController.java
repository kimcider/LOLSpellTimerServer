package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private Map<String, Liner> linerList = new HashMap<String, Liner>();
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

    // 때가 되면 지우기
    @PostMapping("/useFlash")
    public void useFlash(@RequestBody String json) {
        try {
            Liner liner = mapper.readValue(json, Liner.class);
//            linerList.get(liner.getName()).getFlash().setOn(liner.getFlash().isOn());
            linerList.get(liner.getName()).getFlash().setCoolTime(liner.getFlash().getCoolTime());

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessions.stream().filter(WebSocketSession::isOpen).forEach(session -> {
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

    // useFlash와 완전히 같은 함수
    @PostMapping("/sendLinerStatus")
    public void sendLinerStatus(@RequestBody String json) {
        try {
            Liner clientLiner = mapper.readValue(json, Liner.class);
            Liner serverLiner = linerList.get(clientLiner.getName());

            serverLiner.getFlash().setCoolTime(clientLiner.getFlash().getCoolTime());
            serverLiner.getFlash().setFlashCoolTime(clientLiner.getFlash().getFlashCoolTime());

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessions.stream().filter(WebSocketSession::isOpen).forEach(session -> {
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
