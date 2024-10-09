package org.example.server.mywebsockethandler;

import org.example.server.Liner;
import org.example.server.MyWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class afterConnectionClosedTest {
    MyWebSocketHandler myHandler = MyWebSocketHandler.getInstance();
    WebSocketSession mockSession;

    Map<String, Liner> tempLinerList;
    @BeforeEach
    public void setUp(){
        tempLinerList = new HashMap<>();
        tempLinerList.put("top", new Liner("top"));
        tempLinerList.put("jg", new Liner("jg"));
        tempLinerList.put("mid", new Liner("mid"));
        tempLinerList.put("bot", new Liner("bot"));
        tempLinerList.put("sup", new Liner("sup"));

    }

    @Test
    @DirtiesContext
    public void checkLinerListMapAfterConnectionClosed_OneSession(){
        mockSession = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession, new TextMessage("hashValue"));
        myHandler.afterConnectionClosed(mockSession, null);

        assertNull(MyWebSocketHandler.linerListMap.get("hashValue"));
    }

    @Test
    @DirtiesContext
    public void checkLinerListMapAfterConnectionClosed_TwoSession(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue"));
        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue"));
        myHandler.afterConnectionClosed(mockSession1, null);

        assertNotNull(MyWebSocketHandler.linerListMap.get("hashValue"));
        assertEquals(tempLinerList, MyWebSocketHandler.linerListMap.get("hashValue"));
    }
}
