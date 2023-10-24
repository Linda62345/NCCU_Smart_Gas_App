package com.example.smartgasapp;

public class UsageHistoryItem {
    String volume;
    String time;
    String percent;

    public UsageHistoryItem(String volume, String time, String percent) {
        this.volume = volume;
        this.time = time;
        this.percent = percent;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

}
