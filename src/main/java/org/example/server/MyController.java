package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@RestController
@RequestMapping("/")
@Getter
public class MyController {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String wrapMethod(String method, String json) {
        ObjectNode wrappedData = mapper.createObjectNode();
        try{
            wrappedData.put("method", method);

            if(!json.isBlank()){
                JsonNode jsonNode = mapper.readTree(json);
                wrappedData.set("data", jsonNode);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return wrappedData.toString();
    }

    @PostMapping("/sendLinerStatus")
    public void sendLinerStatus(@RequestBody String json) {
        //System.out.println(json);
        try {
            JsonNode rootNode = mapper.readTree(json);

            String hash = rootNode.get("hash").asText();
            JsonNode dataNode = rootNode.get("data");
            //이게 null일 일이 뭐가있지?
            if (dataNode == null) {
                return;
            }
            String dataJson = mapper.writeValueAsString(dataNode);
            String wrappedJson = wrapMethod("sendLinerStatus", dataJson);

            MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();
            myWebSocketHandler.sessionMap.get(hash).stream().filter(WebSocketSession::isOpen).forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(wrappedJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    //얘를 여기다 두지 말고 handleTextMessage 에 넣어야할듯
//    @PostMapping("/getLinerStatus")
//    public String getLinerStatus(@RequestBody String hashValue){
//        MyWebSocketHandler.getInstance().sessionMap.get(hashValue).stream().filter(WebSocketSession::isOpen).forEach(session -> {
//            try {
//                session.sendMessage(new TextMessage(wrapMethod("getLinerStatus", "")));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    @PostMapping("/test")
    public String testTest(@RequestBody String json){
        return """
                [{"name":"top","flash":{"type":"flash","coolTime":60},"cosmicInsight":false,"ionianBoots":false},
                {"name":"bot","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"mid","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"jg","flash":{"type":"flash","coolTime":0},"cosmicInsight":true,"ionianBoots":false},
                {"name":"sup","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}]""";
    }
}
