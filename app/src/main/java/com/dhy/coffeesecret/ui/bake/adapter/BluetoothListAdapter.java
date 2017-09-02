package com.dhy.coffeesecret.ui.bake.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jesse.nativelogger.NLogger;

/**
 * Created by CoDeleven on 17-3-7.
 */

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder> {
    private static final String TAG = BluetoothListAdapter.class.getSimpleName();
    private Context context;
    private BluetoothDevice mCurConnectedDevice;
    private LayoutInflater layoutInflater;
    private List<ScanResult> devices = new ArrayList<>();
    private Map<String, ImageView> rssiTextView = new HashMap<>();
    private Map<String, Integer> rssiMac = new HashMap<>();
    private OnItemClickListener onItemClickListener;
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private int curIndex;
    private BluetoothViewHolder mCurConnectedHolder;
    public BluetoothListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        /*if(devices != null && devices.size() == 0){
            if(mCurConnectedDevice != null){
                return 1;
            }
        }else{
            return devices.size();
        }
        return 0;*/
        return devices.size();
    }

    public void missConnected(){
        mCurConnectedDevice = null;
        devices.remove(mCurConnectedDevice);
        devices.clear();
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BluetoothViewHolder(layoutInflater.inflate(R.layout.bluetooth_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(final BluetoothViewHolder holder, final int position) {
        final ScanResult result = devices.get(position);
        final BluetoothDevice device = devices.get(position).getDevice();
        /*
            因ListActivity重新生成，所以判断上一次因连接成功关闭activity时，所设置地址是否和本次地址一致，true则直接默认该设备tick可见
          */
        NLogger.i(TAG, "绑定新的holder：targetDevice:" + device + ", mCurConnectedDevice:" + mCurConnectedDevice);
        if (mCurConnectedDevice != null && device != null && mCurConnectedDevice.getAddress().equals(device.getAddress())) {
            mCurConnectedHolder = holder;
            mCurConnectedHolder.layout.setEnabled(false);
            // 默认设置tick可见
            holder.tick.setVisibility(View.VISIBLE);
        }else{
            // 默认设置tick不可见
            holder.tick.setVisibility(View.GONE);
            holder.layout.setEnabled(true);
        }
        holder.machine.setText(device.getName());
        rssiTextView.put(device.getAddress(), holder.rssi);
        // 从map中获取rssi的值
        Integer rssiValue = rssiMac.get(device.getAddress());
        // 如果rssiValue不为空，则重新设置rssi的文本
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curIndex = position;
                onItemClickListener.onItemClick(result, holder.layout);
            }
        });
    }

    public void setRssi(String macAddr, int rssi) {
        rssiMac.put(macAddr, rssi);
    }

    public void addDevice(ScanResult device) {
        devices.add(device);
        notifyDataSetChanged();
    }

    /**
     * 用于设置当前连接着的设备
     * @param connectedDevice
     */
    public void setConnectedDevice(BluetoothDevice connectedDevice){
        this.mCurConnectedDevice = connectedDevice;
        // devices.add(new ScanResult(mCurConnectedDevice, -1, new byte[]{}, System.currentTimeMillis()));
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void clearDevices() {
        devices.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(ScanResult device, View view);
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {
        private ImageView rssi;
        private TextView machine;
        private View layout;
        private ProgressBar progressCircle;
        private ImageView tick;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            this.layout = itemView;
            machine = (TextView) itemView.findViewById(R.id.text_name);
            // rssi = (ImageView) itemView.findViewById(R.id.id_device_rssi);
            progressCircle = (ProgressBar) itemView.findViewById(R.id.circle_progress);
            tick = (ImageView) itemView.findViewById(R.id.id_bluetooth_list_right);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public void setDevices(Collection<? extends ScanResult> results){
        devices.clear();
        devices.addAll(results);
    }
    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }


    public View getCurConnectedTick(){
        if(mCurConnectedHolder != null){
            return mCurConnectedHolder.tick;
        }
        return null;
    }

    public View getCorConnectedLayout(){
        if(mCurConnectedHolder != null){
            return mCurConnectedHolder.layout;
        }
        return null;
    }
}