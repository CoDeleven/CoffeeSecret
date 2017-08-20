package com.dhy.coffeesecret.model.report_edit;

import android.util.Log;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.utils.Utils;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;

import java.util.List;

import static com.dhy.coffeesecret.ui.device.EditBehindActivity.BUTTON_NAME;
import static com.dhy.coffeesecret.ui.device.EditBehindActivity.INVALIDATE_COOKED_WEIGHT;
import static com.dhy.coffeesecret.ui.device.EditBehindActivity.RERANGE_BEAN_INFO;

/**
 * Created by CoDeleven on 17-8-5.
 */

public class Presenter4Editor extends BaseBlePresenter {
    private static final String TAG = Presenter4Editor.class.getSimpleName();
    private static Presenter4Editor mPresenter;
    private List<Entry> entries = null;
    private List<BeanInfoSimple> beanInfoSimples;
    private BeanInfoSimple mCurBeanInfo;
    private Model4Editor mModel4Editor;

    private Presenter4Editor() {
        this.mModel4Editor = Model4Editor.newInstance();
    }

    public static Presenter4Editor newInstance() {
        if (mPresenter == null) {
            mPresenter = new Presenter4Editor();
        }
        return mPresenter;
    }

    @Override
    public void setView(IBaseView baseView) {
        super.setView(baseView);
        super.mViewOperator = baseView;
    }

    /**
     * 从BakeReport获取带事件的节点
     */
    public void generateItem() {
        entries = super.mCurBakingProxy.getEntriesWithEvents();
        ((IEditView)(super.mViewOperator)).updateEntryEvents(entries);
    }

    /**
     * 从BakeReport获取BeanInfos
     */
    public void generateBean() {
        beanInfoSimples = super.mCurBakingProxy.getBeanInfos();
        ((IEditView) super.mViewOperator).updateBeanInfos(beanInfoSimples);
    }

    /**
     * 设置当前要更新的Bean
     */
    public void setCurUpdateBeanInfo(BeanInfoSimple infoSimple) {
        mCurBeanInfo = infoSimple;
    }

    /**
     * 更新BeanInfo
     *
     * @param beanInfo
     */
    public void updateBeanInfo(BeanInfo beanInfo) {
        mCurBeanInfo.setBeanName(beanInfo.getName());
        mCurBeanInfo.setWaterContent(beanInfo.getWaterContent() + "");
        mCurBeanInfo.setManor(beanInfo.getManor());
        mCurBeanInfo.setProcess(beanInfo.getProcess());
        mCurBeanInfo.setLevel(beanInfo.getLevel());
        mCurBeanInfo.setAltitude(beanInfo.getAltitude());
        mCurBeanInfo.setCountry(beanInfo.getCountry());
        mCurBeanInfo.setArea(beanInfo.getArea());
        mCurBeanInfo.setSpecies(beanInfo.getSpecies());
        super.mViewOperator.updateText(RERANGE_BEAN_INFO, beanInfo.getName());
    }

    /**
     * 设置熟豆重量
     *
     * @param weight
     */
    public void setCookedWeight4BakeReport(String weight) {
        if (!"".equals(weight) && weight != null) {
            float defaultWeight = Utils.getReversed2DefaultWeight(Float.parseFloat(weight) + "");
            // 填写的熟豆重量大于生豆重量时进行提示
            if (defaultWeight > super.mCurBakingProxy.getRawBeanWeight()) {
                super.mViewOperator.showToast(INVALIDATE_COOKED_WEIGHT, "填写不大于生豆重量的数值...");
                return;
            }
            super.mCurBakingProxy.setCookedBeanWeight(defaultWeight);
        } else {
            super.mCurBakingProxy.setCookedBeanWeight(0);
        }
        if (super.mCurBakingProxy.getBeanInfos().size() != 1) {
            super.mCurBakingProxy.setSingleBeanId(-1);
        }
    }

    /**
     * 设置烘焙程度
     *
     * @param bakeDegree
     */
    public void setBakeDegree(float bakeDegree) {
        super.mCurBakingProxy.setBakeDegree(bakeDegree);
    }

    /**
     * 保存
     */
    public void save() {
        // TODO 测试用隐藏
        // mModel4Editor.sendJsonData(super.mCurBakingProxy.getBakeReport());

        Log.d(TAG, "save-data -> " + new Gson().toJson(super.mCurBakingProxy.getBakeReport()));
    }

    /**
     * 补充事件
     * @param entry 发生事件的节点
     * @param supplement 要补充的内容
     */
    public void supplyEventInfo(Entry entry, String supplement){
        entry.getEvent().setDescription(supplement);
        super.mViewOperator.updateText(BUTTON_NAME, supplement);
    }

    public void initViewWithProxy(){
        ((IEditView)(super.mViewOperator)).init(super.mCurBakingProxy);
    }
}
