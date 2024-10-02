package org.example.server;

public class Liner {
    String name;
    Flash flash;

    // 기본 생성자 필요
    public Liner() {
    }

    public Liner(String name) {
        this.name = name;
        this.flash = new Flash(); // 기본값 설정
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }
}