package com.dhy.coffeesecret.model.base;

import com.dhy.coffeesecret.pojo.BakeReportProxy;

/**
 * Created by CoDeleven on 17-8-20.
 * 基础Presenter类
 */

public abstract class BasePresenter <V extends IBaseView, M extends BaseModel> implements IBasePresenter<V> {
    // 表示是否最小化
    protected static boolean isMinimized = false;
    // 表示是否正在烘焙
    protected static boolean isBakingNow = false;
    // 当前正在烘焙的报告
    // protected static BakeReportProxy mCurBakingProxy = null;

    protected V mViewOperator;
    // 这玩意大多情况都是由presenter创建的。就不给方法设置了
    protected M mModelOperator;

    public BasePresenter(M mModelOperator) {
        this.mModelOperator = mModelOperator;
    }

    @Override
    public void setView(V baseView) {
        this.mViewOperator = baseView;
    }

    public void clearBakeReport() {
        mModelOperator.clearBakeReport();
        // mCurBakingProxy = null;
    }

    public void initPrepareBakeReport(){
        mModelOperator.initBakeReport();
    }

    public BakeReportProxy getCurBakingReport() {
        return mModelOperator.getCurBakingReport();
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
    public void setBakingNow(boolean isBakingNow){
        this.isBakingNow = isBakingNow;
    }
    public M getModel(){
        return this.mModelOperator;
    }
    public V getView(){
        return this.mViewOperator;
    }
}
