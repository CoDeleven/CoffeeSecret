package com.dhy.coffeesecret.ui.device;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.device.IDeviceView;
import com.dhy.coffeesecret.model.device.Presenter4Device;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.services.IBluetoothOperator;
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;
import com.dhy.coffeesecret.ui.mine.BluetoothListActivity;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.ArcProgress;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dhy.coffeesecret.MyApplication.temperatureUnit;

// TODO 待重构,重构对象-> 统一符号处理、统一handler处理、统一dialog的生成方式

public class DeviceFragment extends Fragment implements IDeviceView {

    public static final int AUTO_CONNECTION_TIPS = 0x100;
    public static final int UPDATE_TEMPERATURE_TEXT = 0x111;
    private static String lastAddress = null;
    private static Presenter4Device mPresenter;
    @Bind(R.id.id_device_prepare_bake)
    Button mPrepareBake;
    List<BeanInfoSimple> beanInfos;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.id_modify_beanInfos)
    LinearLayout beanInfoBoard;
    @Bind(R.id.bluetooth_status)
    TextView bluetoothStatus;
    @Bind(R.id.bluetooth_operator)
    TextView mTvListBluetooth;
    @Bind(R.id.id_add_bean)
    Button mBtModifyBeanInfo;
    @Bind(R.id.id_arcprogress_bean)
    ArcProgress tempratureNeedConvert1;
    @Bind(R.id.id_arcprogress_inwind)
    ArcProgress tempratureNeedConvert2;
    @Bind(R.id.id_arcprogress_outwind)
    ArcProgress tempratureNeedConvert3;
    @Bind(R.id.id_device_mode)
    Button idDeviceMode;
    @Bind(R.id.id_baking_comma0)
    TextView idBakingComma0;
    @Bind(R.id.id_baking_accBeanTemp_before)
    TextView idBakingAccBeanTempBefore;
    @Bind(R.id.id_baking_accBeanTemp_after)
    TextView idBakingAccBeanTempAfter;
    @Bind(R.id.id_temp_unit0)
    TextView idTempUnit0;
    @Bind(R.id.id_layout_switch)
    RelativeLayout idLayoutSwitch;
    @Bind(R.id.device_bake_circle1)
    RelativeLayout deviceBakeCircle1;
    @Bind(R.id.bake_vertical_refer)
    View bakeVerticalRefer;
    @Bind(R.id.bake_horizontal_refer)
    View bakeHorizontalRefer;
    @Bind(R.id.id_baking_comma1)
    TextView idBakingComma1;
    @Bind(R.id.id_baking_accInwindTemp_before)
    TextView idBakingAccInwindTempBefore;
    @Bind(R.id.id_baking_accInwindTemp_after)
    TextView idBakingAccInwindTempAfter;
    @Bind(R.id.id_temp_unit1)
    TextView idTempUnit1;
    @Bind(R.id.device_bake_circle2)
    RelativeLayout deviceBakeCircle2;
    @Bind(R.id.id_baking_comma2)
    TextView idBakingComma2;
    @Bind(R.id.id_baking_accOutwindTemp_before)
    TextView idBakingAccOutwindTempBefore;
    @Bind(R.id.id_baking_accOutwindTemp_after)
    TextView idBakingAccOutwindTempAfter;
    @Bind(R.id.id_temp_unit2)
    TextView idTempUnit2;
    @Bind(R.id.device_bake_circle3)
    RelativeLayout deviceBakeCircle3;
    @Bind(R.id.device_content)
    RelativeLayout deviceContent;
    @Bind(R.id.id_bake_beanTemp)
    TextView idBakeBeanTemp;
    @Bind(R.id.id_bake_inwindTemp)
    TextView idBakeInwindTemp;
    @Bind(R.id.id_bake_outwindTemp)
    TextView idBakeOutwindTemp;
    private BakeReport referTemperatures;
    private Handler mToastHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_CONNECTION_TIPS:
                    T.showShort(getContext(), (String) msg.obj);
                    break;
            }
            return false;
        }
    });
    private Handler mTextHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    bluetoothStatus.setText(" 已连接");
                    mTvListBluetooth.setText("切换");
                    break;
                case 1:
                    bluetoothStatus.setText(" 未连接");
                    mTvListBluetooth.setText("连接");
                    break;
                case UPDATE_TEMPERATURE_TEXT:
                    Temperature temperature = (Temperature) msg.obj;
                    updateTemperatureText(temperature);
            }
            return false;
        }
    });
    private Handler mShowHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final AlertDialog.Builder dialogBuilder =
                    new AlertDialog.Builder(getContext());
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            switch (msg.what) {
                case 0:
                    dialogBuilder.setTitle("");
                    dialogBuilder.setMessage("当前尚未连接蓝牙设备，请确认");
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), BluetoothListActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.show();
                    break;
                case 1:
                    dialogBuilder.setMessage("当前尚未添加豆种");
                    dialogBuilder.setPositiveButton("去添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showDialogFragment();
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.show();
                    break;
                case 3:

            }
            return false;
        }
    });
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取蓝牙服务
            IBluetoothOperator mBluetoothOperator = ((BluetoothService.BluetoothBinder) service).getBluetoothOperator();
            mPresenter = Presenter4Device.newInstance(mBluetoothOperator);
            setupPresenter();

            // 如果lastaddress不为空，则尝试直接连接该蓝牙;
            if (!"".equals(lastAddress)) {
                T.showShort(getContext(), "正在搜索上一次设备:" + lastAddress + "...");
                // 通过扫描，如果扫描到地址一样的设备，则进行重连
                mBluetoothOperator.startScanDevice();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 用于更新温度显示
     *
     * @param temperature 温度
     */
    private void updateTemperatureText(Temperature temperature) {
        idBakeBeanTemp.setText(Utils.getCrspTempratureValue(temperature.getBeanTemp() + "") + MyApplication.temperatureUnit);
        idBakingAccBeanTempBefore.setText(Utils.getCommaBefore(temperature.getAccBeanTemp()));
        idBakingAccBeanTempAfter.setText(Utils.getCommaAfter(temperature.getAccBeanTemp()));
        idTempUnit0.setText(temperatureUnit + "/m");

        idBakeInwindTemp.setText(Utils.getCrspTempratureValue(temperature.getInwindTemp() + "") + MyApplication.temperatureUnit);
        idBakingAccInwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccInwindTemp()));
        idBakingAccInwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccInwindTemp()));
        idTempUnit1.setText(temperatureUnit + "/m");

        idBakeOutwindTemp.setText(Utils.getCrspTempratureValue(temperature.getOutwindTemp() + "") + MyApplication.temperatureUnit);
        idBakingAccOutwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccOutwindTemp()));
        idBakingAccOutwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccOutwindTemp()));
        idTempUnit2.setText(temperatureUnit + "/m");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取最后一次连接着的蓝牙设备地址
        lastAddress = SettingTool.getConfig(getContext()).getAddress();
        // 判断是否连接过蓝牙，如果尚未连接过，初始化
        if (mPresenter == null) {
            Intent intent = new Intent(getContext().getApplicationContext(), BluetoothService.class);
            getContext().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupPresenter();
    }

    @OnClick({R.id.id_modify_beanInfos, R.id.bluetooth_operator, R.id.id_device_prepare_bake, R.id.id_add_bean})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_add_bean:
            case R.id.id_modify_beanInfos:
                showDialogFragment();
                break;
            case R.id.bluetooth_operator:
                Intent intent = new Intent(getContext(), BluetoothListActivity.class);
                startActivityForResult(intent, 9);
                break;
            case R.id.id_device_prepare_bake:
                goBake();
                break;
        }
    }

    /**
     * 安装presenter
     */
    private void setupPresenter() {
        // 如果操作对象不为null而且是连接着的状态
        if (mPresenter != null) {
            mPresenter.setView(this);
            // 更改蓝牙监听到该Presenter
            mPresenter.initBluetoothListener();
        } else {
            mTextHandler.sendEmptyMessage(1);
        }
    }

    private void showDialogFragment() {
        final BakeDialog dialogFragment = new BakeDialog();
        Bundle bundle = new Bundle();
        if (beanInfos != null) {
            bundle.putSerializable("beanInfos", new ArrayList<>(beanInfos));
        }
        dialogFragment.setArguments(bundle);

        dialogFragment.setBeanInfosListener(new BakeDialog.OnBeaninfosConfirmListener() {
            @Override
            public void setBeanInfos(List<BeanInfoSimple> beanInfos) {
                DeviceFragment.this.beanInfos = beanInfos;
                // 隐藏ModifyBeanInfo的按钮
                mBtModifyBeanInfo.setVisibility(View.GONE);
                // 显示豆种信息列表
                beanInfoBoard.setVisibility(View.VISIBLE);
                // 生成简要的豆种信息在界面上
                showSimpleBeanInfo2Fragment();
            }

            @Override
            public void setTemperatures(BakeReport temperatures) {
                DeviceFragment.this.referTemperatures = temperatures;
            }
        });
        FragmentTool.getFragmentToolInstance(getContext()).showDialogFragmen("dialogFragment", dialogFragment);
    }

    private void goBake() {
        if (mPresenter == null || !mPresenter.isConnected()) {
            mShowHandler.sendEmptyMessage(0);
            return;
        }
        if(beanInfos == null){
            beanInfos = new ArrayList<>();
        }
        if (!(beanInfos.size() > 0)) {
            // TODO 理论上是要处理的，但是懒得
            // return;
        }
        mPresenter.initBakeReport();
        BakeReportProxy proxy = mPresenter.getBakeReportProxy();

        Intent intent = new Intent(getContext(), BakeActivity.class);
        proxy.setBeanInfoSimples(beanInfos);
        if (beanInfos.size() == 1) {
            proxy.setSingleBeanId(beanInfos.get(0).getSingleBeanId());
        }
        for (BeanInfoSimple simple : beanInfos) {
            simple.setUsage(Utils.getReversed2DefaultWeight(simple.getUsage()) + "");

        }
        intent.putExtra(BakeActivity.DEVICE_NAME, mPresenter.getConnectedDevice());
        if (referTemperatures != null) {
            intent.putExtra(BakeActivity.ENABLE_REFERLINE, referTemperatures);
        }

        mBtModifyBeanInfo.setVisibility(View.INVISIBLE);
        startActivity(intent);
        mPresenter.resetBluetoothListener();
    }


    @Override
    public void updateText(int index, Object updateContent) {
        Message msg = new Message();
        msg.what = index;
        msg.obj = updateContent;
        mTextHandler.sendMessage(msg);
    }

    @Override
    public void showToast(int index, String toastContent) {
        Message msg = new Message();
        msg.what = index;
        msg.obj = toastContent;
        mToastHandler.sendMessage(msg);
    }

    @Override
    public void showDialog(int index) {

    }

    private List<TextView> generateSimpleBeanInfo(List<BeanInfoSimple> beanInfoSimples) {
        List<TextView> simpleBeanInfos = new LinkedList<>();
        for (int i = 0; i < beanInfoSimples.size(); i++) {
            TextView tvBeanInfo = new TextView(getContext());
            tvBeanInfo.setText((i + 1) + ".   " + beanInfoSimples.get(i).getBeanName() + ",  " + beanInfoSimples.get(i).getUsage() + MyApplication.weightUnit);
            tvBeanInfo.setEllipsize(TextUtils.TruncateAt.END);
            tvBeanInfo.setSingleLine();
            tvBeanInfo.setMaxLines(1);
            tvBeanInfo.setLines(1);
            simpleBeanInfos.add(tvBeanInfo);
        }
        return simpleBeanInfos;
    }

    private void showSimpleBeanInfo2Fragment() {
        beanInfoBoard.removeAllViews();
        List<TextView> textViews = generateSimpleBeanInfo(beanInfos);
        // 最多就显示三列数据
        for (int i = 0; i < (textViews.size() > 3 ? 3 : textViews.size()); i++) {
            beanInfoBoard.addView(textViews.get(i));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
