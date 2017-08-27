package com.dhy.coffeesecret.model.bake;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBakeModel{
    void recordOneEvent(Integer eventType, Model4Bake.EventInfo eventInfo);
    void updateEntry(Entry entry, int eventStatus, String extraContent);
    Model4Bake.EventInfo getDryEventInfoByStatus(int status);
}
