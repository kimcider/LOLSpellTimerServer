package org.example.server;

public class Flash {
    static int flashCoolTime = 300;
    int coolTime = flashCoolTime; // 오타 수정: collTime -> coolTime
    boolean on = true;

    // 기본 생성자 필요
    public Flash() {
    }

    public Flash(int coolTime, boolean on) {
        this.coolTime = coolTime;
        this.on = on;
    }

    public int getFlashCoolTime() {
        return flashCoolTime;
    }

    public void setFlashCoolTime(int flashCoolTime) {
        Flash.flashCoolTime = flashCoolTime; // static 필드에 대한 setter는 특별하게 처리해야 함
    }

    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
