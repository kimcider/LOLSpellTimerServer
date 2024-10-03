package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Flash.class, name = "flash")
        //, @JsonSubTypes.Type(value = TP.class, name = "tp")
})
@Getter
@Setter
@JsonIgnoreProperties({"mapper"})
public abstract class Spell {
    protected int spellCoolTime = 300;
    protected int coolTime = 0;
    protected boolean cosmicInsight = false;
    protected boolean IonianBoots = false;

    public boolean isOn(){
        return coolTime == 0;
    }
    public void on() {
        coolTime = 0;
    }

    public void off() {
        if (coolTime == 0) {
            coolTime = spellCoolTime;
        }
    }
    @Override
    public boolean equals(Object obj) {
        Flash other = (Flash) obj;
        if (spellCoolTime == other.spellCoolTime && isOn() == other.isOn() && cosmicInsight == other.cosmicInsight && IonianBoots == other.IonianBoots) {
            return true;
        }
        return false;
    }

}
