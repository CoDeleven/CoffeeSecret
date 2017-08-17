package com.dhy.coffeesecret.model.report_edit;

import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

/**
 * Created by CoDeleven on 17-8-5.
 */

public interface IEditView extends IBaseView {
    void updateBeanInfos(List<BeanInfoSimple> infoSimples);
    void updateEntryEvents(List<Entry> entryEevents);
    void init(BakeReportProxy proxy);
}
