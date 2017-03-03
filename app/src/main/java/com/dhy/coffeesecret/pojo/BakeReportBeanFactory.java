package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.LineData;

/**
 * Created by CoDeleven on 17-3-3.
 */

public class BakeReportBeanFactory {
    private static BakeReportProxy proxy;
    public static BakeReportProxy getInstance(){
        if(proxy == null){
            proxy = new BakeReportProxy();
        }
        return proxy;
    }
}
