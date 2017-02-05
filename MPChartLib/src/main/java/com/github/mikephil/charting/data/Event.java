package com.github.mikephil.charting.data;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class Event {
    private int untilTime;
    private String description;
    private float x;
    private float y;

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
