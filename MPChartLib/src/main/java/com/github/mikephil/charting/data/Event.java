package com.github.mikephil.charting.data;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class Event {
    // DRY 脱水, FIRST_BURST 一爆, SECOND_BURST 二爆, END 出豆, FIRE_WIND 风火, OTHER 其他
    public static final int DRY = 1, FIRST_BURST = 2, SECOND_BURST = 3, END = 4, FIRE_WIND = 7, OTHER = 9;
    private int untilTime;
    private String description;
    private float x;
    private float y;
    private int curStatus;
    public Event(int eventId){
        this.curStatus = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUntilTime() {
        return untilTime;
    }

    public void setUntilTime(int untilTime) {
        this.untilTime = untilTime;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
