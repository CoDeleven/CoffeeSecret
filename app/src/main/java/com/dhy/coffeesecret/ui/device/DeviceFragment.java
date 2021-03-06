package com.dhy.coffeesecret.ui.device;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;
import com.dhy.coffeesecret.ui.mine.BluetoothListActivity;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment implements BluetoothService.DeviceChangedListener, BluetoothService.DataChangedListener {
    @Bind(R.id.id_device_prepare_bake)
    Button mPrepareBake;
    boolean hasPrepared = false;
    List<DialogBeanInfo> dialogBeanInfos;
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
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private float beginTemp;
    private boolean isStart = false;
    private float envTemp;
    private ArrayList<Float> referTempratures;
    private static String lastAddress = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取蓝牙服务
            mBluetoothOperator = (BluetoothService.BluetoothOperator) service;
            mBluetoothOperator.setDataChangedListener(DeviceFragment.this);
            mBluetoothOperator.setDeviceChangedListener(DeviceFragment.this);


            // 如果不为空，则尝试直接连接该蓝牙
            if (!"".equals(lastAddress)) {
                T.showShort(getContext(), "正在搜索上一次设备:" + lastAddress + "...");
                mBluetoothOperator.startScanDevice();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
                case 2:
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            dialogBuilder.setMessage("重连失败，请检查蓝牙设备");
                            dialogBuilder.setPositiveButton("手动连接", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 去连接界面
                                    dialog.dismiss();
                                }
                            });
                            dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            Looper.prepare();
                            dialogBuilder.show();
                        }
                    }, 5000);
                    mBluetoothOperator.reConnect();
                    progressDialog.setMessage("正在尝试重连...请稍后...");
                    progressDialog.show();
                    break;
                case 3:

            }
            return false;
        }
    });
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Temprature temprature = (Temprature) bundle.getSerializable("temprature");
            beanTemp.setText(String.format("%1$.2f", temprature.getBeanTemp()));
            // 随时保存开始烘焙的温度
            beginTemp = (temprature.getBeanTemp() * 100) / 100.f;
            envTemp = (temprature.getEnvTemp() * 100) / 100.f;
            inwindTemp.setText(String.format("%1$.2f", temprature.getInwindTemp()));
            outwindTemp.setText(String.format("%1$.2f", temprature.getOutwindTemp()));
            accBeanTemp.setText(String.format("%1$.2f", temprature.getAccBeanTemp()));
            accInwindTemp.setText(String.format("%1$.2f", temprature.getAccInwindTemp()));
            accOutwindTemp.setText(String.format("%1$.2f", temprature.getAccOutwindTemp()));

            switchImage(temprature);

            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastAddress = SettingTool.getConfig(getContext()).getAddress();
        // 获取上一次连接的蓝牙设备地址
        if (BluetoothService.BLUETOOTH_OPERATOR == null) {
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
        // 默认每次重启不准备
        hasPrepared = false;
        // 清空上一次的beanInfo
        if (dialogBeanInfos != null) {
            dialogBeanInfos.clear();
        }
        // 根据是否准备，更换按钮事件
        switchStatus();
        // 如果连接
        if (BluetoothService.BLUETOOTH_OPERATOR != null && BluetoothService.BLUETOOTH_OPERATOR.isConnected()) {
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
            // 更改已连接的视图
            mTextHandler.sendEmptyMessage(0);
            // 重新设置回调接口到本对象
            mBluetoothOperator.setDataChangedListener(DeviceFragment.this);
            mBluetoothOperator.setDeviceChangedListener(DeviceFragment.this);
        } else {
            mTextHandler.sendEmptyMessage(1);
        }


    }

    private void showDialogFragment() {
        final BakeDialog dialogFragment = new BakeDialog();
        dialogFragment.setBeanInfosListener(new BakeDialog.OnBeaninfosConfirmListener() {
            @Override
            public void setBeanInfos(List<DialogBeanInfo> beanInfos) {
                dialogBeanInfos = beanInfos;
                hasPrepared = true;
                mPrepareBake.setText("开始烘焙");
                switchStatus();
            }

            @Override
            public void setTempratures(ArrayList<Float> tempratures) {
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
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBluetoothOperator.isConnected()) {
                        mShowHandler.sendEmptyMessage(0);
                        return;
                    }
                    if (!(dialogBeanInfos.size() > 0)) {
                        mShowHandler.sendEmptyMessage(1);
                        return;
                    }
                    Intent intent = new Intent(getContext(), BakeActivity.class);
                    intent.putExtra(BakeActivity.RAW_BEAN_INFO, dialogBeanInfos.toArray());
                    intent.putExtra(BakeActivity.DEVICE_NAME, mBluetoothOperator.getCurDeviceName());
                    intent.putExtra(BakeActivity.START_TEMP, beginTemp);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    intent.putExtra(BakeActivity.BAKE_DATE, format.format(new Date()));
                    intent.putExtra(BakeActivity.ENV_TEMP, envTemp);
                    if (referTempratures != null) {
                        intent.putExtra(BakeActivity.ENABLE_REFERLINE, referTempratures);
                    }
                    startActivity(intent);
                    mBluetoothOperator.setDataChangedListener(null);
                }
            });
        } else {
            mPrepareBake.setText("准备烘焙");
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 方便测试烘焙过程界面, 暂时隐藏
                    Log.e("codelevex", "卧槽，准备烘焙");
                    showDialogFragment();
                }
            });
        }
    }

    @Override
    public void notifyDataChanged(Temprature temprature) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("temprature", temprature);
        if (!isStart) {
            beginTemp = temprature.getBeanTemp();
            isStart = true;
        }
        message.setData(bundle);

        mHandler.sendMessage(message);

    }

    private void switchImage(Temprature temprature) {
        float t1 = temprature.getAccBeanTemp();
        float t2 = temprature.getAccInwindTemp();
        float t3 = temprature.getAccOutwindTemp();
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

    @Override
    public void notifyDeviceConnectStatus(boolean isConnected, BluetoothDevice device) {
        if (isConnected) {
            mTextHandler.sendEmptyMessage(0);
        } else {
            mTextHandler.sendEmptyMessage(1);
            mShowHandler.sendEmptyMessage(2);
        }
    }

    @Override
    public void notifyNewDevice(BluetoothDevice device, int rssi) {
        if(lastAddress.equals(device.getAddress())){
            T.showShort(getContext(), "正在尝试自动连接...");
            mBluetoothOperator.connect(device);
            mBluetoothOperator.stopScanDevice();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
