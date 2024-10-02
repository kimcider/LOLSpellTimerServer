package org.example.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlashTest {

    @Test
    public void createFlash(){
        Flash flash = new Flash();
        assertNotNull(flash);
        assertEquals(0, flash.coolTime);
        assertTrue(flash.isOn());
    }

    @Test
    public void createFlash2(){
        Flash flash = new Flash(50);
        assertNotNull(flash);
        assertEquals(50, flash.coolTime);
        assertFalse(flash.isOn());
    }

    @Test
    public void equals() {
        Flash f1 = new Flash();
        Flash f2 = new Flash();
        assertEquals(f1, f2);

        f1.off();
        assertNotEquals(f1, f2);

        f2.off();
        assertEquals(f1, f2);

        f1.setCoolTime(150);
        assertNotEquals(f1, f2);

        f2.setCoolTime(150);
        assertEquals(f1, f2);
    }

    @Test
    public void testOnOff() {
        Flash flash = new Flash();
        flash.off();
        assertFalse(flash.isOn());
        assertEquals(flash.getFlashCoolTime(), flash.coolTime);
        flash.on();
        assertTrue(flash.isOn());
        assertEquals(0, flash.coolTime);
    }
}
