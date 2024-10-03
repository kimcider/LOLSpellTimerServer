package org.example.server.spell;

import org.example.server.Flash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpellTest {
    @Test
    public void createSpell(){
        Flash flash = new Flash();
        assertNotNull(flash);
        assertEquals(0, flash.getCoolTime());
        assertTrue(flash.isOn());
    }

    @Test
    public void createSpell2(){
        Flash flash = new Flash(50);
        assertNotNull(flash);
        assertEquals(50, flash.getCoolTime());
        assertFalse(flash.isOn());
    }
}
