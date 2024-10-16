package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    MyController myController;
    private MyWebSocketHandler myWebSocketHandler = MyWebSocketHandler.getInstance();

    private WebSocketSession mockSession;

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


    @BeforeEach
    public void setUp() throws JsonProcessingException {
        Mockito.reset(myController);

        mockSession = Mockito.spy(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(true);
        assertTrue(mockSession.isOpen());
        myWebSocketHandler.handleTextMessage(mockSession, new TextMessage(wrapMethodJson("open", "hashValue", "")));
        assertEquals("hashValue", myWebSocketHandler.sessionHashValue.get(mockSession.hashCode()));
        assertEquals(1, myWebSocketHandler.sessionMap.get("hashValue").size());
    }

    @AfterEach
    public void closeConnection() throws IOException {
        myWebSocketHandler.uninitializedSessionMap.clear();
        myWebSocketHandler.sessionMap.clear();
        myWebSocketHandler.sessionHashValue.clear();
    }

    @Test
    @DirtiesContext
    void assertSendLinerStatusWillCallSetSpell() throws Exception {
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
    }

    @Test
    @DirtiesContext
    public void sendLinerStatusWithEmptyJson() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("{}")
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());


        verify(mockSession, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    @DirtiesContext
    public void sendLinerStatusWithMissingHash() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                                {
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

        verify(mockSession, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    @DirtiesContext
    public void sendLinerStatusWithMissingData() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                                {
                                    "hash": "hashValue"
                                }
                            """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(mockSession, never()).sendMessage(any(TextMessage.class));
    }
}
