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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

// TODO 待重构,重构对象-> 统一符号处理、统一handler处理、统一dialog的生成方式

public class DeviceFragment extends Fragment implements IDeviceView {

    public static final int MANUAL = 2;
    public static final int AUTOMATIC = 3;
    public static final int AUTO_CONNECTION_TIPS = 0x100;
    private static String lastAddress = null;
    @Bind(R.id.id_device_prepare_bake)
    Button mPrepareBake;
    boolean hasPrepared = false;
    // List<DialogBeanInfo> dialogBeanInfos;
    List<BeanInfoSimple> beanInfos;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.bluetooth_status)
    TextView bluetoothStatus;
    @Bind(R.id.bluetooth_operator)
    TextView operator;
    @Bind(R.id.id_bake_beanTemp)
    TextView beanTemp;
    @Bind(R.id.id_bake_inwindTemp)
    TextView inwindTemp;
    @Bind(R.id.id_bake_outwindTemp)
    TextView outwindTemp;
    @Bind(R.id.id_bake_accBeanTemp)
    TextView accBeanTemp;
    @Bind(R.id.id_bake_accInwindTemp)
    TextView accInwindTemp;
    @Bind(R.id.id_bake_accOutwindTemp)
    TextView accOutwindTemp;
    @Bind(R.id.id_bake_accBeanView)
    ImageView accBeanView;
    @Bind(R.id.id_bake_accInwindView)
    ImageView accInwindView;
    @Bind(R.id.id_bake_accOutwindView)
    ImageView accOutwindView;
    @Bind(R.id.id_rerange_bean)
    Button rerangeBean;
    @Bind(R.id.id_arcprogress_bean)
    ArcProgress tempratureNeedConvert1;
    @Bind(R.id.id_arcprogress_inwind)
    ArcProgress tempratureNeedConvert2;
    @Bind(R.id.id_arcprogress_outwind)
    ArcProgress tempratureNeedConvert3;
    @Bind(R.id.id_bake_accBeanUnit)
    TextView accBeanUnit;
    @Bind(R.id.id_bake_accinwindUnit)
    TextView accInwindunit;
    @Bind(R.id.id_bake_accoutwindUnit)
    TextView accOutwindUnit;
/*    @Bind(R.id.id_device_mode)
    TextView mode;*/

    private float beginTemp;
    private boolean isStart = false;
    // private int curMode = MANUAL;
    private float envTemp;
    private static Presenter4Device mPresenter;
    private BakeReport referTempratures;
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

    private Handler mToastHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case AUTO_CONNECTION_TIPS:
                    T.showShort(getContext(), (String)msg.obj);
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
                    operator.setText("切换");
                    break;
                case 1:
                    bluetoothStatus.setText(" 未连接");
                    operator.setText("连接");
                    break;
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
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Temperature temperature = (Temperature) msg.obj;
            beanTemp.setText(Utils.getCrspTempratureValue(String.format("%1$.2f", temperature.getBeanTemp())) + MyApplication.tempratureUnit);
            // 随时保存开始烘焙的温度
            beginTemp = Utils.get2PrecisionFloat(temperature.getBeanTemp());
            envTemp = Utils.get2PrecisionFloat(temperature.getEnvTemp());
            inwindTemp.setText(Utils.getCrspTempratureValue(temperature.getInwindTemp() + "") + MyApplication.tempratureUnit);
            outwindTemp.setText(Utils.getCrspTempratureValue(temperature.getOutwindTemp() + "") + MyApplication.tempratureUnit);
            accBeanTemp.setText(Utils.getCrspTempratureValue(temperature.getAccBeanTemp() + "") + "");
            accInwindTemp.setText(Utils.getCrspTempratureValue(temperature.getAccInwindTemp() + "") + "");
            accOutwindTemp.setText(Utils.getCrspTempratureValue(temperature.getAccOutwindTemp() + "") + "");

            switchImage(temperature);

            return false;
        }
    });

    @Override
    public void updateTemperatureView(Temperature temperature) {
        Message msg = new Message();
        msg.obj = temperature;
        mHandler.sendMessage(msg);
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
        init();
        switchStatus();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*// 清空上一次的beanInfo
        if (beanInfos != null) {
            beanInfos.clear();
        }*/
        // 根据是否准备，更换按钮事件
        switchStatus();
        setupPresenter();
        // 转化单位
        convertUnit();
    }

    /**
     * 安装presenter
     */
    private void setupPresenter(){
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
                hasPrepared = true;
                mPrepareBake.setText("开始烘焙");
                switchStatus();
            }

            @Override
            public void setTempratures(BakeReport tempratures) {
                DeviceFragment.this.referTempratures = tempratures;
            }
        });
        FragmentTool.getFragmentToolInstance(getContext()).showDialogFragmen("dialogFragment", dialogFragment);
    }

    private void init() {


        titleText.setText("烘焙");

        operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BluetoothListActivity.class);
                startActivityForResult(intent, 9);
            }
        });
    }

    private void switchStatus() {
        if (hasPrepared) {
            mPrepareBake.setText("开始烘焙");
            // TODO 省赛
            rerangeBean.setVisibility(View.VISIBLE);
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter == null || !mPresenter.isConnected()) {
                        mShowHandler.sendEmptyMessage(0);
                        return;
                    }
                    if (!(beanInfos.size() > 0)) {
                        mShowHandler.sendEmptyMessage(1);
                        return;
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
                    if (referTempratures != null) {
                        intent.putExtra(BakeActivity.ENABLE_REFERLINE, referTempratures);
                    }

                    rerangeBean.setVisibility(View.INVISIBLE);
                    startActivity(intent);
                    mPresenter.destroyBluetoothListener();
                    hasPrepared = false;
                }
            });
        } else {
            rerangeBean.setVisibility(View.INVISIBLE);
            mPrepareBake.setText("准备烘焙");
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 方便测试烘焙过程界面, 暂时隐藏
                    showDialogFragment();
                }
            });
        }
    }

    @OnClick(R.id.id_rerange_bean)
    public void onRerange(View view) {
        showDialogFragment();
    }


    private void switchImage(Temperature temperature) {
        float t1 = temperature.getAccBeanTemp();
        float t2 = temperature.getAccInwindTemp();
        float t3 = temperature.getAccOutwindTemp();
        if (t1 > 0) {
            accBeanView.setImageResource(R.drawable.ic_bake_acc_up_big);
        } else if (t1 < 0) {
            accBeanView.setImageResource(R.drawable.ic_bake_acc_down_big);
        } else {
            accBeanView.setImageResource(R.drawable.ic_bake_acc_invariant_big);
        }
        if (t2 > 0) {
            accInwindView.setImageResource(R.drawable.ic_bake_acc_up_small);
        } else if (t2 < 0) {
            accInwindView.setImageResource(R.drawable.ic_bake_acc_down_small);
        } else {
            accInwindView.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
        if (t3 > 0) {
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_up_small);
        } else if (t3 < 0) {
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_down_small);
        } else {
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
    }

    private void convertUnit() {
        String tempratureUnit = MyApplication.tempratureUnit;

        accBeanUnit.setText(tempratureUnit + "/m");
        accInwindunit.setText(tempratureUnit + "/m");
        accOutwindUnit.setText(tempratureUnit + "/m");
    }

    @Override
    public void updateText(int index, String updateContent) {
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
}
