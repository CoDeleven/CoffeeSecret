package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;
import com.dhy.coffeesecret.ui.mine.BluetoothListActivity;
import com.dhy.coffeesecret.utils.BluetoothHelper;
import com.dhy.coffeesecret.utils.FragmentTool;

import java.util.List;

public class DeviceFragment extends Fragment implements BluetoothHelper.DataChangeListener{
    private Button mPrepareBake;
    private boolean hasPrepared = false;
    private List<DialogBeanInfo> dialogBeanInfos;
    private TextView bluetoothStatus;
    private TextView operator;
    private BluetoothHelper mHelper;
    private TextView beanTemp;
    private TextView inwindTemp;
    private TextView outwindTemp;
    private TextView accBeanTemp;
    private TextView accInwindTemp;
    private TextView accOutwindTemp;
    private ImageView accBeanView;
    private ImageView accInwindView;
    private ImageView accOutwindView;
    private int count = 0;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Temprature temprature = (Temprature)bundle.getSerializable("temprature");
            beanTemp.setText(String.format("%1$.2f", temprature.getBeanTemp()));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        init(view);
        switchStatus();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mHelper == null){
            mHelper = BluetoothHelper.getNewInstance(getActivity().getApplicationContext());
            mHelper.setDataListener(this);
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
        });
        FragmentTool.getFragmentToolInstance(getContext()).showDialogFragmen("dialogFragment", dialogFragment);
    }

    private void init(View view) {
        mPrepareBake = (Button) view.findViewById(R.id.id_device_prepare_bake);
        bluetoothStatus = (TextView) view.findViewById(R.id.bluetooth_status);
        operator = (TextView) view.findViewById(R.id.bluetooth_operator);
        beanTemp = (TextView) view.findViewById(R.id.id_bake_beanTemp);
        inwindTemp = (TextView) view.findViewById(R.id.id_bake_inwindTemp);
        outwindTemp = (TextView) view.findViewById(R.id.id_bake_outwindTemp);
        accBeanTemp = (TextView)view.findViewById(R.id.id_bake_accBeanTemp);
        accInwindTemp = (TextView)view.findViewById(R.id.id_bake_accInwindTemp);
        accOutwindTemp = (TextView)view.findViewById(R.id.id_bake_accOutwindTemp);
        accBeanView = (ImageView)view.findViewById(R.id.id_bake_accBeanView);
        accInwindView = (ImageView)view.findViewById(R.id.id_bake_accInwindView);
        accOutwindView = (ImageView)view.findViewById(R.id.id_bake_accOutwindView);

        operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BluetoothListActivity.class);
                startActivityForResult(intent, 9);
                Log.e("codelevex", "切换蓝牙连接状态按钮启动");
            }
        });
    }

    private void switchStatus() {
        if (hasPrepared) {
            mPrepareBake.setText("开始烘焙");
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), BakeActivity.class);
                    startActivity(intent);
                    mHelper.setDataListener(null);
                    Log.e("codelevex", "卧槽，开始烘焙");
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
        message.setData(bundle);

        mHandler.sendMessage(message);
    }

    private void switchImage(Temprature temprature){
        float t1 = temprature.getAccBeanTemp();
        float t2 = temprature.getAccInwindTemp();
        float t3 = temprature.getAccOutwindTemp();
        if(t1 > 0){
            accBeanView.setImageResource(R.drawable.ic_bake_acc_up_big);
        }else if(t1 < 0){
            accBeanView.setImageResource(R.drawable.ic_bake_acc_down_big);
        }else{
            accBeanView.setImageResource(R.drawable.ic_bake_acc_invariant_big);
        }
        if(t2 > 0){
            accInwindView.setImageResource(R.drawable.ic_bake_acc_up_small);
        }else if(t2 < 0){
            accInwindView.setImageResource(R.drawable.ic_bake_acc_down_small);
        }else{
            accInwindView.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
        if(t3 > 0){
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_up_small);
        }else if(t3 < 0){
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_down_small);
        }else{
            accOutwindView.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
    }
}
