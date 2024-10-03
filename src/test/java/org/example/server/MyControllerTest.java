package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.server.spell.Flash;
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
        serverLinerList = myController.getLinerList();

        mockSession = Mockito.spy(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(true);
        assertTrue(mockSession.isOpen());
        myWebSocketHandler.afterConnectionEstablished(mockSession);
        assertEquals(1, myWebSocketHandler.sessions.size());
    }

    @AfterEach
    public void closeConnection() throws IOException {
        myWebSocketHandler.sessions.clear();
        assertEquals(0, myWebSocketHandler.sessions.size());
    }

    @Test
    @DirtiesContext
    public void testGetJsonLineList() throws JsonProcessingException {
        assertEquals(
                """
                        [{"name":"top","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}},{"name":"bot","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}},{"name":"mid","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}},{"name":"jg","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}},{"name":"sup","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}}]"""
                , myController.getJsonLineList(serverLinerList)
        );
    }

    @Test
    @DirtiesContext
    void assertSendLinerStatusWillCallSetSpell() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 0,
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
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 55,
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());
        verify(myController, times(1)).sendLinerStatus(any(String.class));

        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));
        assertFalse(myController.getLinerList().get("jg").getFlash().isOn());
    }


    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOn1() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 0,
                            "cosmicInsight": false,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(myController.getLinerList().get("jg").getFlash().isOn());
        assertEquals(tempLinerList, myController.getLinerList());

        Liner resultLiner = new Liner("jg");
        assertEquals(resultLiner, myController.getLinerList().get("jg"));
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOnWithIonianBoots() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 0,
                            "cosmicInsight": true,
                            "ionianBoots": false
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(myController.getLinerList().get("jg").getFlash().isOn());

        Liner resultLiner = new Liner("jg");
        assertNotEquals(resultLiner, myController.getLinerList().get("jg"));
        resultLiner.getFlash().setCosmicInsight(true);
        assertEquals(resultLiner, myController.getLinerList().get("jg"));
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlashOnWithCosmicInsight() throws Exception {
        Spell mockServerLinerFlash = Mockito.spy(serverLinerList.get("jg").getFlash());
        serverLinerList.get("jg").setFlash(mockServerLinerFlash);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 0,
                            "cosmicInsight": false,
                            "ionianBoots": true
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(myController.getLinerList().get("jg").getFlash().isOn());

        Liner resultLiner = new Liner("jg");
        assertNotEquals(resultLiner, myController.getLinerList().get("jg"));
        resultLiner.getFlash().setIonianBoots(true);
        assertEquals(resultLiner, myController.getLinerList().get("jg"));
    }
    @Test
    @DirtiesContext
    void testSendLinerStatusFlashOff() throws Exception {
        tempLinerList.get("jg").getFlash().setCoolTime(44);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 44,
                            "cosmicInsight": false,
                            "ionianBoots": true
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        assertFalse(myController.getLinerList().get("jg").getFlash().isOn());
    }


    @Test
    @DirtiesContext
    void testSendLinerStatusFlashOffWithCosmicInsightsAndIonianBoots() throws Exception {
        tempLinerList.get("jg").getFlash().setCoolTime(44);

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                        "name": "jg",
                        "flash": {
                            "type":"flash",
                            "spellCoolTime": 300,
                            "coolTime": 44,
                            "cosmicInsight": true,
                            "ionianBoots": true
                        }
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(myController.getLinerList().get("jg").getFlash().isOn());
        assertTrue(myController.getLinerList().get("jg").getFlash().isCosmicInsight());
        assertTrue(myController.getLinerList().get("jg").getFlash().isIonianBoots());
    }
}
