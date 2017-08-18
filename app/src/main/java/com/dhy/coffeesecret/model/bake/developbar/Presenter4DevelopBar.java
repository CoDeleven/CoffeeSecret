package com.dhy.coffeesecret.model.bake.developbar;

import com.dhy.coffeesecret.model.IBasePresenter;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.DevelopBar;

/**
 * Created by CoDeleven on 17-8-18.
 */

public class Presenter4DevelopBar implements IBasePresenter{
    private IDevelopBarView mOperatorView;
    private IDevelopBarModel mOperatorModel;
    private static Presenter4DevelopBar mSelfPresenter;

    private Presenter4DevelopBar(){
        mOperatorModel = new Model4DevelopBar();
    }

    public static Presenter4DevelopBar newInstance(){
        if(mSelfPresenter == null){
            mSelfPresenter = new Presenter4DevelopBar();
        }
        return mSelfPresenter;
    }

    public void init(){
        mOperatorModel.clearData();
    }

    @Override
    public void setView(IBaseView baseView) {
        this.mOperatorView = (IDevelopBarView)baseView;
    }
    public void updateDevelopBar(int status){
        switch (status){
            case DevelopBar.RAW_BEAN:
                mOperatorModel.incrementRawBeanTime();
                break;
            case DevelopBar.AFTER160:
                mOperatorModel.incrementAfter160Time();
                break;
            case DevelopBar.FIRST_BURST:
                mOperatorModel.incrementFirstBurstTime();
                break;
            default:
                mOperatorModel.incrementRawBeanTime();
        }
        mOperatorView.updateDevelopBar(mOperatorModel.getRawTime() / mOperatorModel.getTotalTime(),
                mOperatorModel.getAfter160Time() / mOperatorModel.getTotalTime(), mOperatorModel.getFirstBurstTime() / mOperatorModel.getTotalTime());
    }

    /**
     * 获取发展率 字符串格式
     * 保留两位小数
     * @return
     */
    public String getDevelopRateString(){
        return String.format("%1$.2f", (mOperatorModel.getFirstBurstTime() * 100) / mOperatorModel.getTotalTime());
    }

    /**
     * 获取发展率 浮点型格式
     * @return
     */
    public float getDevelopRateFloat(){
        return (mOperatorModel.getFirstBurstTime()) / mOperatorModel.getTotalTime();
    }

    /**
     * 获取发展时间字符串 mm:ss
     * @return
     */
    public String getDevelopTimeString() {
        return Utils.getTimeWithFormat(mOperatorModel.getFirstBurstTime());
    }

    /**
     * 获取发展率时间
     * @return
     */
    public Integer getDevelopTimeFloat(){
        return (int)mOperatorModel.getTotalTime();
    }
}
