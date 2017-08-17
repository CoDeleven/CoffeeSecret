package com.dhy.coffeesecret.model.report_edit;

import com.dhy.coffeesecret.model.IBaseModel;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;

import java.io.IOException;

/**
 * Created by CoDeleven on 17-8-5.
 */

public class Model4Editor implements IBaseModel {
    private static Model4Editor mModel4Editor;

    private Model4Editor() {
    }

    public static Model4Editor newInstance() {
        if (mModel4Editor == null) {
            mModel4Editor = new Model4Editor();
        }
        return mModel4Editor;
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
