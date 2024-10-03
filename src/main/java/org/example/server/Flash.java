package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({"on"})
@Getter
@Setter
public class Flash extends Spell{

    // 기본 생성자 필요
    public Flash() {
    }

    public Flash(int coolTime) {
        this.coolTime = coolTime;
    }

}
