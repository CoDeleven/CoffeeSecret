package com.dhy.coffeesecret.interfaces;

import com.dhy.coffeesecret.pojo.BakeReport;

/**
 * Created by CoDeleven on 17-2-9.
 */

public interface ReportHandler {
    boolean sendBakeReport(BakeReport bakeReport);

}
