package com.example.testen;

public class APInfo {
    private String name;
    private int level;
    private String BSSID;
    private int count;

    public APInfo(int level, String BSSID,String name) {
        this.level = level;
        this.BSSID = BSSID;
        this.name = name;
        count = 1;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
