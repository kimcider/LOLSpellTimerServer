package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
                        [{"name":"top","flash":{"coolTime":0}},{"name":"bot","flash":{"coolTime":0}},{"name":"mid","flash":{"coolTime":0}},{"name":"jg","flash":{"coolTime":0}},{"name":"sup","flash":{"coolTime":0}}]"""
                , myController.getJsonLineList(serverLinerList)
        );
    }


    @Test
    @DirtiesContext
    void testsSendLinerStatusJGFlagOn() throws Exception {
        tempLinerList.get("jg").getFlash().on();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                      "name": "jg",
                      "flash":{"coolTime":0}
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());
        verify(myController, times(1)).sendLinerStatus(any(String.class));
        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());

        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));

        assertEquals(tempLinerList, myController.getLinerList());
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlagOff() throws Exception {
        tempLinerList.get("jg").getFlash().off();

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                      "name": "jg",
                      "flash":{"coolTime":300}
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        assertEquals(false, myController.getLinerList().get("jg").getFlash().isOn());

        assertEquals(tempLinerList, myController.getLinerList());
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusJGFlagOff2() throws Exception {


        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                      "name": "jg",
                      "flash":{"coolTime":200}
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        assertEquals(false, myController.getLinerList().get("jg").getFlash().isOn());

        tempLinerList.get("jg").getFlash().off();
        assertNotEquals(tempLinerList, myController.getLinerList());
        tempLinerList.get("jg").getFlash().setCoolTime(200);
        assertEquals(tempLinerList, myController.getLinerList());
    }

    @Test
    @DirtiesContext
    void testSendLinerStatusFlashOn() throws Exception {
        tempLinerList.get("jg").getFlash().off();

        assertEquals(true, myController.getLinerList().get("jg").getFlash().isOn());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/sendLinerStatus")
                        .content("""
                    {
                      "name": "jg",
                      "flash":{"coolTime":300}
                    }
                """)
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        verify(myController, times(1)).sendLinerStatus(any(String.class));

        assertEquals(false, myController.getLinerList().get("jg").getFlash().isOn());

        assertEquals(tempLinerList, myController.getLinerList());
    }


//    @Test
//    @DirtiesContext
//    void testAllLinerStatus() throws Exception {
//        String json = """
//                        [{"name":"top","flash":{"coolTime":0}},
//                          {"name":"jg","flash":{"coolTime":300}},
//                          {"name":"mid","flash":{"coolTime":285}},
//                          {"name":"bot","flash":{"coolTime":0}},
//                          {"name":"sup","flash":{"coolTime":100}}]""";
//        mockMvc.perform(
//                MockMvcRequestBuilders.post("/allLinerStatus")
//                        .content(json)
//                        .contentType("application/json")
//        ).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(json));
//        verify(myController, times(1)).allLinerStatus(json);
//    }
}
