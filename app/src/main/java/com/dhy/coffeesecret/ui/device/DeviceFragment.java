package com.dhy.coffeesecret.ui.device;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.dhy.coffeesecret.ui.mine.HistoryLineActivity;
import com.dhy.coffeesecret.utils.FragmentTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceFragment extends Fragment implements BluetoothService.DeviceChangedListener, BluetoothService.DataChangedListener {
    private BluetoothService.BluetoothOperator mBluetoothOperator;
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

    private float beginTemp;
    private boolean isStart = false;
    private ProgressDialog dialog;
    private float envTemp;
    private ArrayList<Float> referTempratures;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("codelevex", "获取BluetoothService");
            mBluetoothOperator = (BluetoothService.BluetoothOperator) service;
            mBluetoothOperator.setDataChangedListener(DeviceFragment.this);
            mBluetoothOperator.setDeviceChangedListener(DeviceFragment.this);
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
            if (msg.what == 0) {
                dialog = ProgressDialog.show(getContext(), "标题", "加载中，请稍后……");
            } else if (msg.what == 1) {
                if (dialog != null) {
                    dialog.dismiss();
                }
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
            beginTemp = temprature.getBeanTemp();
            envTemp = temprature.getEnvTemp();
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
        if (mBluetoothOperator == null) {
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
        hasPrepared = false;

        if (dialogBeanInfos != null) {
            dialogBeanInfos.clear();
        }
        switchStatus();
        if(mBluetoothOperator != null && mBluetoothOperator.isConnected()){
            mTextHandler.sendEmptyMessage(0);
            if (!BluetoothService.READABLE) {
                mBluetoothOperator.setDataChangedListener(DeviceFragment.this);
                mBluetoothOperator.setDeviceChangedListener(DeviceFragment.this);
                BluetoothService.READABLE = true;
                mBluetoothOperator.read();
            }
        }else{
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mShowHandler.sendEmptyMessage(0);
                            while (true) {
                                if (isStart) {
                                    Intent intent = new Intent(getContext(), BakeActivity.class);
                                    intent.putExtra(BakeActivity.RAW_BEAN_INFO, dialogBeanInfos.toArray());
                                    intent.putExtra(BakeActivity.DEVICE_NAME, mBluetoothOperator.getCurDeviceName());
                                    intent.putExtra(BakeActivity.START_TEMP, beginTemp);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                    intent.putExtra(BakeActivity.BAKE_DATE, format.format(new Date()));
                                    intent.putExtra(BakeActivity.ENV_TEMP, envTemp);
                                    if(referTempratures != null){
                                        intent.putExtra(BakeActivity.ENABLE_REFERLINE, referTempratures);
                                    }
                                    mShowHandler.sendEmptyMessage(1);
                                    startActivity(intent);
                                    mBluetoothOperator.setDataChangedListener(null);
                                    Log.e("codelevex", "卧槽，开始烘焙");
                                    break;
                                }
                            }
                        }
                    }).start();
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
    public void notifyDeviceConnectStatus(boolean isConnected) {
        if (isConnected) {
            mTextHandler.sendEmptyMessage(0);
        } else {
            mTextHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public void notifyNewDevice(BluetoothDevice device) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
