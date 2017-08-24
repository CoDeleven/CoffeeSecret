package com.dhy.coffeesecret.model.base;

import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.utils.SettingTool;

/**
 * Created by CoDeleven on 17-8-22.
 */

public abstract class BaseModel implements IBaseModel {
    // 当前正在烘焙的报告
    protected static BakeReportProxy mCurBakingProxy = null;
    // 已经在MyApplication初始化好了
    protected static UniversalConfiguration mConfig = SettingTool.getConfig();

    @Override
    final public void clearBakeReport() {
        mCurBakingProxy = null;
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
}
