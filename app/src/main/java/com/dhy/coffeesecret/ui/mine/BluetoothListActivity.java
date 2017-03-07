package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.services.BluetoothService;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, BluetoothService.DeviceChangedListener, BluetoothService.ViewControllerListener {
    private static BluetoothDevice curDevice = null;
    private static Queue<Integer> rssiQueue = new ArrayBlockingQueue<Integer>(10);
    @Bind(R.id.id_connecting_bluetooth_list)
    ListView canConnectDevice;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    ImageView tick = null;
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private BaseAdapter mAdapter = null;
    private ProgressBar progressCircle = null;
    private Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();
    private List<BluetoothDevice> canConnectDeviceValues = new ArrayList<>();
    private BluetoothDevice temp;
    private View curView;

    public BluetoothListActivity() {


        // 生成适配器
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return canConnectDeviceValues.size();
            }

            @Override
            public Object getItem(int position) {
                return canConnectDeviceValues.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.bluetooth_item_view, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.machine = (TextView) convertView.findViewById(R.id.text_name);
                    viewHolder.position = position;
                    viewHolder.rssi = (TextView) convertView.findViewById(R.id.id_device_rssi);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                BluetoothDevice device = (BluetoothDevice) getItem(viewHolder.position);
                viewHolder.machine.setText(device.getName());
                viewHolder.rssi.setText(rssiQueue.poll() + "");
                if (curDevice != null && canConnectDeviceValues.get(viewHolder.position) == curDevice) {
                    convertView.findViewById(R.id.id_bluetooth_list_right).setVisibility(View.VISIBLE);
                }
                return convertView;
            }
        };
    }

    @OnClick(R.id.id_back)
    public void back() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        enableBluetooth(isChecked);
    }

    public void enableBluetooth(boolean isEnable) {
        Log.d("codelevex", "enableBluetooth");
        if (!mBluetoothOperator.isEnable() && isEnable) {
            mBluetoothOperator.enable();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mBluetoothOperator.isEnable()) {
                        mBluetoothOperator.enable();
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mBluetoothOperator.startScanDevice();
                }
            }).start();
        } else {
            canConnectDeviceMap.clear();
            canConnectDeviceValues.clear();
            mAdapter.notifyDataSetChanged();
            canConnectDevice.invalidate();

            mBluetoothOperator.disableBluetooth();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothOperator == null) {
            // 获取helper实例
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
        }
        mBluetoothOperator.setDeviceChangedListener(this);
        mBluetoothOperator.setViewControllerListener(this);
        if (mBluetoothOperator.isEnable()) {
            switchButton.setChecked(true);
            mBluetoothOperator.startScanDevice();
        }
        switchButton.setOnCheckedChangeListener(this);

        if (curDevice != null) {
            canConnectDeviceMap.put(curDevice.getAddress(), curDevice);
            canConnectDeviceValues.add(curDevice);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        ButterKnife.bind(this);
        canConnectDevice.setAdapter(mAdapter);
        canConnectDevice.setOnItemClickListener(this);
        switchButton.setOnCheckedChangeListener(null);

    }


    @Override
    public void notifyDeviceConnectStatus(boolean isConnected) {

    }

    @Override
    public void notifyNewDevice(BluetoothDevice device, int rssi) {
        if (!canConnectDeviceMap.containsKey(device.getAddress())) {
            canConnectDeviceValues.add(device);
            canConnectDeviceMap.put(device.getAddress(), device);
            rssiQueue.add(rssi);
            refreshListView();
        }
    }

    @Override
    public void handleViewBeforeStartRead() {
        progressCircle.setVisibility(View.GONE);
        tick.setVisibility(View.VISIBLE);
        curDevice = temp;
        temp = null;
        finish();
    }

    public void refreshListView() {
        mAdapter.notifyDataSetChanged();
        canConnectDevice.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ((temp != null || curDevice != null) && curView != null) {
            curView.findViewById(R.id.circle_progress).setVisibility(View.GONE);
        }
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView) view.findViewById(R.id.id_bluetooth_list_right);

        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
        if (!mBluetoothOperator.connect(device)) {
            return;
        }
        temp = device;
        curView = view;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothOperator.stopScanDevice();
    }

    class ViewHolder {
        int position;
        TextView rssi;
        TextView machine;
    }
}
