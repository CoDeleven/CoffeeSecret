package com.dhy.coffeesecret.model.device;

import android.util.Log;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.interfaces.IBluetoothOperator;
import com.dhy.coffeesecret.ui.bake.PreparationFragment;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

import cn.jesse.nativelogger.NLogger;

/**
 * Created by CoDeleven on 17-8-1.
 */

public class Presenter4Device extends BaseBlePresenter<IDeviceView, Model4Device> {
    private static final String TAG = Presenter4Device.class.getSimpleName();
    private static Presenter4Device mSelf;
    private boolean isStart = false;
    private List<BeanInfoSimple> temporaryBeanInfo = new ArrayList<>();
    private BakeReport temporaryReferTemperatures;
    private Presenter4Device() {
        super(Model4Device.newInstance());
    }

    /**
     * 第一次调用必须调用该方法
     *
     * @param bluetoothOperator
     * @return
     */
    public static Presenter4Device newInstance(IBluetoothOperator bluetoothOperator) {
        if (BaseBlePresenter.mBluetoothOperator == null) {
            mSelf = new Presenter4Device();
            BaseBlePresenter.mBluetoothOperator = bluetoothOperator;
        }
        return mSelf;
    }

    public void setTemporaryBeanInfo(List<BeanInfoSimple> temporaryBeanInfo) {
        this.temporaryBeanInfo = temporaryBeanInfo;
    }

    public List<BeanInfoSimple> getTemporaryBeanInfo(){
        return this.temporaryBeanInfo;
    }

    @Override
    public void notifyTemperature(Temperature temperature) {
        Log.d(TAG, "presenter4device -> notifyTemperature: 收到温度信息：" + temperature);
        if (!isStart) {
            mModelOperator.updateBeginTemperature(temperature);
            // beginTemp = temperature.getBeanTemp();
            isStart = true;
        }
        mViewOperator.updateText(PreparationFragment.UPDATE_TEMPERATURE_TEXT, temperature);
    }

    public void setTemporaryReferTemperatures(BakeReport temporaryReferTemperatures) {
        this.temporaryReferTemperatures = temporaryReferTemperatures;
    }

    public BakeReport getTemporaryReferTemperatures() {
        return temporaryReferTemperatures;
    }

    @Override
    public void onScanning(ScanResult result) {
        if (result.getDevice().getAddress().equals(mModelOperator.getLastConnectedAddr())) {
            mViewOperator.showToast(PreparationFragment.AUTO_CONNECTION_TIPS, "正在自动重连...");
            // 连接蓝牙设备
            BaseBlePresenter.mBluetoothOperator.connect(result);
            // 停止扫描
            BaseBlePresenter.mBluetoothOperator.stopScanDevice();
        }
    }

    public boolean isConnected() {
        return BaseBlePresenter.mBluetoothOperator.isConnected();
    }

    /**
     * 开始烘焙
     * 初始化烘焙报告，设置豆种信息，统一单位，设置设备名，
     * 启动BakeActivity，清理临时变量，恢复一些视图
     */
    public void goBake() {
        if(mBluetoothOperator == null || !mBluetoothOperator.isConnected()){
            mViewOperator.showWarnDialog(PreparationFragment.NO_BLUETOOTH_CONNECTED);
            return;
        }

        initPrepareBakeReport();

        BakeReportProxy proxy = mModelOperator.getCurBakingReport();
        NLogger.i(TAG, "总共放入豆种数量为:" + temporaryBeanInfo.size());
        if(temporaryBeanInfo.size() == 0){
            BeanInfoSimple simpleBean = new BeanInfoSimple();
            simpleBean.setBeanName("样品豆");
            simpleBean.setUsage("0");
            simpleBean.setSingleBeanId(-1);

            temporaryBeanInfo.add(simpleBean);
        }



        // 对于只有一个豆种的情况进行id特殊处理
        if (temporaryBeanInfo.size() == 1) {
            proxy.setSingleBeanId(temporaryBeanInfo.get(0).getSingleBeanId());
        }
        // 转换为统一单位kg
        for (BeanInfoSimple simple : temporaryBeanInfo) {
            simple.setUsage(ConvertUtils.getDefaultUnitWeight(simple.getUsage(), MyApplication.weightUnit) + "");
        }
        // 务必在转换为统一单位后进行调用
        proxy.setBeanInfoSimples(temporaryBeanInfo);
        // 设置设备名
        proxy.setDevice(mBluetoothOperator.getConnectedDevice().getName());
        // 启动BakeActivity
        getView().goNextActivity(temporaryReferTemperatures);

        finishDeviceFragmentTask();
    }

    /**
     * 开始烘焙之后处理一些变量，将其初始化即归零
     * 以免下次回到DeviceFragment拥有错误的数据
     */
    private void finishDeviceFragmentTask(){
        temporaryBeanInfo = new ArrayList<>();
        temporaryReferTemperatures = null;
        resetBluetoothListener();
        mViewOperator.updateText(PreparationFragment.FINISH_DEVICE_FRAGMENT_TASK, "");
    }
}
