package org.example.server.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.Liner;
import org.example.server.MyController;
import org.example.server.MyWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.TextMessage;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
public class LinerTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    MyController myController;

    Map<String, Liner> serverLinerList;

    @BeforeEach
    public void setUp(){
        MyWebSocketHandler.getInstance().handleTextMessage(null, new TextMessage("hashValue"));
        Mockito.reset(myController);
        serverLinerList = MyWebSocketHandler.linerListMap.get("hashValue");
    }

    @Test
    public void createLiner(){
        Liner liner = new Liner();
        assertNull(liner.getName());
        assertNotNull(liner.getFlash());
    }

    @Test
    public void createLiner2(){
        Liner liner = new Liner("top");
        assertNotNull(liner.getName());
        assertNotNull(liner.getFlash());
        assertEquals("top", liner.getName());
    }


    @Test
    public void testLinerToJson() throws JsonProcessingException {
        Liner liner = new Liner("jg");
        String json = mapper.writeValueAsString(liner);

        assertEquals("""
                {"name":"jg","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}""", json);
    }

    @Test
    @DirtiesContext
    public void testJsonToLinerListWithCoolTime() throws JsonProcessingException {
        serverLinerList.get("jg").getFlash().off();
        serverLinerList.get("mid").getFlash().off();
        serverLinerList.get("sup").getFlash().setCoolTime(100);

        String json = """
                  [{"name":"top","flash":{"type":"flash", "coolTime":0}},
                  {"name":"jg","flash":{"type":"flash", "coolTime":300}},
                  {"name":"mid","flash":{"type":"flash", "coolTime":300}},
                  {"name":"bot","flash":{"type":"flash", "coolTime":0}},
                  {"name":"sup","flash":{"type":"flash", "coolTime":100}}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
        for(Liner liner : liners){
            assertEquals(liner, serverLinerList.get(liner.getName()));
        }
    }

    @Test
    @DirtiesContext
    public void testJsonToLinerListWithIonianBootsAndCosmicInsights() throws JsonProcessingException {
        serverLinerList.get("jg").setIonianBoots(true);
        serverLinerList.get("mid").setCosmicInsight(true);;

        String json = """
                  [{"name":"top","flash":{"type":"flash", "coolTime":0},"ionianBoots":false,"cosmicInsight":false},
                  {"name":"jg","flash":{"type":"flash", "coolTime":0},"ionianBoots":true,"cosmicInsight":false},
                  {"name":"mid","flash":{"type":"flash", "coolTime":0},"ionianBoots":false,"cosmicInsight":true},
                  {"name":"bot","flash":{"type":"flash", "coolTime":0},"ionianBoots":false,"cosmicInsight":false},
                  {"name":"sup","flash":{"type":"flash", "coolTime":0},"ionianBoots":false,"cosmicInsight":false}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
        for(Liner liner : liners){
            assertEquals(liner, serverLinerList.get(liner.getName()));
        }
    }
}
