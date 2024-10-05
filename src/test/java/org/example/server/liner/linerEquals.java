package org.example.server.liner;

import org.example.server.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class linerEquals {
    Liner l1;
    Liner l2;

    @BeforeEach
    public void setUp() {
        l1 = new Liner("top");
        l2 = new Liner("top");

        assertEquals(l1, l2);
    }

    @Test
    public void Diffrentname(){
        l1.setName("mid");
        assertNotEquals(l1, l2);
    }

    @Test
    public void DiffentFlashOn(){
        l1.getFlash().off();
        assertNotEquals(l1, l2);
    }

    @Test
    public void DifferentCosmicInsight(){
        l1.setCosmicInsight(true);
        assertNotEquals(l1, l2);
    }

    @Test
    public void DifferentIonianBoots(){
        l1.setIonianBoots(true);
        assertNotEquals(l1, l2);
    }

    @Test
    public void equalsReturnsFalseForNullObject(){
        l2 = null;
        assertFalse(l1.equals(l2));
    }
}
