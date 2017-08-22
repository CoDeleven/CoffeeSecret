package com.dhy.coffeesecret.model.device_list;

import com.dhy.coffeesecret.model.base.BaseModel;

/**
 * Created by CoDeleven on 17-8-22.
 */

public class Model4ScanList extends BaseModel implements IScanListModel {
    private static Model4ScanList mSelf;
    private Model4ScanList(){

    }
    public static Model4ScanList newInstance(){
        if (mSelf == null){
            mSelf = new Model4ScanList();
        }
        return mSelf;
    }
}
