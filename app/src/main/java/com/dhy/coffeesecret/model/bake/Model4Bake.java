package com.dhy.coffeesecret.model.bake;

import com.dhy.coffeesecret.model.base.BaseModel;
import com.dhy.coffeesecret.pojo.Temperature;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Model4Bake extends BaseModel implements IBakeModel {
    private Map<Integer, EventInfo> eventInfoMap = new HashMap<>();
    private static Model4Bake model4Bake = new Model4Bake();

    private Model4Bake() {
    }

    @Override
    public void notifyTemperatureByManual(Temperature temperature) {

    }

    @Override
    public void notifyTemperatureByAtuo(Temperature temperature) {

    }

    @Override
    public void updateEntry(Entry entry, int eventStatus, String extraContent){
        entry.setEvent(new Event(eventStatus, extraContent));
    }

    @Override
    public void recordOneEvent(Integer eventType, EventInfo info) {
        eventInfoMap.put(eventType, info);
    }

    @Override
    public EventInfo getDryEventInfoByStatus(int status) {
        return eventInfoMap.get(new Integer(status));
    }

    public static Model4Bake newInstance() {
        if(model4Bake == null){
            model4Bake = new Model4Bake();
        }
        return model4Bake;
    }

    static class EventInfo{
        private int startIndex;
        private float consumeTime;
        private float avgTemperature;

        public EventInfo(int startIndex, float consumeTime, float avgTemperature) {
            this.startIndex = startIndex;
            this.consumeTime = consumeTime;
            this.avgTemperature = avgTemperature;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public float getConsumeTime() {
            return consumeTime;
        }

        public void setConsumeTime(float consumeTime) {
            this.consumeTime = consumeTime;
        }

        public float getAvgTemperature() {
            return avgTemperature;
        }

        public void setAvgTemperature(float avgTemperature) {
            this.avgTemperature = avgTemperature;
        }
    }
}
