package com.dhy.coffeesecret.model;

import com.dhy.coffeesecret.interfaces.ReportHandler;
import com.dhy.coffeesecret.pojo.BakeReport;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class BakeReportModel implements ReportHandler {
    @Override
    public boolean sendBakeReport(BakeReport bakeReport) {
        return false;
    }
}
