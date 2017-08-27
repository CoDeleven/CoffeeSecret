package com.dhy.coffeesecret.model.report_edit;

import com.dhy.coffeesecret.model.base.BaseModel;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
import java.util.List;

/**
 * Created by CoDeleven on 17-8-5.
 */

public class Model4Editor extends BaseModel implements IEditModel{
    private static Model4Editor mModel4Editor;

    private List<Entry> entries = null;
    private List<BeanInfoSimple> beanInfoSimples;

    private Model4Editor() {
    }

    public static Model4Editor newInstance() {
        if (mModel4Editor == null) {
            mModel4Editor = new Model4Editor();
        }
        return mModel4Editor;
    }

    void setBeanInfo(List<BeanInfoSimple> beanInfo){
        this.beanInfoSimples = beanInfo;
    }

    void setEntriesWithEvent(List<Entry> entries){
        this.entries = entries;
    }

    List<Entry> getEntries() {
        return entries;
    }

    List<BeanInfoSimple> getBeanInfoSimples() {
        return beanInfoSimples;
    }

    public void sendJsonData(final BakeReport bakeReport) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtils.execute(URLs.ADD_BAKE_REPORT, bakeReport);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
