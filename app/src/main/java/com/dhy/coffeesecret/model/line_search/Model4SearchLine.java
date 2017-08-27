package com.dhy.coffeesecret.model.line_search;

import com.dhy.coffeesecret.model.base.BaseModel;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class Model4SearchLine extends BaseModel {
    private static Model4SearchLine mSelf;

    private Model4SearchLine() {
    }

    public static Model4SearchLine newInstance(){
        if(mSelf == null){
            mSelf = new Model4SearchLine();
        }
        return mSelf;
    }
}
