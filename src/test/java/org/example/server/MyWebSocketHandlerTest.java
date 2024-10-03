package org.example.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyWebSocketHandlerTest {
    MyWebSocketHandler myHandler = MyWebSocketHandler.getInstance();

    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void closeConnection(){
        myHandler.sessions.clear();
        assertEquals(0, myHandler.sessions.size());
    }

    @Test
    public void addSession(){
        WebSocketSession mockSession = Mockito.mock(WebSocketSession.class);
        myHandler.afterConnectionEstablished(mockSession);
        assertEquals(1, myHandler.sessions.size());
    }

    @Test
    public void removeSession() throws Exception {
        WebSocketSession mockSession = Mockito.mock(WebSocketSession.class);
        myHandler.afterConnectionEstablished(mockSession);
        assertEquals(1, myHandler.sessions.size());
        myHandler.afterConnectionClosed(mockSession, CloseStatus.NORMAL);
        assertEquals(0, myHandler.sessions.size());
    }
}
