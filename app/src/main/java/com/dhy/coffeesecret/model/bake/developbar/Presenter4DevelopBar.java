package com.dhy.coffeesecret.model.bake.developbar;

import com.dhy.coffeesecret.model.base.BasePresenter;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.dhy.coffeesecret.views.DevelopBar;

/**
 * Created by CoDeleven on 17-8-18.
 */

public class Presenter4DevelopBar extends BasePresenter<IDevelopBarView, Model4DevelopBar> {
    private static Presenter4DevelopBar mSelfPresenter;

    private Presenter4DevelopBar(){
        super(Model4DevelopBar.newInstance());
        // mOperatorModel = new Model4DevelopBar();
    }
    public static Presenter4DevelopBar newInstance(){
        if(mSelfPresenter == null){
            mSelfPresenter = new Presenter4DevelopBar();
        }
        return mSelfPresenter;
    }

    public void init(){
        getModel().clearData();
    }

    public void updateDevelopBar(int status){
        switch (status){
            case DevelopBar.RAW_BEAN:
                getModel().incrementRawBeanTime();
                break;
            case DevelopBar.AFTER160:
                getModel().incrementAfter160Time();
                break;
            case DevelopBar.FIRST_BURST:
                getModel().incrementFirstBurstTime();
                break;
            default:
                getModel().incrementRawBeanTime();
        }
        getView().updateDevelopBar(getModel().getRawTime() / getModel().getTotalTime(),
                getModel().getAfter160Time() / getModel().getTotalTime(), getModel().getFirstBurstTime() / getModel().getTotalTime());
    }

    /**
     * 获取发展率 字符串格式
     * 保留两位小数
     * @return
     */
    public String getDevelopRateString(){
        return String.format("%1$.2f", (getModel().getFirstBurstTime() * 100) / getModel().getTotalTime());
    }

    /**
     * 获取发展率 浮点型格式
     * @return
     */
    public float getDevelopRateFloat(){
        return (getModel().getFirstBurstTime()) / getModel().getTotalTime();
    }

    /**
     * 获取发展时间字符串 mm:ss
     * @return
     */
    public String getDevelopTimeString() {
        return FormatUtils.getTimeWithFormat(getModel().getFirstBurstTime());
    }

    /**
     * 获取发展率时间
     * @return
     */
    public Integer getDevelopTimeFloat(){
        return (int)getModel().getTotalTime();
    }
}
