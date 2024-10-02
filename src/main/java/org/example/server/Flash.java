package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({"on"})
@Getter
@Setter
public class Flash {
    int flashCoolTime = 300;
    int coolTime = 0;

    // 기본 생성자 필요
    public Flash() {
    }

    public Flash(int coolTime) {
        this.coolTime = coolTime;
    }

    public boolean isOn(){
        return coolTime == 0;
    }
    public void on() {
        coolTime = 0;
    }

    public void off() {
        coolTime = flashCoolTime;
    }


    @Override
    public boolean equals(Object obj) {
        Flash other = (Flash) obj;
        if (flashCoolTime == other.flashCoolTime && isOn() == other.isOn()) {
            return true;
        }
        return false;
    }
}
