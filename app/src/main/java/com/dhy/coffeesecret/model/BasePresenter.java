package com.dhy.coffeesecret.model;

import com.dhy.coffeesecret.pojo.BakeReportProxy;

/**
 * Created by CoDeleven on 17-8-20.
 * 基础Presenter类
 */

public abstract class BasePresenter implements IBasePresenter {
    // 表示是否最小化
    protected static boolean isMinimized = false;
    // 表示是否正在烘焙
    protected static boolean isBakingNow = false;
    // 当前正在烘焙的报告
    protected static BakeReportProxy mCurBakingProxy = null;

    protected IBaseView mViewOperator;
    // 这玩意大多情况都是由presenter创建的。就不给方法设置了
    protected IBaseModel mModelOperator;

    @Override
    public void setView(IBaseView baseView) {
        this.mViewOperator = baseView;
    }

    public void clearBakeReport() {
        mCurBakingProxy = null;
    }

    public BakeReportProxy getCurBakingReport() {
        return mCurBakingProxy;
    }

    public void setMinimize(boolean isMinimized) {
        this.isMinimized = isMinimized;
    }

    public boolean isMinimized() {
        return isMinimized;
    }
    public boolean isBakingNow(){
        return isBakingNow;
    }
}
