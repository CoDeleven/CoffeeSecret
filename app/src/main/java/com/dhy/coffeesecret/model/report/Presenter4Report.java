package com.dhy.coffeesecret.model.report;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseView;

/**
 * Created by CoDeleven on 17-8-6.
 */

public class Presenter4Report extends BaseBlePresenter {
    private static Presenter4Report mPresenter;
    @Override
    public void setView(IBaseView baseView) {
        super.mViewOperator = baseView;
    }

    private Presenter4Report() {
    }

    public static Presenter4Report newInstance(){
        if(mPresenter == null){
            mPresenter = new Presenter4Report();
        }
        return mPresenter;
    }

    public void initViewWithProxy(){
        ((IReportView)super.mViewOperator).init(super.mCurBakingProxy);
    }
}
