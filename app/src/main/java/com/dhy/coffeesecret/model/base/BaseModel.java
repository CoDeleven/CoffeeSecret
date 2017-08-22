package com.dhy.coffeesecret.model.base;

import com.dhy.coffeesecret.pojo.BakeReportProxy;

/**
 * Created by CoDeleven on 17-8-22.
 */

public abstract class BaseModel implements IBaseModel {
    // 当前正在烘焙的报告
    protected static BakeReportProxy mCurBakingProxy = null;

    @Override
    public void clearBakeReport() {
        mCurBakingProxy = null;
    }

    @Override
    public BakeReportProxy getCurBakingReport() {
        return mCurBakingProxy;
    }

    @Override
    public void initBakeReport(){
        if(mCurBakingProxy == null){
            mCurBakingProxy = new BakeReportProxy();
        }
    }
}
