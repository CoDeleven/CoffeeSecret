package com.github.mikephil.charting.data;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class Event implements Serializable {
    public static final int DRY = 1, FIRST_BURST = 2, SECOND_BURST = 3, END = 4, FIRE_WIND = 7, OTHER = 9;
    // 后来补上
    public static final int FIRST_BURST_END = -2, SECOND_BURST_END = -3;
    private String description = "";
    private int curStatus;
    public Event(){

    }
    public Event(int eventId){
        this.curStatus = eventId;
    }
    public Event(int eventId, String description){
        this.curStatus = eventId;
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurStatus(){
        return curStatus;
    }

    public void setCurStatus(int curStatus){
        this.curStatus = curStatus;
    }

    @Override
    public String toString() {
        return "Event{" +
                "description='" + description + '\'' +
                ", curStatus=" + curStatus +
                '}';
    }
}
