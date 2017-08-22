package com.dhy.coffeesecret.model.report;

import com.dhy.coffeesecret.model.base.IBaseView;
import com.dhy.coffeesecret.pojo.BakeReportProxy;

/**
 * Created by CoDeleven on 17-8-6.
 */

public interface IReportView extends IBaseView {
    void init(BakeReportProxy proxy);
}
