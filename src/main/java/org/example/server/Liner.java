package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.example.server.spell.Flash;
import org.example.server.spell.Spell;

@Getter
@Setter
@JsonIgnoreProperties({"spell2"})
public class Liner {
    private String name;
    private Spell flash;
    private Spell spell2;

    private boolean cosmicInsight = false;
    private boolean ionianBoots = false;

    // 기본 생성자 필요
    public Liner() {
        flash = new Flash(); // 기본값 설정
    }

    public Liner(String name) {
        this.name = name;
        this.flash = new Flash(); // 기본값 설정
    }


    public void setLiner(Liner model){
        if(model == null){
            return;
        }
        flash.setSpell(model.getFlash());
        cosmicInsight = model.isCosmicInsight();
        ionianBoots = model.isIonianBoots();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        Liner other = (Liner) obj;
        if (name.equals(other.name) && flash.equals(other.flash)  && cosmicInsight == other.cosmicInsight && ionianBoots == other.ionianBoots) {
            return true;
        }
        return false;
    }
}