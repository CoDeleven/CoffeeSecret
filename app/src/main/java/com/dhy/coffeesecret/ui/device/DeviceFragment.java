package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;
import com.dhy.coffeesecret.ui.mine.BluetoothListActivity;

import java.util.List;

public class DeviceFragment extends Fragment {
    private Button mPrepareBake;
    private boolean hasPrepared = false;
    private List<DialogBeanInfo> dialogBeanInfos;
    private TextView bluetoothStatus;
    private TextView operator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        init(view);
        switchStatus();

        return view;
    }

    private void showDialogFragment() {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("dialogFragment");
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
            mPrepareBake.setText("开始烘焙");
        }
        final BakeDialog dialogFragment = new BakeDialog();
        dialogFragment.setBeanInfosListener(new BakeDialog.OnBeaninfosConfirmListener() {
            @Override
            public void setBeanInfos(List<DialogBeanInfo> beanInfos) {
                dialogBeanInfos = beanInfos;
                hasPrepared = true;
                switchStatus();
            }
        });
        //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
        dialogFragment.show(mFragTransaction, "dialogFragment");
    }

    private void init(View view) {
        mPrepareBake = (Button) view.findViewById(R.id.id_device_prepare_bake);
        bluetoothStatus = (TextView) view.findViewById(R.id.bluetooth_status);
        operator = (TextView) view.findViewById(R.id.bluetooth_operator);
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
                    Log.e("codelevex", "卧槽，开始烘焙");
                }
            });
        } else {
            mPrepareBake.setText("准备烘焙");
            mPrepareBake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("codelevex", "卧槽，准备烘焙");
                    showDialogFragment();
                }
            });
        }
    }

}
