package com.dhy.coffeesecret.model.report;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;

/**
 * Created by CoDeleven on 17-8-6.
 */

public class Presenter4Report extends BaseBlePresenter<IReportView, Model4Report> {
    private static Presenter4Report mPresenter;
    private BakeReportProxy tLocalBakeReport;
    private Presenter4Report() {
        super(Model4Report.newInstance());
    }

    public static Presenter4Report newInstance(){
        if(mPresenter == null){
            mPresenter = new Presenter4Report();
        }
        return mPresenter;
    }

    /**
     *
     */
    public void initViewWithProxy(){
        // 表示已经有烘焙报告了而且已经烘焙结束了
        // 在点击开始烘焙之后的确会存在BakeReport，在点击了开始之后才会isBakingNow的状态
        if(getCurBakingReport() != null && !isBakingNow()){
            getView().init(getCurBakingReport());
        // 如果不存在正在烘焙的内容，那么查看是否存在不是正在烘焙的内容，如果存在，那么用它来初始化
        }else if(tLocalBakeReport != null){
            getView().init(tLocalBakeReport);
        }

    }


    public void setLocalBakeReport(BakeReport bakeReport){
        this.tLocalBakeReport = new BakeReportProxy(bakeReport);
    }

    public BakeReportProxy gettLocalBakeReport() {
        return tLocalBakeReport;
    }
}
