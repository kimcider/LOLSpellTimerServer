package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({"flashCoolTime"})
@Getter
@Setter
public class Flash {
    static int flashCoolTime = 300;
    int coolTime = flashCoolTime; // 오타 수정: collTime -> coolTime
    boolean on = true;

    // 기본 생성자 필요
    public Flash() {
    }

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
    }


    public Flash(int coolTime, boolean on) {
        this.coolTime = coolTime;
        this.on = on;
    }


    @Override
    public boolean equals(Object obj) {
        Flash other = (Flash) obj;
        if (on == other.on && coolTime == other.coolTime) {
            return true;
        }
        return false;
    }
}
