package org.example.server.mywebsockethandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.server.MyController;
import org.example.server.MyWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class getLinerStatusResponseTest {
    private static ObjectMapper mapper = new ObjectMapper();
    MyWebSocketHandler myHandler = Mockito.spy(MyWebSocketHandler.getInstance());

    private String wrapMethodJson(String method, String hash, String json) throws JsonProcessingException {
        ObjectNode wrappedData = mapper.createObjectNode();
        wrappedData.put("method", method);
        wrappedData.put("hash", hash);

        if (!json.isBlank()) {
            JsonNode jsonNode = mapper.readTree(json);
            wrappedData.set("data", jsonNode);
        }

        return wrappedData.toString();
    }

    @AfterEach
    public void tearDown(){
        myHandler.uninitializedSessionMap.clear();
        myHandler.sessionMap.clear();
        myHandler.sessionHashValue.clear();
    }

    @Test
    @DirtiesContext
    public void hasOneSession() throws IOException {
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);

        when(mockSession2.isOpen()).thenReturn(true);

        myHandler.handleTextMessage(mockSession1, new TextMessage(wrapMethodJson("open", "hashValue", "")));
        myHandler.handleTextMessage(mockSession2, new TextMessage(wrapMethodJson("open", "hashValue", "")));
        assertEquals(1, myHandler.sessionMap.get("hashValue").size());
        assertEquals(1, myHandler.uninitializedSessionMap.get("hashValue").size());

        myHandler.handleTextMessage(mockSession1, new TextMessage(wrapMethodJson("getLinerStatusResponse", "hashValue", """
                {"data": "data"}""")));

        String wrappedJson = MyController.wrapMethod("getLinerStatusResponse", """
                {"data": "data"}""");
        verify(mockSession2, times(1)).sendMessage(new TextMessage(wrappedJson));

        assertEquals(2, myHandler.sessionMap.get("hashValue").size());
        assertEquals(0, myHandler.uninitializedSessionMap.get("hashValue").size());
    }
}
