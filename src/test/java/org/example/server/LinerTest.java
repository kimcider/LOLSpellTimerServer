package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
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
        Mockito.reset(myController);
        serverLinerList = myController.getLinerList();
    }

    @Test
    public void createLiner(){
        Liner liner = new Liner();
        assertNull(liner.getName());
        assertNull(liner.getFlash());
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
                {"name":"jg","flash":{"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}}""", json);
    }

    @Test
    @DirtiesContext
    public void testJsonToLinerListWithCoolTime() throws JsonProcessingException {
        myController.getLinerList().get("jg").getFlash().off();
        myController.getLinerList().get("mid").getFlash().off();
        myController.getLinerList().get("sup").getFlash().setCoolTime(100);

        String json = """
                  [{"name":"top","flash":{"type":"flash", "coolTime":0}},
                  {"name":"jg","flash":{"type":"flash", "coolTime":300}},
                  {"name":"mid","flash":{"type":"flash", "coolTime":300}},
                  {"name":"bot","flash":{"type":"flash", "coolTime":0}},
                  {"name":"sup","flash":{"type":"flash", "coolTime":100}}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
        for(Liner liner : liners){
            assertEquals(liner, myController.getLinerList().get(liner.getName()));
        }

    }


    @Test
    @DirtiesContext
    public void testJsonToLinerListWithCosmicInsightsAndIonianBoots() throws JsonProcessingException {
        myController.getLinerList().get("jg").getFlash().setCosmicInsight(true);
        myController.getLinerList().get("mid").getFlash().setIonianBoots(true);
        myController.getLinerList().get("sup").getFlash().setCosmicInsight(true);
        myController.getLinerList().get("sup").getFlash().setIonianBoots(true);

        String json = """
                  [{"name":"top","flash":{"type":"flash", "coolTime":0, "cosmicInsight":false, "ionianBoots":false}},
                  {"name":"jg","flash":{"type":"flash", "coolTime":0, "cosmicInsight":true, "ionianBoots":false}},
                  {"name":"mid","flash":{"type":"flash", "coolTime":0, "cosmicInsight":false, "ionianBoots":true}},
                  {"name":"bot","flash":{"type":"flash", "coolTime":0, "cosmicInsight":false, "ionianBoots":false}},
                  {"name":"sup","flash":{"type":"flash", "coolTime":0, "cosmicInsight":true, "ionianBoots":true}}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
        for(Liner liner : liners){
            assertEquals(liner, myController.getLinerList().get(liner.getName()));
        }

    }

    @Test
    @DirtiesContext
    public void testJsonToLinerListWithCoolTimeAndCosmicInsightsAndIonianBoots() throws JsonProcessingException{
        myController.getLinerList().get("jg").getFlash().off();
        myController.getLinerList().get("mid").getFlash().off();
        myController.getLinerList().get("sup").getFlash().setCoolTime(100);

        myController.getLinerList().get("jg").getFlash().setCosmicInsight(true);
        myController.getLinerList().get("mid").getFlash().setIonianBoots(true);
        myController.getLinerList().get("sup").getFlash().setCosmicInsight(true);
        myController.getLinerList().get("sup").getFlash().setIonianBoots(true);

        String json = """
                  [{"name":"top","flash":{"type":"flash", "coolTime":0, "cosmicInsight":false, "ionianBoots":false}},
                  {"name":"jg","flash":{"type":"flash", "coolTime":300, "cosmicInsight":true, "ionianBoots":false}},
                  {"name":"mid","flash":{"type":"flash", "coolTime":300, "cosmicInsight":false, "ionianBoots":true}},
                  {"name":"bot","flash":{"type":"flash", "coolTime":0, "cosmicInsight":false, "ionianBoots":false}},
                  {"name":"sup","flash":{"type":"flash", "coolTime":100, "cosmicInsight":true, "ionianBoots":true}}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
        for(Liner liner : liners){
            assertEquals(liner, myController.getLinerList().get(liner.getName()));
        }
    }

    @Test
    public void equals() {
        Liner l1 = new Liner("top");
        Liner l2 = new Liner("top");

        assertEquals(l1, l2);

        l1.setName("jg");
        assertNotEquals(l1, l2);

        l2.setName("jg");
        assertEquals(l1, l2);

        l1.getFlash().off();
        assertNotEquals(l1, l2);
        l2.getFlash().off();
        assertEquals(l1, l2);
    }

}
