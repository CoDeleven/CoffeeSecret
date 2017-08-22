package com.dhy.coffeesecret.model.report;

import com.dhy.coffeesecret.model.BaseBlePresenter;

/**
 * Created by CoDeleven on 17-8-6.
 */

public class Presenter4Report extends BaseBlePresenter<IReportView, Model4Report> {
    private static Presenter4Report mPresenter;

    private Presenter4Report() {
        super(Model4Report.newInstance());
    }

    public static Presenter4Report newInstance(){
        if(mPresenter == null){
            mPresenter = new Presenter4Report();
        }
        return mPresenter;
    }

    public void initViewWithProxy(){
        getView().init(getCurBakingReport());
    }
}
