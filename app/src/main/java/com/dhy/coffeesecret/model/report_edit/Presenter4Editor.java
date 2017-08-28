package com.dhy.coffeesecret.model.report_edit;

import android.util.Log;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.utils.ConvertUtils;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;

import java.util.List;

import static com.dhy.coffeesecret.ui.bake.EditBehindActivity.BUTTON_EVENT_NAME;
import static com.dhy.coffeesecret.ui.bake.EditBehindActivity.INVALIDATE_COOKED_WEIGHT;
import static com.dhy.coffeesecret.ui.bake.EditBehindActivity.RERANGE_BEAN_INFO;

/**
 * Created by CoDeleven on 17-8-5.
 */

public class Presenter4Editor extends BaseBlePresenter<IEditView, Model4Editor> {
    private static final String TAG = Presenter4Editor.class.getSimpleName();
    private static Presenter4Editor mPresenter;
    private BakeReportProxy tLocalBakeReport;
    private BeanInfoSimple mCurBeanInfo;

    public BakeReportProxy gettLocalBakeReport() {
        return tLocalBakeReport;
    }

    public void settLocalBakeReport(BakeReport tLocalBakeReport) {
        this.tLocalBakeReport = new BakeReportProxy(tLocalBakeReport);
    }

    private Presenter4Editor() {
        super(Model4Editor.newInstance());
    }

    public static Presenter4Editor newInstance() {
        if (mPresenter == null) {
            mPresenter = new Presenter4Editor();
        }
        return mPresenter;
    }

    /**
     * 从BakeReport获取带事件的节点
     */
    public void generateItem() {
        List<Entry> entries;
        if(mModelOperator.getCurBakingReport() == null){
            entries = tLocalBakeReport.getEntriesWithEvents();
        }else{
            entries = mModelOperator.getCurBakingReport().getEntriesWithEvents();
        }

        getModel().setEntriesWithEvent(entries);
        getView().updateEntryEvents(entries);
    }

    /**
     * 从BakeReport获取BeanInfos
     */
    public void generateBean() {
        List<BeanInfoSimple> simpleBeanInfo;
        if(mModelOperator.getCurBakingReport() == null){
            simpleBeanInfo = tLocalBakeReport.getBeanInfos();
        }else{
            simpleBeanInfo = getModel().getCurBakingReport().getBeanInfos();
        }

        getModel().setBeanInfo(simpleBeanInfo);
        getView().updateBeanInfos(simpleBeanInfo);
    }

    /**
     * 设置当前要更新的Bean
     */
    public void setCurUpdateBeanInfo(BeanInfoSimple infoSimple) {
        mCurBeanInfo = infoSimple;
    }

    /**
     * 更新BeanInfo
     * 当补充完豆种时，会调用updateBeanInfo更新豆种信息
     * 字段有冗余
     *
     * @param beanInfo 新补充的豆种信息
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
        getView().updateText(RERANGE_BEAN_INFO, beanInfo.getName());
    }

    /**
     * 设置熟豆重量
     *
     * @param weight 重量字符串
     */
    public boolean setCookedWeight4BakeReport(String weight) {
        BakeReportProxy localProxy = null;
        if(mModelOperator.getCurBakingReport() == null){
            localProxy = tLocalBakeReport;
        }else{
            localProxy = mModelOperator.getCurBakingReport();
        }
        if (!"".equals(weight) && weight != null) {
            float defaultWeight = ConvertUtils.getReversed2DefaultWeight(Float.parseFloat(weight) + "");
            // 填写的熟豆重量大于生豆重量时进行提示
            if (defaultWeight > localProxy.getRawBeanWeight()) {
                super.mViewOperator.showToast(INVALIDATE_COOKED_WEIGHT, "填写不大于生豆重量的数值...");
                return false;
            }
            localProxy.setCookedBeanWeight(defaultWeight);
        } else {
            localProxy.setCookedBeanWeight(0);
        }
        if (localProxy.getBeanInfos().size() != 1) {
            localProxy.setSingleBeanId(-1);
        }
        return true;
    }

    /**
     * 设置烘焙程度
     *
     * @param bakeDegree 烘焙度
     */
    public void setBakeDegree(float bakeDegree) {
        if(mModelOperator.getCurBakingReport() == null){
            tLocalBakeReport.setBakeDegree(bakeDegree);
        }else{
            getModel().getCurBakingReport().setBakeDegree(bakeDegree);
        }

    }

    /**
     * 保存
     */
    public void save(String token) {
        // TODO 需要根据是烘焙的报告还是已经存在的报告，分别进行处理

        if(mModelOperator.getCurBakingReport() == null){
            Log.d(TAG, "save-data -> " + new Gson().toJson(tLocalBakeReport.getBakeReport()));
            getModel().sendJsonData(token, tLocalBakeReport.getBakeReport());
        }else{
            Log.d(TAG, "save-data -> " + new Gson().toJson(getModel().getCurBakingReport().getBakeReport()));
            getModel().sendJsonData(token, mModelOperator.getCurBakingReport().getBakeReport());
        }

    }

    /**
     * 补充事件
     *
     * @param entry      发生事件的节点
     * @param supplement 要补充的内容
     */
    public void supplyEventInfo(Entry entry, String supplement) {
        Log.d(TAG, "entry: " + entry.toString() + "supplyEventInfo: 补充的内容是：" + supplement);
        String descriptor;
        String subStr;
        BakeReport foo;
        if(mModelOperator.getCurBakingReport() != null){
            // String descriptor = entry.getEvent().getDescription();
            foo = mModelOperator.getCurBakingReport().getBakeReport();
        }else{
            foo = tLocalBakeReport.getBakeReport();
        }
        descriptor = foo.getEvents().get(entry.getX() + "");
        subStr = ConvertUtils.separateStrByChar(descriptor, ":", true);
        descriptor = descriptor.replace(subStr, supplement);


        foo.getEvents().put(entry.getX() + "", descriptor);
        entry.getEvent().setDescription(descriptor);
        // 这里的entry是对proxy设置的，所以没有效果
        super.mViewOperator.updateText(BUTTON_EVENT_NAME, supplement);
    }

    public void initViewWithProxy() {
        // 表示已经有烘焙报告了而且已经烘焙结束了
        // 在点击开始烘焙之后的确会存在BakeReport，在点击了开始之后才会isBakingNow的状态
        if(getCurBakingReport() != null && !isBakingNow()){
            getView().init(getCurBakingReport());
            // 如果不存在正在烘焙的内容，那么查看是否存在不是正在烘焙的内容，如果存在，那么用它来初始化
        }else if(tLocalBakeReport != null){
            getView().init(tLocalBakeReport);
        }
    }
}
