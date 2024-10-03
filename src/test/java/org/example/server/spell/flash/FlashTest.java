package org.example.server.spell.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class FlashTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createFlashWithName() {
        Liner liner = new Liner("top");
        assertEquals(300, liner.getFlash().getSpellCoolTime());
        assertEquals(0, liner.getFlash().getCoolTime());
        assertTrue(liner.getFlash().isOn());
    }
}
