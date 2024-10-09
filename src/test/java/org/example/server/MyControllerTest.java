package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.server.spell.Spell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    MyController myController;

    Map<String, Liner> serverLinerList;

    Map<String, Liner> tempLinerList;

    private MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();

    private WebSocketSession mockSession;

    @BeforeEach
    public void setUp(){
        tempLinerList = new HashMap<>();
        tempLinerList.put("top", new Liner("top"));
        tempLinerList.put("jg", new Liner("jg"));
        tempLinerList.put("mid", new Liner("mid"));
        tempLinerList.put("bot", new Liner("bot"));
        tempLinerList.put("sup", new Liner("sup"));

        Mockito.reset(myController);



        mockSession = Mockito.spy(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(true);
        assertTrue(mockSession.isOpen());
        myWebSocketHandler.handleTextMessage(mockSession, new TextMessage("hashValue"));
        assertEquals("hashValue", myWebSocketHandler.sessionHashValue.get(mockSession));
        assertEquals(1, myWebSocketHandler.sessionMap.get("hashValue").size());

        serverLinerList = MyWebSocketHandler.linerListMap.get("hashValue");
    }

    @AfterEach
    public void closeConnection() throws IOException {
        myWebSocketHandler.afterConnectionClosed(mockSession, null);
        assertEquals(0, myWebSocketHandler.sessionMap.get("hashValue").size());
    }

    @Test
    @DirtiesContext
    public void testGetJsonLineList() throws JsonProcessingException {
        assertEquals(
                """
                        [{"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},{"name":"bot","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},{"name":"mid","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},{"name":"jg","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},{"name":"sup","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}]"""
                , myController.getJsonLineList(serverLinerList)
        );
    }

    @Test
    @DirtiesContext
    void assertSendLinerStatusWillCallSetSpell() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 0
                            },
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        verify(mockServerLinerFlash, times(1)).setSpell(any(Spell.class));
    }

    @Test
    @DirtiesContext
    void testsSendLinerStatusJGFlaOff() throws Exception {
        tempLinerList.get("jg").getFlash().on();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 55
                            },
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());
        verify(myController, times(1)).sendLinerStatus(any(String.class));

        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));
        assertFalse(serverLinerList.get("jg").getFlash().isOn());
    }


    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOn1() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 0
                            },
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(serverLinerList.get("jg").getFlash().isOn());
        assertEquals(tempLinerList, serverLinerList);

        Liner resultLiner = new Liner("jg");
        assertEquals(resultLiner, serverLinerList.get("jg"));
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOnWithIonianBoots() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 0
                            },
                            "cosmicInsight": true,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(serverLinerList.get("jg").getFlash().isOn());

        Liner resultLiner = new Liner("jg");
        assertNotEquals(resultLiner, serverLinerList.get("jg"));
        resultLiner.setCosmicInsight(true);
        assertEquals(resultLiner, serverLinerList.get("jg"));
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOnWithCosmicInsight() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 0
                            },
                            "cosmicInsight": false,
                            "ionianBoots": true
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(serverLinerList.get("jg").getFlash().isOn());

        assertFalse(serverLinerList.get("jg").isCosmicInsight());
        assertTrue(serverLinerList.get("jg").isIonianBoots());

        Liner resultLiner = new Liner("jg");
        assertNotEquals(resultLiner, serverLinerList.get("jg"));
        resultLiner.setIonianBoots(true);
        assertEquals(resultLiner, serverLinerList.get("jg"));
    }
    @Test
    @DirtiesContext
    void testSendLinerStatusFlashOff() throws Exception {
        tempLinerList.get("jg").getFlash().setCoolTime(44);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 44
                            },
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        assertFalse(serverLinerList.get("jg").getFlash().isOn());
        assertEquals(tempLinerList, serverLinerList);
    }


    @Test
    @DirtiesContext
    void testSendLinerStatusFlashOffWithCosmicInsightsAndIonianBoots() throws Exception {
        tempLinerList.get("jg").getFlash().setCoolTime(44);
        tempLinerList.get("jg").setCosmicInsight(true);
        tempLinerList.get("jg").setIonianBoots(true);

        assertEquals(true, serverLinerList.get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "hash": "hashValue",
                        "data": {
                            "name": "jg",
                            "flash": {
                                "type":"flash",
                                "spellCoolTime": 300,
                                "coolTime": 44
                            },
                            "cosmicInsight": true,
                            "ionianBoots": true
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(serverLinerList.get("jg").getFlash().isOn());
        assertTrue(serverLinerList.get("jg").isCosmicInsight());
        assertTrue(serverLinerList.get("jg").isIonianBoots());
        assertEquals(tempLinerList, serverLinerList);
    }
}
