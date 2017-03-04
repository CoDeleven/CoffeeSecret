package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothAdapter;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.BluetoothHelper;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothHelper.DeviceChangeListener,
        BluetoothHelper.ViewHandlerListener, AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static BluetoothDevice curDevice = null;
    @Bind(R.id.id_connecting_bluetooth_list)
    ListView canConnectDevice;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    private BluetoothHelper mHelper;
    private BaseAdapter mAdapter = null;
    private ProgressBar progressCircle = null;
    ImageView tick = null;
    private Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();
    private List<BluetoothDevice> canConnectDeviceValues = new ArrayList<>();
    private BluetoothDevice temp;

    public BluetoothListActivity() {
        // 获取helper实例
        mHelper = BluetoothHelper.getNewInstance();
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
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.machine.setText(((BluetoothDevice) getItem(viewHolder.position)).getName());

                if(curDevice != null && canConnectDeviceValues.get(viewHolder.position) == curDevice){
                    Log.e("codelevex", "已经打勾了");
                    convertView.findViewById(R.id.id_bluetooth_list_right).setVisibility(View.VISIBLE);
                }
                return convertView;
            }
        };
    }

    @OnClick(R.id.id_back)
    public void back(){
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        enableBluetooth(isChecked);
    }

    public void enableBluetooth(boolean isEnable) {
        Log.e("codelevex", "enableBluetooth");
        if (mHelper.getmAdapter().getState() == BluetoothAdapter.STATE_OFF && isEnable) {
            mHelper.getmAdapter().enable();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mHelper.getEnableStatus()) {
                        mHelper.getmAdapter().enable();
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHelper.scanBluetoothDevice();
                }
            }).start();
        } else {
            canConnectDeviceMap.clear();
            canConnectDeviceValues.clear();
            mAdapter.notifyDataSetChanged();
            canConnectDevice.invalidate();

            mHelper.close();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHelper.close();
                    Log.e("codelevex", "关闭状态：" + mHelper.getmAdapter().disable());
                }
            }).start();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = BluetoothHelper.getNewInstance();
        }
        mHelper.setDeviceListener(this);
        mHelper.setViewHandlerListener(this);
        if (mHelper.getEnableStatus()) {
            switchButton.setChecked(true);
            mHelper.scanBluetoothDevice();
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
        setListViewHeightBasedOnChildren(canConnectDevice);
        switchButton.setOnCheckedChangeListener(null);

    }


    @Override
    public void notifyNewDevice(BluetoothDevice device) {
        if (!canConnectDeviceMap.containsKey(device.getAddress())) {
            Log.e("codelevex", "我activity的实现:Device" + device.getName());
            canConnectDeviceValues.add(device);
            canConnectDeviceMap.put(device.getAddress(), device);
            refreshListView();
        }
    }

    @Override
    public void handleViewBeforeStartRead() {
        progressCircle.setVisibility(View.GONE);
        tick.setVisibility(View.VISIBLE);
        curDevice = temp;

        finish();
    }

    public void refreshListView() {
        mAdapter.notifyDataSetChanged();
        canConnectDevice.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView)view.findViewById(R.id.id_bluetooth_list_right);

        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
        if (!mHelper.connectDevice(device)) {
            Log.e("codelevex", "连接失败");
            return;
        }
        temp = device;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = mAdapter;
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.stopScanBluetoothDevice();
    }

    class ViewHolder {
        int position;
        TextView machine;
    }
}
