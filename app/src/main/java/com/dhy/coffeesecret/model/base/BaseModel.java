package com.dhy.coffeesecret.model.base;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.utils.SettingTool;

import cn.jesse.nativelogger.NLogger;

/**
 * Created by CoDeleven on 17-8-22.
 */

public abstract class BaseModel implements IBaseModel {
    private static final String TAG = BaseModel.class.getSimpleName();
    // 当前正在烘焙的报告
    protected static BakeReportProxy mCurBakingProxy = null;
    // 已经在MyApplication初始化好了
    protected static UniversalConfiguration mConfig = SettingTool.getConfig();
    protected static BakeReport mNoBakingBakeReport = null;

    @Override
    final public void clearBakeReport() {
        NLogger.i(TAG, "BaseModel:清理mCurBakingProxy, mNoBakingBakeReport...");
        mCurBakingProxy = null;
        mNoBakingBakeReport = null;
    }

    @Override
    final public BakeReportProxy getCurBakingReport() {
        return mCurBakingProxy;
    }

    @Override
    final public void initBakeReport(){
        if(mCurBakingProxy == null){
            mCurBakingProxy = new BakeReportProxy();
        }
    }

    @Override
    final public UniversalConfiguration getAppConfig(){
        return mConfig;
    }

    @Override
    final public void updateAppConfig(UniversalConfiguration appConfig) {
        SettingTool.saveConfig(appConfig);
    }

    public void setNoBakingBakeReport(BakeReport bakeReport){
        BaseModel.mNoBakingBakeReport = bakeReport;
    }
    public BakeReport getNoBakingBakeReport(){
        return BaseModel.mNoBakingBakeReport;
    }
}
