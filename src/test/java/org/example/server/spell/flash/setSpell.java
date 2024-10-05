package org.example.server.spell.flash;

import org.example.server.spell.Flash;
import org.example.server.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class setSpell {
    Spell spellTarget ;
    Spell spellModel ;
    @BeforeEach
    void setUp() {
        spellTarget = new Flash();
        spellModel = new Flash();
    }

    @Test
    public void setSpell_AssertSetSameSpellWillNotChangeSpell(){
        Spell spell = new Flash();
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);

        spellTarget.setSpell(spellModel);
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);
    }

    @Test
    public void setSpell_changeCollTime(){
        spellModel.setCoolTime(50);

        spellTarget.setSpell(spellModel);

        assertEquals(50, spellTarget.getCoolTime());
    }

    @Test
    public void setSpell_SpyFunctionCall(){
        Spell mockSpellTarget = Mockito.spy(spellTarget);
        Spell mockSpellModel = Mockito.spy(spellModel);

        mockSpellTarget.setSpell(mockSpellModel);

        verify(mockSpellTarget, times(1)).setSpell(mockSpellModel);
        verify(mockSpellModel, times(1)).getCoolTime();
    }
}
