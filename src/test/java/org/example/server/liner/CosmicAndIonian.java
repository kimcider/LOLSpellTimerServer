package org.example.server.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.Liner;
import org.example.server.MyController;
import org.example.server.spell.Flash;
import org.example.server.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class CosmicAndIonian {
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
    public void jsonToLinerWithCosmicInsight() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":true,"ionianBoots":false}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top");
        expectedLiner.setCosmicInsight(true);
        assertEquals(expectedLiner, liner);
    }

    @Test
    public void jsonToLinerWithIonianBoots() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":true}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top");
        expectedLiner.setIonianBoots(true);
        assertEquals(expectedLiner, liner);
    }

    @Test
    public void jsonToLinerWithIonianBootsAndCosmicInsight() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":true,"ionianBoots":true}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top");
        expectedLiner.setIonianBoots(true);
        expectedLiner.setCosmicInsight(true);
        assertEquals(expectedLiner, liner);
    }

}
