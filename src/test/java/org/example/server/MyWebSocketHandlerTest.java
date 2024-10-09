package org.example.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MyWebSocketHandlerTest {
    MyWebSocketHandler myHandler = MyWebSocketHandler.getInstance();
    WebSocketSession mockSession;

    @Test
    @DirtiesContext
    public void afterConnectionClosedWithNullSession() {
        assertThrows(NullPointerException.class, () -> {
            myHandler.afterConnectionClosed(null, null);
        });
    }

    @Test
    @DirtiesContext
    public void addSessionToMap(){
        mockSession = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession, new TextMessage("hashValue"));
        assertEquals("hashValue", myHandler.sessionHashValue.get(mockSession));
        assertEquals(1, myHandler.sessionMap.get("hashValue").size());
        myHandler.afterConnectionClosed(mockSession, null);
        assertEquals(0, myHandler.sessionMap.get("hashValue").size());
    }

    @Test
    @DirtiesContext
    public void addTwoSessionToMap_SameHashValue(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue"));
        assertEquals("hashValue", myHandler.sessionHashValue.get(mockSession1));
        assertEquals(1, myHandler.sessionMap.get("hashValue").size());

        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue"));
        assertEquals("hashValue", myHandler.sessionHashValue.get(mockSession2));
        assertEquals(2, myHandler.sessionMap.get("hashValue").size());

        myHandler.afterConnectionClosed(mockSession1, null);
        assertEquals(1, myHandler.sessionMap.get("hashValue").size());
        myHandler.afterConnectionClosed(mockSession2, null);
        assertEquals(0, myHandler.sessionMap.get("hashValue").size());
    }

    @Test
    @DirtiesContext
    public void addTwoSessionToMap_DiffHashValue(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue1"));
        assertEquals("hashValue1", myHandler.sessionHashValue.get(mockSession1));
        assertEquals(1, myHandler.sessionMap.get("hashValue1").size());

        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue2"));
        assertEquals("hashValue2", myHandler.sessionHashValue.get(mockSession2));
        assertEquals(1, myHandler.sessionMap.get("hashValue2").size());

        myHandler.afterConnectionClosed(mockSession1, null);
        assertEquals(0, myHandler.sessionMap.get("hashValue1").size());
        myHandler.afterConnectionClosed(mockSession2, null);
        assertEquals(0, myHandler.sessionMap.get("hashValue2").size());
    }
}
