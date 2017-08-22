package com.dhy.coffeesecret.model.report;

import com.dhy.coffeesecret.model.base.BaseModel;

/**
 * Created by CoDeleven on 17-8-6.
 */

public class Model4Report extends BaseModel implements IReportModel {
    private static Model4Report mSelf;

    private Model4Report() {
    }
    public static Model4Report newInstance(){
        if(mSelf == null){
            mSelf = new Model4Report();
        }
        return mSelf;
    }
}
