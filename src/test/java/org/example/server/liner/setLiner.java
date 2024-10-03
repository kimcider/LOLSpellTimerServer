package org.example.server.liner;

import org.example.server.Liner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class setLiner {
    @Test
    void setLinerUpdatesFlash() {
        Liner liner1 = new Liner();
        assertTrue(liner1.getFlash().isOn());

        Liner liner2 = new Liner();
        liner2.getFlash().off();

        liner1.setLiner(liner2);

        assertFalse(liner1.getFlash().isOn());
    }

    @Test
    void setLinerUpdatesCosmicInsight() {
        Liner liner1 = new Liner();
        Liner liner2 = new Liner();
        liner2.setCosmicInsight(true);

        liner1.setLiner(liner2);

        assertTrue(liner1.isCosmicInsight());
    }

    @Test
    void setLinerUpdatesIonianBoots() {
        Liner liner1 = new Liner();
        Liner liner2 = new Liner();
        liner2.setIonianBoots(true);

        liner1.setLiner(liner2);

        assertTrue(liner1.isIonianBoots());
    }

    @Test
    void setLinerWithNullModelDoesNotThrowException() {
        Liner liner1 = new Liner();

        assertDoesNotThrow(() -> liner1.setLiner(null));
    }
}
