package org.example.server.mywebsockethandler;

import org.example.server.Liner;
import org.example.server.MyWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class addSessionCheckLinerListMapTest {
    MyWebSocketHandler myHandler = MyWebSocketHandler.getInstance();

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

    @AfterEach
    public void tearDown(){

    }

    @Test
    @DirtiesContext
    public void addSessionCheckLinerListMap(){
        WebSocketSession mockSession = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession, new TextMessage("hashValue"));

        assertEquals(tempLinerList, MyWebSocketHandler.linerListMap.get("hashValue"));
    }

    @Test
    @DirtiesContext
    public void addSessionCheckLinerListMap_SameHashValue(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue"));
        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue"));

        assertEquals(MyWebSocketHandler.linerListMap.get("hashValue").hashCode(), MyWebSocketHandler.linerListMap.get("hashValue").hashCode());
    }

    @Test
    @DirtiesContext
    public void addSessionCheckLinerListMap_DiffHashValue(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue1"));
        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue2"));

        assertNotEquals(MyWebSocketHandler.linerListMap.get("hashValue1").hashCode(), MyWebSocketHandler.linerListMap.get("hashValue2").hashCode());
    }

    @Test
    @DirtiesContext
    public void addSessionCheckLinerListMap_SameHashSessionInsertionWillNotChangeLinerStatus(){
        WebSocketSession mockSession1 = Mockito.mock(WebSocketSession.class);
        WebSocketSession mockSession2 = Mockito.mock(WebSocketSession.class);
        myHandler.handleTextMessage(mockSession1, new TextMessage("hashValue"));

        Map<String, Liner> linerList = MyWebSocketHandler.linerListMap.get("hashValue");
        linerList.get("top").setIonianBoots(true);

        myHandler.handleTextMessage(mockSession2, new TextMessage("hashValue"));

        assertTrue(MyWebSocketHandler.linerListMap.get("hashValue").get("top").isIonianBoots());
    }
}
