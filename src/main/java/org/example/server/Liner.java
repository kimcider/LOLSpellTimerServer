package org.example.server;

import lombok.Getter;
import lombok.Setter;
import org.example.server.spell.Flash;
import org.example.server.spell.Spell;

@Getter
@Setter
public class Liner {
    private String name;
    private Spell flash;

    // 기본 생성자 필요
    public Liner() {
    }

    public Liner(String name) {
        this.name = name;
        this.flash = new Flash(); // 기본값 설정
    }

    @Override
    public boolean equals(Object obj) {
        Liner other = (Liner) obj;
        if (name.equals(other.name) && flash.equals(other.flash)) {
            return true;
        }
        return false;
    }
}